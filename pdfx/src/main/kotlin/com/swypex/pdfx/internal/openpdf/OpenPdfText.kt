package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import com.swypex.pdfx.*
import java.text.Bidi

internal fun OpenPdfComponents.paragraph(
    value: String,
    style: StyleValues,
    href: String? = null,
): Paragraph {
    // Helvetica has no Arabic glyphs; use the embedded font for the entire mixed run.
    val hasArabic = value.hasArabic()
    val baseFont =
        when {
            hasArabic && style.bold -> arabicBold
            hasArabic -> arabicRegular
            style.bold -> bold
            else -> regular
        }
    val font = Font(baseFont, style.size.points, Font.NORMAL, style.color.awt())

    // An Anchor carries the link target while retaining the same text formatting.
    val phrase =
        if (href == null) {
            Phrase(value, font)
        } else {
            Anchor(value, font).apply { reference = href }
        }

    return Paragraph().apply {
        add(phrase)

        // Leading is absolute in OpenPDF; the model stores it as a font-size multiplier.
        // Arabic glyphs can extend beyond the Latin font metrics used by the default leading.
        // Choosing a sane default that look good in most cases.
        leading =
            style.size.points *
                if (hasArabic) {
                    maxOf(style.lineHeight, 1.8f)
                } else {
                    style.lineHeight
                }

        val rtl = value.pdfRunDirection() == PdfWriter.RUN_DIRECTION_RTL

        // OpenPDF interprets left and right as start and end for RTL runs.
        alignment =
            when (style.align) {
                TextAlign.Left -> if (rtl) Element.ALIGN_RIGHT else Element.ALIGN_LEFT
                TextAlign.Center -> Element.ALIGN_CENTER
                TextAlign.Right -> if (rtl) Element.ALIGN_LEFT else Element.ALIGN_RIGHT
            }
    }
}

internal fun String.hasArabic(): Boolean =
    codePoints().anyMatch { Character.UnicodeScript.of(it) == Character.UnicodeScript.ARABIC }

// OpenPDF performs Arabic shaping and bidi reordering only when a run direction is set.
internal fun String.pdfRunDirection(): Int =
    if (
        codePoints().noneMatch {
            Character.getDirectionality(it) == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                Character.getDirectionality(it) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
        }
    ) {
        PdfWriter.RUN_DIRECTION_NO_BIDI
    } else if (Bidi(this, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).baseIsLeftToRight()) {
        PdfWriter.RUN_DIRECTION_LTR
    } else {
        PdfWriter.RUN_DIRECTION_RTL
    }
