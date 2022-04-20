import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("jvm") version "1.6.20"
    `maven-publish`
}

group = "dev.virefire.kson"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            pom {
                name.set("KSON")
                description.set("Kotlin JSON library based on GSON")
            }
        }
    }
    repositories {
        maven {
            val properties = Properties()
            properties.load(rootProject.file("publish.properties").inputStream())
            url = uri(properties["deployRepoUrl"].toString())
            credentials {
                username = properties["deployRepoUsername"].toString()
                password = properties["deployRepoPassword"].toString()
            }
        }
    }
}