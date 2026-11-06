package com.swypex.pdfx

import java.io.File
import java.util.function.Consumer

// Java adapters use Consumer so builder blocks do not need to return Kotlin Unit.
object Pdf {
    @JvmStatic
    fun create(block: Consumer<JavaDocument>): PdfDocument = pdf {
        block.accept(JavaDocument(this))
    }

    @JvmField val A4 = PageSize.A4
    @JvmField val LETTER = PageSize.Letter

    @JvmStatic fun pageSize(width: Number, height: Number): PageSize = PageSize(width.pt, height.pt)

    @JvmStatic fun style(): JavaStyle = JavaStyle(EmptyStyle)

    @JvmStatic fun color(red: Int, green: Int, blue: Int): Color = Color(red, green, blue)
}

// Each operation returns a new style so reusable styles remain unchanged.
class JavaStyle internal constructor(internal val value: Style) {
    fun size(points: Number) = JavaStyle(value + points.pt)

    fun textColor(color: Color) = JavaStyle(value + TextColor(color))

    fun bold() = JavaStyle(value + Bold)

    fun align(alignment: TextAlign) = JavaStyle(value + alignment)

    fun padding(points: Number) = JavaStyle(value + Padding(points.pt))

    fun padding(top: Number, right: Number, bottom: Number, left: Number) =
        JavaStyle(value + Insets(top.pt, right.pt, bottom.pt, left.pt))

    fun paddingTop(points: Number) = JavaStyle(value + PaddingTop(points.pt))

    fun spacing(points: Number) = JavaStyle(value + Spacing(points.pt))

    fun rounded(points: Number) = JavaStyle(value + Rounded(points.pt))

    fun background(color: Color) = JavaStyle(value + Background(color))

    fun border(color: Color, width: Number) = JavaStyle(value + Border(color, width.pt))

    fun and(other: JavaStyle) = JavaStyle(value + other.value)
}

class JavaDocument internal constructor(private val delegate: DocumentBuilder) {
    fun fonts(regular: File, bold: File) {
        delegate.fonts = FontFamily(regular, bold)
    }

    fun fonts(regular: File) {
        delegate.fonts = FontFamily(regular)
    }

    fun page(block: Consumer<JavaPage>) = page(PageSize.A4, 40f, block)

    fun page(size: PageSize, margin: Number, block: Consumer<JavaPage>) {
        delegate.page(size = size, margin = margin.pt) { block.accept(JavaPage(this)) }
    }

    fun page(width: Number, height: Number, margin: Number, block: Consumer<JavaPage>) =
        page(PageSize(width.pt, height.pt), margin, block)
}

open class JavaComponents internal constructor(protected val delegate: ComponentBuilder) {
    fun text(value: String) = delegate.text(value)

    fun text(value: String, style: JavaStyle) = delegate.text(value, style.value)

    fun heading(value: String) = delegate.heading(value)

    fun heading(value: String, level: Int) = delegate.heading(value, level)

    fun heading(value: String, level: Int, style: JavaStyle) =
        delegate.heading(value, level, style.value)

    fun link(value: String, href: String) = delegate.link(value, href)

    fun link(value: String, href: String, style: JavaStyle) =
        delegate.link(value, href, style.value)

    fun pageNumber() = delegate.pageNumber()

    fun pageNumber(style: JavaStyle, prefix: String) = delegate.pageNumber(style.value, prefix)

    fun divider() = delegate.divider()

    fun divider(color: Color, thickness: Number) = delegate.divider(color, thickness.pt)

    fun spacer(height: Number) = delegate.spacer(height.pt)

    fun image(file: File, width: Number, height: Number) = delegate.image(file, width.pt, height.pt)

    fun image(file: File, width: Number, height: Number, fit: ImageFit) =
        delegate.image(file, width.pt, height.pt, fit)

    fun table(block: Consumer<JavaTable>) = table(Pdf.style(), block)

    fun table(style: JavaStyle, block: Consumer<JavaTable>) {
        delegate.table(style.value) { block.accept(JavaTable(this)) }
    }

    fun container(block: Consumer<JavaComponents>) = container(null, 0f, block)

    fun container(weight: Float, block: Consumer<JavaComponents>) = container(null, weight, block)

    fun containerWithWidth(width: Number, block: Consumer<JavaComponents>) =
        container(width, 0f, block)

    fun container(width: Number?, weight: Float, block: Consumer<JavaComponents>) {
        delegate.container(width?.pt, weight) { block.accept(JavaComponents(this)) }
    }

    fun column(block: Consumer<JavaComponents>) = column(Pdf.style(), block)

    fun column(style: JavaStyle, block: Consumer<JavaComponents>) =
        column(style, Alignment.Start, block)

    fun column(style: JavaStyle, alignment: Alignment, block: Consumer<JavaComponents>) {
        delegate.column(style.value, alignment) { block.accept(JavaComponents(this)) }
    }

    fun row(block: Consumer<JavaComponents>) = row(Pdf.style(), block)

    fun row(style: JavaStyle, block: Consumer<JavaComponents>) = row(style, Alignment.Start, block)

    fun row(style: JavaStyle, alignment: Alignment, block: Consumer<JavaComponents>) {
        delegate.row(style.value, alignment) { block.accept(JavaComponents(this)) }
    }

    fun card(block: Consumer<JavaComponents>) = card(Pdf.style(), block)

    fun card(style: JavaStyle, block: Consumer<JavaComponents>) =
        card(style, Alignment.Start, block)

    fun card(style: JavaStyle, alignment: Alignment, block: Consumer<JavaComponents>) {
        delegate.card(style.value, alignment) { block.accept(JavaComponents(this)) }
    }
}

class JavaPage internal constructor(private val page: PageBuilder) : JavaComponents(page) {
    fun header(block: Consumer<JavaComponents>) {
        page.header { block.accept(JavaComponents(this)) }
    }

    fun footer(block: Consumer<JavaComponents>) = footer(0, block)

    fun footer(marginTop: Number, block: Consumer<JavaComponents>) {
        page.footer(marginTop.pt) { block.accept(JavaComponents(this)) }
    }
}

class JavaTable internal constructor(private val delegate: TableBuilder) {
    fun header(vararg cells: String) = delegate.header(*cells)

    fun header(style: JavaStyle, vararg cells: String) =
        delegate.header(style.value) { cells.toList() }

    fun row(vararg cells: String) = delegate.row(*cells)
}
