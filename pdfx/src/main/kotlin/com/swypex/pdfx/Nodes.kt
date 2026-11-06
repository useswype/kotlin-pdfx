package com.swypex.pdfx

import java.io.File

internal sealed interface Node

internal data class Container(
    val children: List<Node>,
    val horizontal: Boolean,
    val spacing: Dimension,
    val padding: Insets,
    val alignment: Alignment,
    val background: Color?,
    val border: Border?,
    val radius: Dimension,
    val width: Dimension?,
    val weight: Float,
) : Node

internal data class TextNode(val value: String, val style: StyleValues) : Node

internal data class LinkNode(val value: String, val href: String, val style: StyleValues) : Node

internal data class PageNumberNode(val style: StyleValues, val prefix: String) : Node

internal data class Rule(val color: Color, val thickness: Dimension) : Node

internal data class Space(val height: Dimension) : Node

internal data class ImageNode(
    val file: File,
    val width: Dimension,
    val height: Dimension,
    val fit: ImageFit,
) : Node

internal data class TableNode(
    val headers: List<String>,
    val rows: List<List<String>>,
    val padding: Insets,
    val border: Border,
    val cornerRadius: Dimension,
    val headerStyle: StyleValues,
) : Node

internal data class PageSpec(
    val size: PageSize,
    val margin: Insets,
    val nodes: List<Node>,
    val header: List<Node>,
    val footer: List<Node>,
)
