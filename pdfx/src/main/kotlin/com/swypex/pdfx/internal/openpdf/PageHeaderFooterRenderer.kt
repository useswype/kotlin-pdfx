package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*

// Page-end rendering has the final page number, including pages created by overflow.
internal class PageHeaderFooterRenderer(
    private val header: (Int) -> PdfPTable?,
    private val footer: (Int) -> PdfPTable?,
    private val headerHeight: Float,
    private val footerHeight: Float,
    private val page: PageSpec,
) : PdfPageEventHelper() {
    override fun onEndPage(writer: PdfWriter, document: Document) {
        val headerTable = header(writer.pageNumber)
        val footerTable = footer(writer.pageNumber)

        // A wider page number can wrap despite the placeholder; fail instead of
        // silently drawing over the body. The tolerance absorbs rounding error.
        require((headerTable?.totalHeight ?: 0f) <= headerHeight + 0.01f) {
            "Page number does not fit in the reserved header area"
        }

        require((footerTable?.totalHeight ?: 0f) <= footerHeight + 0.01f) {
            "Page number does not fit in the reserved footer area"
        }

        // Direct drawing keeps headers and footers outside the body flow and its page-break logic.
        headerTable?.writeSelectedRows(
            0,
            -1,
            page.margin.left.points,
            page.size.height.points - page.margin.top.points,
            writer.directContent,
        )

        footerTable?.writeSelectedRows(
            0,
            -1,
            page.margin.left.points,
            page.margin.bottom.points + footerHeight,
            writer.directContent,
        )
    }
}
