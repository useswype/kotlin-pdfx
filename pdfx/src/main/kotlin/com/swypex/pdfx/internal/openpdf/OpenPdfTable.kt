package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*

internal fun OpenPdfComponents.dataTable(node: TableNode, width: Float): PdfPTable =
    PdfPTable(node.headers.size).apply {
        widthPercentage = 100f

        // Repeating the header preserves column context after a page break.
        headerRows = 1
        isSplitRows = false

        // Per-cell borders and fills would break the continuous rounded outline.
        // Include headers in the event so the same outline works on continuation pages.
        if (node.cornerRadius.points > 0f) {
            isHeadersInEvent = true
            tableEvent = tableBackgroundAndBorder(node)
        }

        fun addRow(row: List<String>, header: Boolean) =
            row.forEach { value ->
                val style =
                    if (header) {
                        node.headerStyle
                    } else {
                        StyleValues()
                    }

                addCell(
                    PdfPCell(paragraph(value, style)).apply {
                        runDirection = value.pdfRunDirection()
                        paddingTop = node.padding.top.points
                        paddingRight = node.padding.right.points
                        paddingBottom = node.padding.bottom.points
                        paddingLeft = node.padding.left.points

                        horizontalAlignment = Element.ALIGN_LEFT
                        verticalAlignment = Element.ALIGN_MIDDLE

                        // Include font ascenders and descenders in cell height calculations.
                        isUseAscender = true
                        isUseDescender = true

                        border =
                            if (node.cornerRadius.points > 0f) Rectangle.NO_BORDER
                            else Rectangle.BOX

                        borderWidth = node.border.width.points
                        borderColor = node.border.color.awt()

                        if (header && node.cornerRadius.points == 0f) {
                            backgroundColor = node.headerStyle.background?.awt()
                        }
                    }
                )
            }

        addRow(node.headers, true)

        node.rows.forEach { addRow(it, false) }
    }
