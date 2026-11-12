package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*

internal fun OpenPdfComponents.dataTable(node: TableNode, width: Float): PdfPTable =
    PdfPTable(node.headers.size).apply {
        widthPercentage = 100f
        node.columnWidths?.let { setWidths(it.toFloatArray()) }

        // Repeating the header preserves column context after a page break.
        headerRows = 1
        isSplitRows = false

        // Per-cell borders and fills would break the continuous rounded outline.
        // Include headers in the event so the same outline works on continuation pages.
        if (node.cornerRadius.points > 0f) {
            isHeadersInEvent = true
            tableEvent = tableBackgroundAndBorder(node)
        }

        fun addRow(
            row: List<String>,
            rowStyle: StyleValues,
            cellStyles: Map<Int, Style> = emptyMap(),
        ) =
            row.forEachIndexed { index, value ->
                val columnStyle = node.columnStyles[index]?.resolve(rowStyle) ?: rowStyle
                val style = cellStyles[index]?.resolve(columnStyle) ?: columnStyle
                addCell(
                    PdfPCell(paragraph(value, style)).apply {
                        runDirection = value.pdfRunDirection()
                        paddingTop = node.padding.top.points
                        paddingRight = node.padding.right.points
                        paddingBottom = node.padding.bottom.points
                        paddingLeft = node.padding.left.points

                        horizontalAlignment =
                            when (style.align) {
                                TextAlign.Left -> Element.ALIGN_LEFT
                                TextAlign.Center -> Element.ALIGN_CENTER
                                TextAlign.Right -> Element.ALIGN_RIGHT
                            }
                        verticalAlignment = Element.ALIGN_MIDDLE

                        // Include font ascenders and descenders in cell height calculations.
                        isUseAscender = true
                        isUseDescender = true

                        border =
                            if (node.cornerRadius.points > 0f) Rectangle.NO_BORDER
                            else
                                when (node.borders) {
                                    TableBorders.All -> Rectangle.BOX
                                    TableBorders.Horizontal -> Rectangle.BOTTOM
                                    TableBorders.None -> Rectangle.NO_BORDER
                                }

                        borderWidth = node.border.width.points
                        borderColor = node.border.color.awt()

                        if (node.cornerRadius.points == 0f) {
                            backgroundColor = style.background?.awt()
                        } else {
                            cellEvent = RoundedTableCellBackground(style.background)
                        }
                    }
                )
            }

        addRow(node.headers, node.headerStyle)

        node.rows.forEach { addRow(it.cells, it.style, it.cellStyles) }
    }
