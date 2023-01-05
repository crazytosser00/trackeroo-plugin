import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `kotlin-dsl`
    id("java-gradle-plugin")
    kotlin("jvm") version "1.5.10"
    kotlin("android") version "1.5.10" apply false
    application
}

group = "ru.roansa.trackeroo"
version = "0.1"

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
    maven( "https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    //TODO read more info about compileOnly
    compileOnly("com.android.tools.build:gradle:7.0.0")
    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-commons:9.4")
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//
//        }
//    }
//}

gradlePlugin {
    plugins {
        create("trackeroo-plugin") {
            id = "ru.roansa.trackeroo.trackeroo-plugin"
            group = "ru.roansa"
            version = "0.1-alpha"
            displayName = "Trackeroo plugin"
            description = "Lorem ipsum"
            implementationClass = "ru.roansa.trackeroo.trackeroo_plugin.ASMPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}