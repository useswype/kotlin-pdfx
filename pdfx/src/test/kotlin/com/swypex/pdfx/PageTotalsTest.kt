package com.swypex.pdfx

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

class PageTotalsTest {
    @Test
    fun `footer includes actual document total on overflow and separate page specs`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 15.pt) {
                        footer { pageNumber(8.pt + TextAlign.Center, includeTotal = true) }
                        repeat(55) { text("Body $it") }
                    }
                    page(size = PageSize(300.pt, 200.pt), margin = 15.pt) {
                        footer { pageNumber(includeTotal = true) }
                        text("Final section")
                    }
                }
                .render()
        PDDocument.load(bytes).use { document ->
            assertTrue(document.numberOfPages >= 4)
            for (number in 1..document.numberOfPages) {
                val text =
                    PDFTextStripper()
                        .apply {
                            startPage = number
                            endPage = number
                        }
                        .getText(document)
                assertContains(text, "Page $number of ${document.numberOfPages}")
            }
        }
    }
}
