package com.swypex.pdfx

import com.swypex.pdfx.internal.openpdf.OpenPdfRenderer

class PdfDocument
internal constructor(internal val pages: List<PageSpec>, internal val fonts: FontFamily?) {
    /** Renders this document to a complete PDF. */
    fun render(): ByteArray = OpenPdfRenderer(this).render()
}

fun pdf(block: DocumentBuilder.() -> Unit): PdfDocument =
    DocumentBuilder().apply(block).let {
        require(it.pages.isNotEmpty())
        PdfDocument(it.pages.toList(), it.fonts)
    }
