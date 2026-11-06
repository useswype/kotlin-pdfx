package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*

internal class OpenPdfComponents(
    val regular: BaseFont,
    val bold: BaseFont,
    val arabicRegular: BaseFont,
    val arabicBold: BaseFont,
)

// A borderless table participates in document flow and can occupy an exact height.
internal fun OpenPdfComponents.spacing(height: Float): PdfPTable =
    PdfPTable(1).apply {
        widthPercentage = 100f
        addCell(
            PdfPCell().apply {
                border = Rectangle.NO_BORDER
                fixedHeight = height
            }
        )
    }

internal fun OpenPdfComponents.vertical(
    nodes: List<Node>,
    spacing: Float,
    availableWidth: Float,
    alignment: Alignment = Alignment.Start,
): PdfPTable =
    PdfPTable(1).apply {
        widthPercentage = 100f
        nodes.forEachIndexed { index, node ->
            if (index > 0 && spacing > 0f) {
                addCell(
                    PdfPCell().apply {
                        border = Rectangle.NO_BORDER
                        fixedHeight = spacing
                    }
                )
            }

            // A normal cell fills its parent, so a locked nested table is needed to
            // align a child whose requested width is smaller than the available width.
            val childWidth = (node as? Container)?.width?.points?.coerceAtMost(availableWidth)
            if (childWidth == null || childWidth == availableWidth) {
                addCell(cell(node, availableWidth))
            } else {
                require(childWidth > 0) { "Container width must be positive" }

                val nested =
                    PdfPTable(1).apply {
                        totalWidth = childWidth
                        isLockedWidth = true
                        horizontalAlignment =
                            when (alignment) {
                                Alignment.Start -> Element.ALIGN_LEFT
                                Alignment.Center -> Element.ALIGN_CENTER
                                Alignment.End -> Element.ALIGN_RIGHT
                            }
                        addCell(cell(node, childWidth))
                    }

                addCell(
                    PdfPCell().apply {
                        border = Rectangle.NO_BORDER
                        setPadding(0f)
                        addElement(nested)
                    }
                )
            }
        }
    }

internal fun OpenPdfComponents.cell(node: Node, availableWidth: Float): PdfPCell =
    PdfPCell().apply {
        // OpenPDF cell defaults would otherwise add spacing and borders to every node.
        border = Rectangle.NO_BORDER
        setPadding(0f)

        when (node) {
            is TextNode -> {
                isUseAscender = true
                isUseDescender = true
                runDirection = node.value.pdfRunDirection()
                paddingTop = node.style.paddingTop.points
                addElement(paragraph(node.value, node.style))
            }
            is LinkNode -> {
                isUseAscender = true
                isUseDescender = true
                runDirection = node.value.pdfRunDirection()
                paddingTop = node.style.paddingTop.points
                addElement(paragraph(node.value, node.style, node.href))
            }
            is Space -> fixedHeight = node.height.points
            is Rule -> {
                fixedHeight = node.thickness.points
                backgroundColor = node.color.awt()
            }
            is ImageNode -> {
                val image = loadImage(node)

                if (node.fit == ImageFit.Contain) {
                    image.scaleToFit(node.width.points, node.height.points)
                } else {
                    image.scaleAbsolute(node.width.points, node.height.points)
                }

                addElement(image)
                // Contained images may be shorter than their box; retain the requested height.
                minimumHeight = node.height.points
            }
            is Container -> {
                // Width calculations must exclude padding or nested content can overflow.
                val innerWidth =
                    availableWidth - node.padding.left.points - node.padding.right.points

                addElement(
                    if (node.horizontal) {
                        horizontal(node, innerWidth)
                    } else {
                        vertical(node.children, node.spacing.points, innerWidth, node.alignment)
                    }
                )

                paddingTop = node.padding.top.points
                paddingRight = node.padding.right.points
                paddingBottom = node.padding.bottom.points
                paddingLeft = node.padding.left.points

                if (node.background != null || node.border != null) {
                    cellEvent = containerBackgroundAndBorder(node)
                }
            }
            is PageNumberNode -> error("pageNumber is only supported in a page header or footer")
            is TableNode ->
                error("Tables must be direct page children or inside an unstyled column")
        }
    }

internal fun OpenPdfComponents.horizontal(node: Container, availableWidth: Float): PdfPTable {
    // OpenPDF tables require at least one column, even for an empty row.
    if (node.children.isEmpty()) {
        return PdfPTable(1).apply { addCell(PdfPCell().apply { border = Rectangle.NO_BORDER }) }
    }

    // Fixed widths and gaps take priority; flexible children share only what remains.
    val gaps = node.spacing.points * (node.children.size - 1)
    val fixed =
        node.children.sumOf { ((it as? Container)?.width?.points ?: 0f).toDouble() }.toFloat()

    require(availableWidth > 0f) { "Row has no available width" }
    require(
        node.children.all { (it as? Container)?.width?.points?.let { width -> width > 0f } ?: true }
    ) {
        "Container width must be positive"
    }

    val remaining = availableWidth - fixed - gaps
    val flexible = node.children.filter { (it as? Container)?.width == null }
    require(remaining >= 0f && (flexible.isEmpty() || remaining > 0f)) {
        "Row widths and spacing exceed the available width"
    }

    // An unspecified or zero weight gets an equal share instead of collapsing.
    val weights =
        flexible
            .sumOf { (((it as? Container)?.weight ?: 0f).takeIf { n -> n > 0 } ?: 1f).toDouble() }
            .toFloat()

    val widths =
        node.children.map { child ->
            (child as? Container)?.width?.points
                ?: remaining * (((child as? Container)?.weight ?: 0f).takeIf { it > 0 } ?: 1f) /
                    weights
        }

    // Gap columns keep spacing independent of the child cells and their borders.
    val columns = mutableListOf<Float>()
    widths.forEachIndexed { i, w ->
        if (i > 0 && node.spacing.points > 0) {
            columns += node.spacing.points
        }

        columns += w
    }

    // OpenPDF treats column widths as proportions, so reserve unused space explicitly
    // to prevent fixed-width children from stretching to fill the row.
    if (flexible.isEmpty() && remaining > 0f) columns += remaining

    return PdfPTable(columns.toFloatArray()).apply {
        widthPercentage = 100f
        node.children.forEachIndexed { i, child ->
            if (i > 0 && node.spacing.points > 0) {
                addCell(PdfPCell().apply { border = Rectangle.NO_BORDER })
            }

            addCell(
                cell(child, widths[i]).apply {
                    verticalAlignment =
                        when (node.alignment) {
                            Alignment.Start -> Element.ALIGN_TOP
                            Alignment.Center -> Element.ALIGN_MIDDLE
                            Alignment.End -> Element.ALIGN_BOTTOM
                        }
                }
            )
        }
        if (flexible.isEmpty() && remaining > 0f) {
            addCell(PdfPCell().apply { border = Rectangle.NO_BORDER })
        }
    }
}
