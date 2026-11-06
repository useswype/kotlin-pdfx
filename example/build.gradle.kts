plugins {
    kotlin("jvm")
    application
}

kotlin { jvmToolchain(17) }

dependencies { implementation(project(":pdfx")) }

application { mainClass = "com.swypex.pdfx.example.InvoiceKt" }

tasks.named<JavaExec>("run") { workingDir(rootProject.projectDir) }

tasks.register<JavaExec>("complexOverview") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.swypex.pdfx.example.ComplexOverviewKt"
    workingDir(rootProject.projectDir)
}

tasks.register<JavaExec>("arabicEnglish") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.swypex.pdfx.example.ArabicEnglishKt"
    workingDir(rootProject.projectDir)
}
