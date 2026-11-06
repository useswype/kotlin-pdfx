package com.swypex.pdfx.example

import com.swypex.pdfx.*
import java.io.File

private val navy = Color(17, 36, 77)
private val softGreen = Color(230, 247, 240)
private val green = Color(24, 128, 94)
private val warm = Color(255, 246, 227)

private data class Transaction(
    val date: String,
    val merchant: String,
    val category: String,
    val amount: String,
)

fun main() {
    val activity = transactions()
    val output =
        pdf {
                page(margin = 42.pt) {
                    header { reportHeader("OCT 2026") }
                    footer(marginTop = 12.pt) { reportFooter() }

                    column(Spacing(13.pt)) {
                        heading { "Monthly Account Report" }
                        text(11.pt + TextColor(Color.Gray)) {
                            "The financial picture for Self this month."
                        }

                        row(Spacing(10.pt)) {
                            summaryCard("AVAILABLE BALANCE", "EGP 245,800", "", dark = true)
                            summaryCard("CARD SPEND", "EGP 128,450", "Across 12 active cards")
                            summaryCard("TOTAL INFLOW", "EGP 310,000", "3 incoming transfers")
                        }

                        row(Spacing(10.pt)) {
                            insight(
                                File("example/assets/card.png"),
                                "12 cards active",
                                "Your team is ready to spend.",
                                softGreen,
                            )
                            insight(
                                File("example/assets/insights.svg"),
                                "Spending in view",
                                "Review activity across your team.",
                                warm,
                            )
                        }

                        spacer(height = 12.pt)

                        row {
                            text(15.pt + Bold) { "Spend Breakdown" }
                            text(8.pt + TextColor(Color.Gray) + Bold + TextAlign.Right) {
                                "BY CATEGORY"
                            }
                        }

                        table(Padding(7.pt)) {
                            header { listOf("CATEGORY", "TRANSACTIONS", "SHARE", "AMOUNT") }
                            row("Software & services", "18", "38%", "EGP 48,811")
                            row("Travel & transport", "12", "27%", "EGP 34,682")
                            row("Office & supplies", "16", "21%", "EGP 26,975")
                            row("Other", "8", "14%", "EGP 17,982")
                        }

                        card(
                            Background(Color.Gray100) +
                                Rounded(8.pt) +
                                Padding(12.pt) +
                                Spacing(4.pt)
                        ) {
                            text(8.pt + TextColor(Color.Blue) + Bold) { "NOTE" }
                            text(9.pt + TextColor(Color.Gray)) {
                                "Your monthly report brings balances and card activity together in one place."
                            }
                        }
                    }
                }

                page(margin = 42.pt) {
                    header { reportHeader("TRANSACTION DETAILS  /  OCT 2026") }
                    footer(marginTop = 12.pt) { reportFooter() }

                    column(Spacing(12.pt)) {
                        text(9.pt + TextColor(Color.Blue) + Bold) { "ACCOUNT ACTIVITY" }
                        heading(level = 2) { "Transaction details" }

                        table(Rounded(8.pt) + Padding(8.pt)) {
                            header("DATE", "MERCHANT", "CATEGORY", "AMOUNT")
                            activity.forEach { row(it.date, it.merchant, it.category, it.amount) }
                        }

                        card(
                            Background(softGreen) + Rounded(8.pt) + Padding(14.pt) + Spacing(5.pt)
                        ) {
                            text(9.pt + TextColor(green) + Bold) { "SEE AN ISSUE?" }
                            text(10.pt + TextColor(Color.Black)) {
                                "Our team is here to help you understand your account."
                            }

                            link(
                                "Visit the help center",
                                "https://support.swypex.com/",
                                10.pt + TextColor(Color.Blue) + Bold,
                            )
                        }

                        text(8.pt + TextColor(Color.Gray)) {
                            "This illustrative report uses sample data and is not an account statement."
                        }
                    }
                }
            }
            .render()

    File("example/swypex-complex-overview.pdf").writeBytes(output)

    println("Wrote example/swypex-complex-overview.pdf")
}

private fun ComponentBuilder.reportHeader(section: String) {
    row {
        text(12.pt + TextColor(Color.Blue) + Bold) { "SWYPEX" }
        text(8.pt + TextColor(Color.Gray) + TextAlign.Right) { section }
    }
    spacer(16.pt)
}

private fun ComponentBuilder.reportFooter() {
    divider()
    row() {
        text(8.pt + TextColor(Color.Gray) + PaddingTop(8.pt)) {
            "SWYPEX  |  better business banking"
        }
        pageNumber(8.pt + TextColor(Color.Gray) + TextAlign.Right + PaddingTop(8.pt))
    }
}

private fun ComponentBuilder.summaryCard(
    label: String,
    value: String,
    caption: String,
    dark: Boolean = false,
) {
    card(
        Background(if (dark) navy else Color.Blue50) +
            Rounded(9.pt) +
            Padding(15.pt) +
            Spacing(7.pt)
    ) {
        text(8.pt + TextColor(if (dark) Color.White else Color.Gray) + Bold) { label }
        text(16.pt + TextColor(if (dark) Color.White else Color.Black) + Bold) { value }
        text(9.pt + TextColor(if (dark) Color.White else Color.Gray)) { caption }
    }
}

private fun ComponentBuilder.insight(icon: File, title: String, detail: String, background: Color) {
    card(Background(background) + Rounded(9.pt) + Padding(14.pt) + Spacing(6.pt)) {
        image(icon, width = 32.pt, height = 32.pt)
        text(10.pt + Bold) { title }
        text(9.pt + TextColor(Color.Gray)) { detail }
    }
}

private fun transactions(): List<Transaction> {
    val merchants =
        listOf(
            "Cairo Workspace" to "Office",
            "Cloud Services" to "Software",
            "Hyperone" to "Supplies",
            "Uber" to "Travel",
            "Vodafone" to "Phone Bill",
            "Metro Market" to "Meals",
        )

    return (1..42).map { index ->
        val (merchant, category) = merchants[(index - 1) % merchants.size]
        Transaction(
            "Oct ${((index - 1) % 28 + 1).toString().padStart(2, '0')}",
            merchant,
            category,
            "EGP ${350 + index * 135}.00",
        )
    }
}
