package com.swypex.pdfx.example

import com.swypex.pdfx.*
import java.io.File

private val ink = Color(22, 22, 25)
private val rule = Color.Gray200
private val body = 9.pt + TextColor(ink)
private val label = body + Bold

private fun ComponentBuilder.invoiceField(name: String, value: String) {
    row {
        container(width = 75.pt) { text(label) { name } }
        text(body) { value }
    }
}

private fun ComponentBuilder.itemHeading() {
    row {
        text(body) { "Description" }
        container(width = 55.pt) { text(body + TextAlign.Right) { "Qty" } }
        container(width = 65.pt) { text(body + TextAlign.Right) { "Unit price" } }
        container(width = 80.pt) { text(body + TextAlign.Right) { "Amount" } }
    }
}

private fun ComponentBuilder.itemLine(
    description: String,
    period: String,
    quantity: String,
    price: String,
    amount: String,
) {
    row {
        column {
            text(body) { description }
            text(body + PaddingTop(2.pt)) { period }
        }

        container(width = 55.pt) { text(body + TextAlign.Right) { quantity } }
        container(width = 65.pt) { text(body + TextAlign.Right) { price } }
        container(width = 80.pt) { text(body + TextAlign.Right) { amount } }
    }
}

private fun ComponentBuilder.totalLine(title: String, amount: String, bold: Boolean = false) {
    divider(rule, 0.5.pt)
    row {
        text(if (bold) label else body) { title }
        text((if (bold) label else body) + TextAlign.Right) { amount }
    }
}

fun main() {
    val bytes =
        pdf {
                page(size = PageSize.Letter, margin = 30.pt) {
                    footer {
                        divider(rule, 0.5.pt)
                        spacer(8.pt)
                        row {
                            text(8.pt + TextColor(ink)) { "INV-2026-001 | Due November 30, 2026" }
                            pageNumber(8.pt + TextColor(ink) + TextAlign.Right)
                        }
                    }

                    column {
                        text(20.pt + TextColor(ink) + Bold) { "Purchase Order" }
                        spacer(18.pt)
                        column(Spacing(4.pt)) {
                            invoiceField("Invoice number", "INV-2026-001")
                            invoiceField("Date of issue", "November 1, 2026")
                            invoiceField("Date due", "November 30, 2026")
                        }

                        spacer(20.pt)
                        row {
                            container(weight = 2f) {
                                text(label) { "My Design Studio" }
                                text(body + PaddingTop(8.pt)) {
                                    "100 Example Street\nSuite 200\nPortland, OR 97201\nUnited States\nbilling@example.com"
                                }
                            }
                            container(weight = 3f) {
                                text(label) { "Bill to" }
                                text(body + PaddingTop(8.pt)) {
                                    "Jordan Lee\n200 Sample Avenue\nSeattle, WA 98101\nUnited States\ncustomer@example.com"
                                }
                            }
                        }

                        spacer(18.pt)
                        text(14.pt + TextColor(ink) + Bold) { "$350.00 USD due November 30, 2026" }

                        spacer(10.pt)
                        text(9.pt + TextColor(Color.Blue500)) { "Pay online" }

                        spacer(30.pt)
                        itemHeading()

                        spacer(3.pt)
                        divider(ink, 0.7.pt)

                        spacer(6.pt)
                        itemLine(
                            "Design consultation",
                            "November 1 - November 15, 2026",
                            "2",
                            "$125.00",
                            "$250.00",
                        )

                        spacer(14.pt)
                        divider(rule, 0.5.pt)
                        spacer(6.pt)

                        itemLine(
                            "Asset preparation",
                            "November 16 - November 30, 2026",
                            "1",
                            "$100.00",
                            "$100.00",
                        )

                        spacer(14.pt)

                        row {
                            container(weight = 1f) {}
                            container(weight = 1f) {
                                column(Spacing(2.pt)) {
                                    totalLine("Subtotal", "$350.00")
                                    totalLine("Total", "$350.00")
                                    totalLine("Amount due", "$350.00 USD", bold = true)
                                }
                            }
                        }
                    }
                }
            }
            .render()

    val output = File("example/swypex-invoice.pdf")

    output.writeBytes(bytes)

    println("Wrote ${output.path}")
}
