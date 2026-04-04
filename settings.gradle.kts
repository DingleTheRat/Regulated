pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
    val loom_version: String by settings
    plugins {
        id("net.fabricmc.fabric-loom") version loom_version
    }
}

rootProject.name = "Regulated"
