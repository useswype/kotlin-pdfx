package com.swypex.pdfx

import java.io.File

@DslMarker annotation class PdfDsl

@PdfDsl
class DocumentBuilder {
    internal val pages = mutableListOf<PageSpec>()
    var fonts: FontFamily? = null

    @JvmName("page") fun javaPage(block: PageBuilder.() -> Unit) = page(block = block)

    fun page(
        size: PageSize = PageSize.A4,
        margin: Dimension = 40.pt,
        margins: Insets = Insets(margin),
        block: PageBuilder.() -> Unit,
    ) {
        val p = PageBuilder().apply(block)
        pages +=
            PageSpec(
                size,
                margins,
                p.nodes.toList(),
                p.headerNodes,
                p.firstPageHeaderNodes,
                p.footerNodes,
            )
    }
}

@PdfDsl
open class ComponentBuilder internal constructor() {
    internal val nodes = mutableListOf<Node>()

    fun text(value: String, style: Style = EmptyStyle) {
        nodes += TextNode(value, style.resolve())
    }

    fun text(style: Style = EmptyStyle, content: () -> String) {
        text(content(), style)
    }

    fun link(value: String, href: String, style: Style = TextColor(Color.Blue)) {
        nodes += LinkNode(value, href, style.resolve(StyleValues(color = Color.Blue)))
    }

    /** Displays the current document page number in a repeating header or footer. */
    fun pageNumber(style: Style = EmptyStyle, prefix: String = "Page ") {
        nodes += PageNumberNode(style.resolve(), prefix, false)
    }

    fun pageNumber(style: Style = EmptyStyle, prefix: String = "Page ", includeTotal: Boolean) {
        nodes += PageNumberNode(style.resolve(), prefix, includeTotal)
    }

    fun heading(value: String, level: Int = 1, style: Style = EmptyStyle) {
        require(level in 1..3)
        val defaults =
            StyleValues(
                size =
                    when (level) {
                        1 -> 24.pt
                        2 -> 16.pt
                        else -> 12.pt
                    },
                bold = true,
                lineHeight = 1.2f,
            )
        nodes += TextNode(value, style.resolve(defaults))
    }

    fun heading(level: Int = 1, style: Style = EmptyStyle, content: () -> String) {
        heading(content(), level, style)
    }

    fun divider(color: Color = Color.Slate200, thickness: Dimension = 1.pt) {
        nodes += Rule(color, thickness)
    }

    fun spacer(height: Dimension) {
        nodes += Space(height)
    }

    fun image(file: File, width: Dimension, height: Dimension, fit: ImageFit = ImageFit.Contain) {
        nodes += ImageNode(file, null, width, height, fit)
    }

    fun image(
        bytes: ByteArray,
        width: Dimension,
        height: Dimension,
        fit: ImageFit = ImageFit.Contain,
    ) {
        nodes += ImageNode(null, bytes.copyOf(), width, height, fit)
    }

    fun table(style: Style = EmptyStyle, block: TableBuilder.() -> Unit) {
        val resolved = style.resolve(StyleValues(padding = Insets(8.pt)))
        val table = TableBuilder().apply(block)
        require(
            table.headers.isNotEmpty() && table.rows.all { it.cells.size == table.headers.size }
        ) {
            "Every table row must match the non-empty header"
        }
        require(table.columnWidths == null || table.columnWidths!!.size == table.headers.size) {
            "Column widths must match the number of headers"
        }
        require(table.columnStyles.keys.all { it in table.headers.indices }) {
            "Column style index is out of range"
        }
        nodes +=
            TableNode(
                table.headers,
                table.rows.toList(),
                table.columnWidths,
                table.columnStyles.toMap(),
                table.borders,
                resolved.padding,
                resolved.border() ?: Border(),
                resolved.cornerRadius,
                table.headerStyle,
            )
    }

    // A layout container assigns width or weight independently of its contents.
    fun container(
        width: Dimension? = null,
        weight: Float = 0f,
        block: ComponentBuilder.() -> Unit,
    ) = styledContainer(false, EmptyStyle, 0.pt, Alignment.Start, width, weight, block)

    fun column(
        style: Style = EmptyStyle,
        alignment: Alignment = Alignment.Start,
        block: ComponentBuilder.() -> Unit,
    ) = styledContainer(false, style, 0.pt, alignment, null, 0f, block)

