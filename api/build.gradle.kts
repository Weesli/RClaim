plugins {
    `java-library`
    `maven-publish`
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.Weesli"
            artifactId = "RClaim-api"
            version = project.version.toString()
            from(components["java"])
        }
    }
}


repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    api("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }