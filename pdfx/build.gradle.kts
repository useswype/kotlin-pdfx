plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("com.gradleup.nmcp")
    signing
}

kotlin { jvmToolchain(17) }

java {
    withSourcesJar()
    withJavadocJar()
}

// The library is written in Kotlin, so Java's Javadoc task has no API to document.
tasks.named<Jar>("javadocJar") { from(rootProject.file("README.md")) }

dependencies {
    implementation("com.github.librepdf:openpdf:2.0.3")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.18")
    implementation("org.apache.xmlgraphics:batik-codec:1.18")
    testImplementation("org.apache.pdfbox:pdfbox:2.0.24")
    testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "Swypex PDFx"
                description =
                    """
                    A Kotlin and Java DSL for creating PDF documents easily.
                    Expressive DSL allows to create beautiful PDF documents easily.
                """
                url = "https://github.com/useswype/kotlin-pdfx"
                developers {
                    developer {
                        name = "Swype Technologies Holding B.V."
                        url = "https://github.com/useswype/kotlin-pdfx/graphs/contributors"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/useswype/kotlin-pdfx.git"
                    developerConnection = "scm:git:ssh://git@github.com/useswype/kotlin-pdfx.git"
                    url = "https://github.com/useswype/kotlin-pdfx"
                }
                licenses {
                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }
            }
        }
    }
}

val unsignedLocal =
    providers.gradleProperty("unsignedLocal").map(String::toBoolean).getOrElse(false)

tasks
    .matching {
        it.name.startsWith("publishAllPublicationsToCentral") ||
            it.name.startsWith("nmcpPublishAllPublicationsToCentral")
    }
    .configureEach {
        doFirst {
            require(!unsignedLocal) { "unsignedLocal cannot be used for Central Portal uploads" }
        }
    }

tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst { require(!unsignedLocal) { "unsignedLocal is only for publishToMavenLocal" } }
}

signing {
    val key = providers.gradleProperty("signingKey").orNull
    val password = providers.gradleProperty("signingPassword").orNull
    val keyId = providers.gradleProperty("signingKeyId").orNull?.takeIf { it.isNotBlank() }

    if (key != null) {
        if (keyId != null) useInMemoryPgpKeys(keyId, key, password)
        else useInMemoryPgpKeys(key, password)
    }

    if (!unsignedLocal) {
        sign(publishing.publications["maven"])
    }
}