    fun row(
        style: Style = EmptyStyle,
        alignment: Alignment = Alignment.Start,
        block: ComponentBuilder.() -> Unit,
    ) = styledContainer(true, style, 0.pt, alignment, null, 0f, block)

    fun card(
        style: Style = EmptyStyle,
        alignment: Alignment = Alignment.Start,
        block: ComponentBuilder.() -> Unit,
    ) = styledContainer(false, style, 4.pt, alignment, null, 0f, block)

    private fun styledContainer(
        horizontal: Boolean,
        style: Style,
        defaultSpacing: Dimension,
        alignment: Alignment,
        width: Dimension?,
        weight: Float,
        block: ComponentBuilder.() -> Unit,
    ) {
        val resolved = style.resolve(StyleValues(spacing = defaultSpacing))
        container(
            horizontal,
            resolved.spacing,
            resolved.padding,
            alignment,
            resolved.background,
            resolved.border(),
            resolved.cornerRadius,
            width,
            weight,
            block,
        )
    }

    private fun container(
        horizontal: Boolean,
        spacing: Dimension,
        padding: Insets,
        alignment: Alignment,
        background: Color?,
        border: Border?,
        radius: Dimension,
        width: Dimension?,
        weight: Float,
        block: ComponentBuilder.() -> Unit,
    ) {
        require(weight >= 0 && weight.isFinite())
        val child = ComponentBuilder().apply(block)
        nodes +=
            Container(
                child.nodes.toList(),
                horizontal,
                spacing,
                padding,
                alignment,
                background,
                border,
                radius,
                width,
                weight,
            )
    }
}

enum class TableBorders {
    All,
    Horizontal,
    None,
}

@PdfDsl
class TableBuilder internal constructor() {
    internal var headers: List<String> = emptyList()
    internal var headerStyle: StyleValues = defaultHeaderStyle()
    internal val rows = mutableListOf<TableRow>()
    internal var columnWidths: List<Float>? = null
    internal val columnStyles = mutableMapOf<Int, Style>()
    var borders: TableBorders = TableBorders.All

    fun columnStyle(index: Int, style: Style) {
        require(index >= 0)
        columnStyles[index] = style
    }

    fun columnWidths(vararg widths: Float) {
        require(widths.isNotEmpty() && widths.all { it > 0 && it.isFinite() }) {
            "Column widths must be finite positive values"
        }
        columnWidths = widths.toList()
    }

    fun header(vararg cells: String) {
        headers = cells.toList()
        headerStyle = defaultHeaderStyle()
    }

    fun header(content: () -> List<String>) {
        header(defaultHeaderStyle(), content)
    }

    fun header(style: Style, content: () -> List<String>) {
        headers = content().toList()
        headerStyle = style.resolve(defaultHeaderStyle())
    }

    fun row(vararg cells: String) {
        rows += TableRow(cells.toList(), StyleValues())
    }

    fun row(style: Style, vararg cells: String) {
        rows += TableRow(cells.toList(), style.resolve())
    }

    fun row(style: Style, cellStyles: Map<Int, Style>, vararg cells: String) {
        require(cellStyles.keys.all { it in cells.indices })
        rows += TableRow(cells.toList(), style.resolve(), cellStyles.toMap())
    }
}

@PdfDsl
class PageBuilder internal constructor() : ComponentBuilder() {
    internal var headerNodes: List<Node> = emptyList()
    internal var firstPageHeaderNodes: List<Node>? = null
    internal var footerNodes: List<Node> = emptyList()

    fun header(block: ComponentBuilder.() -> Unit) {
        headerNodes = ComponentBuilder().apply(block).nodes.toList()
    }

    fun firstPageHeader(block: ComponentBuilder.() -> Unit) {
        firstPageHeaderNodes = ComponentBuilder().apply(block).nodes.toList()
    }

    fun footer(marginTop: Dimension = 0.pt, block: ComponentBuilder.() -> Unit) {
        val content = ComponentBuilder().apply(block).nodes.toList()
        footerNodes =
            if (marginTop.points > 0f && content.isNotEmpty()) listOf(Space(marginTop)) + content
            else content
    }
}

private fun defaultHeaderStyle() =
    StyleValues(color = Color.Gray, bold = true, background = Color.Gray100)

private fun StyleValues.border(): Border? =
    if (borderColor == null && borderWidth == null) null
    else Border(borderColor ?: Color.Slate200, borderWidth ?: 1.pt)
