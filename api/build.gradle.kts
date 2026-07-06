plugins {
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = "EventCore API"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.canvasmc.io/snapshots")
}

dependencies {
    compileOnlyApi(libs.canvas.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

publishing {
    repositories {
        maven {
            name = "allay"
            url = uri("https://repo.allay-studios.com/public")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "eventcore-api"
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks {
    jar {
        archiveBaseName.set("${rootProject.name}API")
    }

    named<Jar>("sourcesJar") {
        archiveBaseName.set("${rootProject.name}API")
    }
}
