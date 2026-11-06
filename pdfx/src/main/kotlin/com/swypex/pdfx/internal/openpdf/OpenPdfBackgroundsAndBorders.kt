package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*
import com.swypex.pdfx.Color

// Individual cell borders cannot form one rounded outline around the whole table.
internal fun tableBackgroundAndBorder(node: TableNode) =
    PdfPTableEvent { _, widths, heights, headerRows, _, canvases ->
        val left = widths.first().first()
        val right = widths.first().last()
        val top = heights.first()
        val bottom = heights.last()

        // Limit the radius to the rendered fragment, including on split tables.
        val radius = minOf(node.cornerRadius.points, (right - left) / 2, (top - bottom) / 2)
        val background = canvases[PdfPTable.BACKGROUNDCANVAS]

        // Without clipping, the header fill would extend beyond the rounded corners.
        if (headerRows > 0) {
            background.saveState()
            background.roundRectangle(left, bottom, right - left, top - bottom, radius)
            background.clip()
            background.newPath()
            background.setColorFill((node.headerStyle.background ?: Color.Gray100).awt())
            background.rectangle(
                left,
                heights[headerRows.coerceAtMost(heights.lastIndex)],
                right - left,
                top - heights[headerRows.coerceAtMost(heights.lastIndex)],
            )
            background.fill()
            background.restoreState()
        }

        val lines = canvases[PdfPTable.LINECANVAS]
        lines.saveState()
        lines.setColorStroke(node.border.color.awt())
        lines.setLineWidth(node.border.width.points)
        lines.roundRectangle(left, bottom, right - left, top - bottom, radius)

        // Cell borders are disabled for rounded tables, so draw the interior grid here.
        for (y in heights.drop(1).dropLast(1)) {
            lines.moveTo(left, y)
            lines.lineTo(right, y)
        }

        for (x in widths.first().drop(1).dropLast(1)) {
            lines.moveTo(x, bottom)
            lines.lineTo(x, top)
        }

        lines.stroke()
        lines.restoreState()
    }

// Cell events paint behind content and allow the border and fill to share an outline.
internal fun containerBackgroundAndBorder(node: Container) = PdfPCellEvent { _, rect, canvases ->
    val canvas = canvases[PdfPTable.BACKGROUNDCANVAS]
    canvas.saveState()

    node.background?.let { canvas.setColorFill(it.awt()) }

    node.border?.let {
        canvas.setColorStroke(it.color.awt())
        canvas.setLineWidth(it.width.points)
    }

    canvas.roundRectangle(rect.left, rect.bottom, rect.width, rect.height, node.radius.points)

    if (node.background != null && node.border != null) {
        canvas.fillStroke()
    } else if (node.background != null) {
        canvas.fill()
    } else {
        canvas.stroke()
    }

    // Keep clipping, colors, and line settings from leaking into later drawing operations.
    canvas.restoreState()
}

internal fun Color.awt() = java.awt.Color(red, green, blue)
