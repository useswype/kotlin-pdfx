package com.swypex.pdfx

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

class FirstPageHeaderTest {
    @Test
    fun `first header is only rendered on first page and continuation header repeats`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 15.pt) {
                        header { text("Continuation header") }
                        firstPageHeader {
                            text("First page header")
                            spacer(30.pt)
                        }
                        repeat(30) { text("Body line $it") }
                    }
                    page(size = PageSize(300.pt, 200.pt), margin = 15.pt) {
                        header { text("Second section header") }
                        text("Second section body")
                    }
                }
                .render()
        PDDocument.load(bytes).use { document ->
            fun page(n: Int) =
                PDFTextStripper()
                    .apply {
                        startPage = n
                        endPage = n
                    }
                    .getText(document)
            assertContains(page(1), "First page header")
            assertFalse(page(1).contains("Continuation header"))
            assertContains(page(2), "Continuation header")
            assertFalse(page(2).contains("First page header"))
            assertContains(page(document.numberOfPages), "Second section header")
            assertContains(page(document.numberOfPages), "Second section body")
        }
    }
}
