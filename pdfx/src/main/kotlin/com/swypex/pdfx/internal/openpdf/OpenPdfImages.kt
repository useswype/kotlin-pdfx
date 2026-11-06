package com.swypex.pdfx.internal.openpdf

import com.lowagie.text.Image
import com.swypex.pdfx.ImageNode
import java.io.ByteArrayOutputStream
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder

// Rasterizing SVGs lets them use the same OpenPDF image path as bitmap files.
internal fun loadImage(node: ImageNode): Image {
    if (!node.file.extension.equals("svg", ignoreCase = true)) {
        return Image.getInstance(node.file.absolutePath)
    }

    val bytes =
        ByteArrayOutputStream().use { output ->
            val transcoder = PNGTranscoder()

            // Extra pixels reduce visible rasterization when the image is placed at PDF size.
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, node.width.points * 2f)

            node.file.inputStream().use { input ->
                transcoder.transcode(TranscoderInput(input), TranscoderOutput(output))
            }

            output.toByteArray()
        }

    return Image.getInstance(bytes)
}
