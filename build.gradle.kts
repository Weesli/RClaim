description = "RClaim-plugin"

plugins {
    `java-library`
    `maven-publish`
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

subprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven("https://jitpack.io")
    }
}