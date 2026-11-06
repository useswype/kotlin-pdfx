pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement { repositories { mavenCentral() } }

rootProject.name = "kotlin-pdfx"

include("pdfx", "example")
