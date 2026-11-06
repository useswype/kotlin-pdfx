package com.swypex.pdfx

/** A typed styling value. Combine values with `+`; later values take precedence. */
sealed interface Style

internal data object EmptyStyle : Style

internal data class CombinedStyle(val left: Style, val right: Style) : Style

operator fun Style.plus(other: Style): Style = CombinedStyle(this, other)

data object Bold : Style

data class PaddingTop(val value: Dimension) : Style

data class Rounded(val value: Dimension) : Style

data class Padding(val value: Dimension) : Style

data class Spacing(val value: Dimension) : Style

data class BorderColor(val value: Color) : Style

data class BorderWidth(val value: Dimension) : Style

data class TextColor(val value: Color) : Style

data class Background(val value: Color) : Style

/** Fold styles in order on top of component-specific defaults. */
internal fun Style.resolve(defaults: StyleValues = StyleValues()): StyleValues =
    when (this) {
        EmptyStyle -> defaults
        is CombinedStyle -> right.resolve(left.resolve(defaults))
        is StyleValues -> this
        is Dimension -> defaults.copy(size = this)
        is TextColor -> defaults.copy(color = value)
        Bold -> defaults.copy(bold = true)
        is TextAlign -> defaults.copy(align = this)
        is PaddingTop -> defaults.copy(paddingTop = value)
        is Rounded -> defaults.copy(cornerRadius = value)
        is Padding -> defaults.copy(padding = Insets(value))
        is Insets -> defaults.copy(padding = this)
        is Border -> defaults.copy(borderColor = color, borderWidth = width)
        is Spacing -> defaults.copy(spacing = value)
        is Background -> defaults.copy(background = value)
        is BorderColor -> defaults.copy(borderColor = value)
        is BorderWidth -> defaults.copy(borderWidth = value)
    }
