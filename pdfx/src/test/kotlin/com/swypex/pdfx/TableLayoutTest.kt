package com.swypex.pdfx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

class TableLayoutTest {
    @Test
    fun `column widths and row backgrounds affect rendering`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 30.pt) {
                        table(Padding(8.pt)) {
                            columnWidths(1f, 2f)
                            header("First", "Second")
                            row(Background(Color.Red500), "A", "B")
                        }
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            val image = PDFRenderer(doc).renderImageWithDPI(0, 72f)
            assertEquals(java.awt.Color(239, 68, 68), java.awt.Color(image.getRGB(50, 65)))
            // The first column ends one third of the way across the 240pt table.
            val boundary = java.awt.Color(image.getRGB(110, 65))
            assertEquals(false, boundary == java.awt.Color(239, 68, 68))
            assertEquals(java.awt.Color(239, 68, 68), java.awt.Color(image.getRGB(150, 65)))
        }
    }

    @Test
    fun `invalid column widths are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            pdf {
                page {
                    table {
                        columnWidths(1f, 0f)
                        header("a", "b")
                    }
                }
            }
        }
        assertFailsWith<IllegalArgumentException> {
            pdf {
                page {
                    table {
                        columnWidths(1f)
                        header("a", "b")
                    }
                }
            }
        }
    }

    @Test
    fun `rounded tables clip row and cell backgrounds across pages`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 30.pt) {
                        table(Rounded(12.pt) + Padding(8.pt)) {
                            header("First", "Second")
                            repeat(12) {
                                row(
                                    Background(Color.Red500),
                                    mapOf(1 to Background(Color.Blue50)),
                                    "A",
                                    "B",
                                )
                            }
                        }
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            assertEquals(true, doc.numberOfPages > 1)
            val renderer = PDFRenderer(doc)
            for (page in 0 until doc.numberOfPages) {
                val image = renderer.renderImageWithDPI(page, 72f)
                assertEquals(java.awt.Color(239, 68, 68), java.awt.Color(image.getRGB(50, 65)))
                assertEquals(java.awt.Color(239, 246, 255), java.awt.Color(image.getRGB(200, 65)))
                assertEquals(java.awt.Color.WHITE, java.awt.Color(image.getRGB(30, 30)))
            }
        }
    }

    @Test
    fun `cell style and horizontal borders are rendered`() {
        val bytes =
            pdf {
                    page(size = PageSize(300.pt, 200.pt), margin = 30.pt) {
                        table(Padding(8.pt)) {
                            borders = TableBorders.Horizontal
                            columnStyle(1, TextAlign.Right)
                            header("First", "Second")
                            row(
                                Background(Color.White),
                                mapOf(1 to Background(Color.Red500)),
                                "A",
                                "B",
                            )
                        }
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            val image = PDFRenderer(doc).renderImageWithDPI(0, 72f)
            assertEquals(java.awt.Color.WHITE, java.awt.Color(image.getRGB(50, 65)))
            assertEquals(java.awt.Color(239, 68, 68), java.awt.Color(image.getRGB(150, 65)))
        }
    }

    @Test
    fun `byte image is rendered`() {
        val image = java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.color = java.awt.Color.RED
        graphics.fillRect(0, 0, 2, 2)
        graphics.dispose()
        val encoded =
            java.io
                .ByteArrayOutputStream()
                .also { javax.imageio.ImageIO.write(image, "png", it) }
                .toByteArray()
        val bytes =
            pdf {
                    page(size = PageSize(100.pt, 100.pt), margin = 10.pt) {
                        image(encoded, 30.pt, 30.pt)
                    }
                }
                .render()
        PDDocument.load(bytes).use { doc ->
            val rendered = PDFRenderer(doc).renderImageWithDPI(0, 72f)
            assertEquals(java.awt.Color.RED, java.awt.Color(rendered.getRGB(20, 20)))
        }
    }
}
