plugins {
    kotlin("jvm") version "2.2.20" apply false
    id("com.diffplug.spotless") version "7.2.1"
    id("com.gradleup.nmcp.aggregation") version "1.6.2"
    id("com.gradleup.nmcp") version "1.6.2" apply false
}

allprojects {
    group = "com.swypex"
    version = providers.gradleProperty("releaseVersion").getOrElse("0.1.0")
}

spotless {
    kotlin {
        target("**/src/**/*.kt")
        ktfmt().kotlinlangStyle()
    }
    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        targetExclude("**/build/**")
        ktfmt().kotlinlangStyle()
    }
}

subprojects {
    tasks
        .matching { it.name == "check" }
        .configureEach { dependsOn(rootProject.tasks.named("spotlessCheck")) }
}

dependencies { nmcpAggregation(project(":pdfx")) }

nmcpAggregation {
    centralPortal {
        username = providers.gradleProperty("centralPortalUsername")
        password = providers.gradleProperty("centralPortalPassword")
        publishingType =
            providers.gradleProperty("centralPortalPublishingType").getOrElse("USER_MANAGED")
        publicationName = "com.swypex:pdfx:$version"
    }
}

tasks
    .matching {
        it.name.startsWith("publishAggregationToCentral") ||
            it.name.startsWith("nmcpPublishAggregationToCentral")
    }
    .configureEach {
        doFirst {
            require(
                !providers.gradleProperty("unsignedLocal").map(String::toBoolean).getOrElse(false)
            ) {
                "unsignedLocal cannot be used for Central Portal uploads"
            }
        }
    }
