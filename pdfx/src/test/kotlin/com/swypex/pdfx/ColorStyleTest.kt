package com.swypex.pdfx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

class ColorStyleTest {
    @Test
    fun `text and background colors compose independently with last value winning`() {
        val style =
            TextColor(Color.Red) +
                Background(Color.Blue50) +
                TextColor(Color.Blue) +
                Background(Color.Gray100)
        val resolved = style.resolve()
        assertEquals(Color.Blue, resolved.color)
        assertEquals(Color.Gray100, resolved.background)
        assertFalse(Style::class.java.isAssignableFrom(Color::class.java))
    }

    @Test
    fun `explicit colors apply to text cards and table headers`() {
        val document = pdf {
            page {
                text(TextColor(Color.Red)) { "Text" }
                link("Link", "https://example.com")
                card(Background(Color.Blue50)) { text { "Card" } }
                table {
                    header(Background(Color.Blue) + TextColor(Color.White)) { listOf("Header") }
                    row("Cell")
                }
            }
        }
        val nodes = document.pages.single().nodes
        assertEquals(Color.Red, (nodes[0] as TextNode).style.color)
        assertEquals(Color.Blue, (nodes[1] as LinkNode).style.color)
        assertEquals(Color.Blue50, (nodes[2] as Container).background)
        val header = (nodes[3] as TableNode).headerStyle
        assertEquals(Color.Blue, header.background)
        assertEquals(Color.White, header.color)
        PDDocument.load(document.render()).use {
            assertTrue(PDFTextStripper().getText(it).contains("Header"))
        }
    }
}
