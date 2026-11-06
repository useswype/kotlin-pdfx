package com.swypex.pdfx.example

import com.swypex.pdfx.*
import java.io.File

fun main() {
    val document = pdf {
        page(margin = 40.pt) {
            column(Spacing(14.pt)) {
                heading(style = TextAlign.Right) { "تقرير المدفوعات" }
                text(12.pt + TextAlign.Right) { "مرحبا بكم في تقرير المدفوعات الشهري" }

                divider()

                heading(level = 2) { "Arabic and English" }

                text(13.pt + TextAlign.Right) { "تحويل بنكي بقيمة 1,250 EGP" }
                text(13.pt) { "Payment 1,250 EGP - تحويل بنكي" }
                text(10.pt + TextColor(Color.Gray)) {
                    "The first strong character determines the text direction for each block."
                }

                table(Padding(8.pt)) {
                    header { listOf("الوصف", "Amount") }
                    row("تحويل بنكي", "1,250 EGP")
                    row("مدفوعات البطاقة", "450 EGP")
                }

                link("زيارة الموقع", "https://example.com", 12.pt + TextAlign.Right)
            }
        }
    }

    val output = File("example/swypex-arabic-english.pdf")

    output.writeBytes(document.render())

    println("Wrote ${output.absolutePath}")
}
