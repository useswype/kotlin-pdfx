[![Maven](https://badges.mvnrepository.com/badge/com.swypex/pdfx/badge.svg?label=Maven&color=green)](https://mvnrepository.com/artifact/com.swypex/pdfx)


# Swypex PDFx

A Kotlin/JVM library for creating PDFs easily with sane default values and friendly DSL.

PDFx enables users to build beautiful PDFs without spending time on boilerplate or
configuration.

## Examples

| Example | Source | Rendered PDF | Generate |
| --- | --- | --- | --- |
| Detailed report | [Kotlin](example/src/main/kotlin/com/swypex/pdfx/example/ComplexOverview.kt) | [PDF](example/swypex-complex-overview.pdf) | `./gradlew :example:complexOverview` |
| Arabic and English | [Kotlin](example/src/main/kotlin/com/swypex/pdfx/example/ArabicEnglish.kt) | [PDF](example/swypex-arabic-english.pdf) | `./gradlew :example:arabicEnglish` |
| Invoice | [Kotlin](example/src/main/kotlin/com/swypex/pdfx/example/Invoice.kt) | [PDF](example/swypex-invoice.pdf) | `./gradlew :example:run` |


## Usage
Include PDFx as a depdency in POM or in Gradle config

```
<dependency>
  <groupId>com.swypex</groupId>
  <artifactId>pdfx</artifactId>
  <version>0.1.0</version>
</dependency>
```
or
```
implementation("com.swypex:pdfx:0.1.0")
```

### Kotlin
```kotlin
import com.swypex.pdfx.*

val bytes = pdf {
    page(size = PageSize.A4, margin = 40.pt) {
        footer(marginTop = 12.pt) { pageNumber(8.pt + TextColor(Color.Gray) + TextAlign.Right) }
        column(Spacing(16.pt)) {
            heading { "Monthly overview" }
            card(Background(Color.Gray100) + Rounded(8.pt) + Padding(12.pt) + Spacing(4.pt)) {
                text { "Your account at a glance" }
            }
            text(9.pt + TextColor(Color.Blue) + Bold) { "OCTOBER 2026" }
            row(Spacing(12.pt)) {
                container(weight = 1f) {
                    card(Background(Color.Blue50) + Rounded(8.pt) + Padding(16.pt)) {
                        text { "Total card spend" }
                        heading(level = 2) { "EGP 128,450.00" }
                    }
                }
                container(weight = 1f) {
                    card(Background(Color.Blue50) + Rounded(8.pt) + Padding(16.pt)) {
                        text { "Available balance" }
                        heading(level = 2) { "EGP 245,800.00" }
                    }
                }
            }
            table(Padding(8.pt) + Rounded(8.pt) + BorderColor(Color.Blue) + BorderWidth(1.pt)) {
                header(Background(Color.Blue50) + TextColor(Color.Blue)) { listOf("Description", "Amount") }
                row("Cloud services", "EGP 6,750.00")
            }
            divider()
            text(10.pt + PaddingTop(8.pt)) { "Thank you for using Swypex." }
            link("Visit the help center", href = "https://example.com/support")
        }
    }
}.render()

java.io.File("overview.pdf").writeBytes(bytes)
```


Use `container(weight = 1f) { ... }` inside a row to control a child’s share of the remaining width, or `container(width = 100.pt) { ... }` for a fixed width. Without explicit sizing, row children share the remaining width equally.

Use `TextColor(Color.Blue)` for text and `Background(Color.Blue50)` for backgrounds. Both are styles and can be combined with `+`; `Color` itself is a color value, not a style.


### Java

Java has a builder API with ordinary lambdas and reusable, immutable styles:

```java
import com.swypex.pdfx.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreatePdf {
    public static void main(String[] args) throws Exception {
        JavaStyle label = Pdf.style().size(9).textColor(PdfColors.BLUE).bold();
        JavaStyle card = Pdf.style().background(PdfColors.BLUE_50)
            .rounded(8).padding(16).spacing(4);

        byte[] bytes = Pdf.create(document -> {
            document.page(page -> {
                page.column(Pdf.style().spacing(16), column -> {
                    column.heading("Monthly overview");
                    column.card(card, content -> {
                        content.text("YOUR ACCOUNT", label);
                        content.heading("EGP 128,450.00", 2);
                    });
                    column.table(Pdf.style().padding(8), table -> {
                        table.header("Description", "Amount");
                        table.row("Cloud services", "EGP 6,750.00");
                    });
                });
            });
        }).render();

        Files.write(Path.of("overview.pdf"), bytes);
    }
}
```

Use `document.page(width, height, margin, block)` for a custom page size in points. Styles can be reused and extended with `.and(...)`; each style method returns a new style. Java projects need the PDF library and its transitive Kotlin runtime dependencies on their classpath.


## Custom fonts

Set `fonts` on the document builder to embed TrueType font files. Supply regular and bold variants to use with normal text and `Bold`:

```kotlin
import com.swypex.pdfx.*
import java.io.File

val bytes = pdf {
    fonts = FontFamily(
        regular = File("fonts/MyFont-Regular.ttf"),
        bold = File("fonts/MyFont-Bold.ttf"),
    )
    page {
        text { "Regular text" }
        text(Bold) { "Bold text" }
    }
}.render()
```

In Java, set the same fonts inside `Pdf.create`:

```java
PdfDocument document = Pdf.create(pdf -> {
    pdf.fonts(new File("fonts/MyFont-Regular.ttf"), new File("fonts/MyFont-Bold.ttf"));
    pdf.page(page -> page.text("Custom font"));
});
byte[] bytes = document.render();
```

Custom fonts must contain every character used in the document, including Arabic presentation forms when rendering Arabic.
Without Latin text uses Helvetica and Arabic text uses the bundled Cairo as defaults.

## License

Copyright © 2026 Swype Technologies Holding B.V.. All Rights Reserved.

This project is licensed under the [Apache License 2.0](LICENSE) (Apache-2.0).

Third-party components retain their own licenses, including the [SIL Open Font License](pdfx/src/main/resources/com/swypex/pdfx/fonts/OFL.txt) for the bundled Cairo fonts.
