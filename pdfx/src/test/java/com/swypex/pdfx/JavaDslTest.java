package com.swypex.pdfx;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class JavaDslTest {
    @Test
    void javaPaletteMatchesEveryKotlinColor() throws Exception {
        int checked = 0;
        for (var method : Color.Companion.getClass().getDeclaredMethods()) {
            if (!method.getName().matches("get[A-Z][a-z]+[0-9]*") || method.getReturnType() != Color.class) continue;
            String name = method.getName().substring(3).replaceFirst("([a-z])([0-9])", "$1_$2").toUpperCase(java.util.Locale.ROOT);
            assertSame(method.invoke(Color.Companion), PdfColors.class.getField(name).get(null), name);
            checked++;
        }
        assertTrue(checked > 150, "Expected the full color palette");
    }

    @Test
    void javaBuildersComposeAndRender() throws Exception {
        JavaStyle label = Pdf.style().size(12).textColor(PdfColors.BLUE);
        JavaStyle boldLabel = label.bold();
        Consumer<JavaComponents> greeting = c -> c.text("Reusable component", label);
        PdfDocument pdf = Pdf.create(doc -> doc.page(300, 400, 20, page -> {
            page.header(header -> header.text("Page header"));
            page.footer(8, footer -> footer.pageNumber(Pdf.style().align(TextAlign.Right), "Page "));
            page.column(Pdf.style().spacing(8), column -> {
                column.heading("Java overview", 2, boldLabel);
                column.row(row -> {
                    row.container(1f, item -> item.card(Pdf.style().background(PdfColors.BLUE_50).padding(8).rounded(4), greeting));
                    row.containerWithWidth(60, item -> item.text("Fixed width"));
                });
                column.table(Pdf.style().padding(4).border(PdfColors.BLUE, 1), table -> {
                    table.header(Pdf.style().background(PdfColors.GRAY_100), "Item", "Amount");
                    table.row("Service", "100 EGP");
                });
            });
        }));
        try (PDDocument document = PDDocument.load(pdf.render())) {
            assertEquals(300, document.getPage(0).getMediaBox().getWidth(), 0.01);
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Java overview"));
            assertTrue(text.contains("Reusable component"));
            assertTrue(text.contains("Fixed width"));
            assertTrue(text.contains("Service"));
            assertTrue(text.contains("Page header"));
            assertTrue(text.contains("Page 1"));
        }
    }
}
