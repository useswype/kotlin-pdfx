package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*
import com.swypex.pdfx.PdfDocument as PdfModel
import java.io.ByteArrayOutputStream
import java.io.File

internal class OpenPdfRenderer(private val model: PdfModel) {
    private lateinit var components: OpenPdfComponents
    private lateinit var document: Document
    private lateinit var writer: PdfWriter
    private var width = 0f
    private var height = 0f

    fun render(): ByteArray {
        val needsTotal =
            model.pages.any { spec ->
                (spec.header + (spec.firstPageHeader ?: emptyList()) + spec.footer).any {
                    it.needsTotalPages()
                }
            }
        if (!needsTotal) return renderOnce(null)
        val first = renderOnce(null)
        val total =
            PdfReader(first).let { reader ->
                try {
                    reader.numberOfPages
                } finally {
                    reader.close()
                }
            }
        val result = renderOnce(total)
        val finalTotal =
            PdfReader(result).let { reader ->
                try {
                    reader.numberOfPages
                } finally {
                    reader.close()
                }
            }
        check(total == finalTotal) {
            "Page count changed while rendering page totals: $total to $finalTotal"
        }
        return result
    }

    private fun Node.needsTotalPages(): Boolean =
        when (this) {
            is PageNumberNode -> includeTotal
            is Container -> children.any { it.needsTotalPages() }
            else -> false
        }

    private fun renderOnce(totalPages: Int?): ByteArray {
        components =
            OpenPdfComponents(
                font(model.fonts?.regular, BaseFont.HELVETICA),
                font(model.fonts?.bold, BaseFont.HELVETICA_BOLD),
                model.fonts?.regular?.let { font(it, BaseFont.HELVETICA) }
                    ?: arabicFont("Cairo-Regular.ttf"),
                model.fonts?.bold?.let { font(it, BaseFont.HELVETICA_BOLD) }
                    ?: arabicFont("Cairo-Bold.ttf"),
            )
        return ByteArrayOutputStream().use { output ->
            document = Document()
            writer = PdfWriter.getInstance(document, output)

            // Keep one registered event while swapping header and footer
            // renderers for each page specification.
            val pageEvents =
                object : PdfPageEventHelper() {
                    var headerFooter: PageHeaderFooterRenderer? = null
                    var updateNextMargins: (() -> Unit)? = null
                    var changingSpec = false

                    override fun onEndPage(writer: PdfWriter, document: Document) {
                        headerFooter?.onEndPage(writer, document)
                        if (!changingSpec) updateNextMargins?.invoke()
                    }
                }
            writer.pageEvent = pageEvents
            try {
                model.pages.forEachIndexed { index, spec ->
                    width =
                        spec.size.width.points - spec.margin.left.points - spec.margin.right.points

                    val pageWidth = width

                    // Reserve space before pagination; a long placeholder reduces the chance
                    // that later page numbers wrap and overlap the body.
                    val header = headerFooterTable(spec.header, pageWidth, 999999999)
                    val footer = headerFooterTable(spec.footer, pageWidth, 999999999)
                    val headerHeight = header?.totalHeight ?: 0f
                    val firstHeader =
                        headerFooterTable(spec.firstPageHeader ?: spec.header, pageWidth, 999999999)
                    val firstHeaderHeight = firstHeader?.totalHeight ?: 0f
                    val footerHeight = footer?.totalHeight ?: 0f

                    height =
                        spec.size.height.points -
                            spec.margin.top.points -
                            spec.margin.bottom.points -
                            headerHeight -
                            footerHeight

                    document.setPageSize(Rectangle(spec.size.width.points, spec.size.height.points))
                    document.setMargins(
                        spec.margin.left.points,
                        spec.margin.right.points,
                        spec.margin.top.points + firstHeaderHeight,
                        spec.margin.bottom.points + footerHeight,
                    )

                    // OpenPDF applies the configured size and margins when a page starts.
                    if (index == 0) {
                        document.open()
                    } else {
                        pageEvents.changingSpec = true
                        try {
                            document.newPage()
                        } finally {
                            pageEvents.changingSpec = false
                        }
                    }
                    val firstPageNumber = writer.pageNumber
                    pageEvents.updateNextMargins = {
                        document.setMargins(
                            spec.margin.left.points,
                            spec.margin.right.points,
                            spec.margin.top.points + headerHeight,
                            spec.margin.bottom.points + footerHeight,
                        )
                    }

                    pageEvents.headerFooter =
                        PageHeaderFooterRenderer(
                            header = { number ->
                                val nodes =
                                    if (number == firstPageNumber)
                                        spec.firstPageHeader ?: spec.header
                                    else spec.header
                                headerFooterTable(nodes, pageWidth, number, totalPages)
                            },
                            footer = { number ->
                                headerFooterTable(spec.footer, pageWidth, number, totalPages)
                            },
                            headerHeight = maxOf(headerHeight, firstHeaderHeight),
                            footerHeight = footerHeight,
                            page = spec,
                        )

                    spec.nodes.forEach { flow(it, Alignment.Start) }
                }
            } catch (failure: Throwable) {
                // Preserve the rendering failure even if closing the partial PDF also fails.
                try {
                    document.close()
                } catch (closeFailure: Throwable) {
                    failure.addSuppressed(closeFailure)
                }

                throw failure
            }

            document.close()
            output.toByteArray()
        }
    }

    // Built-in fonts avoid embedding; supplied fonts use Unicode encoding and are embedded.
    private fun font(file: File?, fallback: String): BaseFont =
        if (file == null) BaseFont.createFont(fallback, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED)
        else BaseFont.createFont(file.absolutePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    private fun arabicFont(name: String): BaseFont {
        val bytes =
            checkNotNull(javaClass.getResourceAsStream("/com/swypex/pdfx/fonts/$name")) {
                    "Missing bundled Arabic font: $name"
                }
                .use { it.readBytes() }

        return BaseFont.createFont(name, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null)
    }

    private fun headerFooterTable(
        nodes: List<Node>,
        pageWidth: Float,
        pageNumber: Int,
        totalPages: Int? = null,
    ): PdfPTable? =
        if (nodes.isEmpty()) null
        else
            components
                .vertical(nodes.map { it.resolvePageNumber(pageNumber, totalPages) }, 0f, pageWidth)
                .apply { totalWidth = pageWidth }

    private fun Node.resolvePageNumber(pageNumber: Int, totalPages: Int?): Node =
        when (this) {
            is PageNumberNode ->
                TextNode(
                    prefix +
                        pageNumber +
                        if (includeTotal) " of ${totalPages ?: 999999999}" else "",
                    style,
                )
            is Container ->
                copy(children = children.map { it.resolvePageNumber(pageNumber, totalPages) })
            else -> this
        }

    private fun flow(node: Node, alignment: Alignment) {
        // Plain columns can flow child by child; decorated containers must stay intact
        // so their background and border enclose the whole component.
        if (
            node is Container &&
                !node.horizontal &&
                node.background == null &&
                node.border == null &&
                node.padding == Insets(0.pt) &&
                node.width == null
        ) {
            node.children.forEachIndexed { i, child ->
                if (i > 0 && node.spacing.points > 0f)
                    document.add(components.spacing(node.spacing.points))
                flow(child, node.alignment)
            }
            return
        }

        val table =
            if (node is TableNode) components.dataTable(node, width)
            else components.vertical(listOf(node), 0f, width, alignment)
        table.totalWidth = width

        if (node !is TableNode) {
            // Keep decorated components intact rather than splitting their visual boundary.
            table.keepTogether = true
        }

        table.widthPercentage = 100f
        document.add(table)
    }
}
