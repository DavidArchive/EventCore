plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version
description = "EventCore API test plugin"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.canvasmc.io/snapshots")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.canvas.api)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}
