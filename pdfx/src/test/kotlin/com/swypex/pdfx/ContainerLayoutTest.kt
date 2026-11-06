package com.swypex.pdfx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

class ContainerLayoutTest {
    @Test
    fun `weighted containers divide row space among their contents`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 30.pt) {
                        row(Spacing(10.pt)) {
                            container(weight = 1f) {
                                card(Background(Color.Red500)) { spacer(20.pt) }
                            }
                            container(weight = 2f) {
                                card(Background(Color.Blue500)) { spacer(20.pt) }
                            }
                        }
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            val image = PDFRenderer(doc).renderImageWithDPI(0, 72f)
            fun color(x: Int) = java.awt.Color(image.getRGB(x, 35))
            assertEquals(java.awt.Color(239, 68, 68), color(100))
            assertEquals(java.awt.Color.WHITE, color(110))
            assertEquals(java.awt.Color(59, 130, 246), color(120))
            assertEquals(java.awt.Color(59, 130, 246), color(260))
        }
    }

    @Test
    fun `fixed width containers preserve width and unused space`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 30.pt) {
                        row(Spacing(10.pt)) {
                            container(width = 50.pt) {
                                card(Background(Color.Red500)) { spacer(20.pt) }
                            }
                            container(width = 50.pt) {
                                card(Background(Color.Blue500)) { spacer(20.pt) }
                            }
                        }
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            val image = PDFRenderer(doc).renderImageWithDPI(0, 72f)
            fun color(x: Int) = java.awt.Color(image.getRGB(x, 35))
            assertEquals(java.awt.Color(239, 68, 68), color(40))
            assertEquals(java.awt.Color.WHITE, color(85))
            assertEquals(java.awt.Color(59, 130, 246), color(100))
            assertEquals(java.awt.Color.WHITE, color(145))
        }
    }

    @Test
    fun `negative weight is rejected`() {
        assertFailsWith<IllegalArgumentException> { pdf { page { container(weight = -1f) {} } } }
    }
}
