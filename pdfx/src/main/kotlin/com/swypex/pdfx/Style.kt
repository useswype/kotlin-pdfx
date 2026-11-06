package com.swypex.pdfx

import java.io.File

@JvmInline
value class Dimension(val points: Float) : Style {
    init {
        require(points.isFinite() && points >= 0)
    }
}

val Number.pt: Dimension
    get() = Dimension(toFloat())

data class Insets(
    val top: Dimension,
    val right: Dimension,
    val bottom: Dimension,
    val left: Dimension,
) : Style {
    constructor(all: Dimension) : this(all, all, all, all)
}

data class Border(val color: Color = Color.Slate200, val width: Dimension = 1.pt) : Style

enum class Alignment {
    Start,
    Center,
    End,
}

enum class TextAlign : Style {
    Left,
    Center,
    Right,
}

enum class ImageFit {
    Contain,
    Stretch,
}

data class PageSize(val width: Dimension, val height: Dimension) {
    companion object {
        val A4 = PageSize(595.28.pt, 841.89.pt)
        val Letter = PageSize(612.pt, 792.pt)
    }
}

internal data class StyleValues(
    val size: Dimension = 10.pt,
    val color: Color = Color.Black,
    val bold: Boolean = false,
    val lineHeight: Float = 1.35f,
    val align: TextAlign = TextAlign.Left,
    val paddingTop: Dimension = 0.pt,
    val background: Color? = null,
    val cornerRadius: Dimension = 0.pt,
    val padding: Insets = Insets(0.pt),
    val spacing: Dimension = 4.pt,
    val borderColor: Color? = null,
    val borderWidth: Dimension? = null,
) : Style {
    init {
        require(size.points > 0 && lineHeight >= 1f && lineHeight.isFinite())
    }
}

/** A TrueType font pair. Fonts must cover every character used by the document. */
data class FontFamily(val regular: File, val bold: File = regular)
