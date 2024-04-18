val guavaVersion: String by project
val gsonVersion: String by project

plugins {
    id("de.espirit.firstspirit-module-annotations") version "4.4.2"
}

dependencies {
    implementation(project(":connect-for-commerce-scope-server"))

    implementation(group = "com.espirit.moddev.fcaf", name = "fcaf-module-scope", version = "1.9.0")
    implementation(group = "com.google.guava", name = "guava", version = guavaVersion)
    implementation(group = "com.google.code.gson", name = "gson", version = gsonVersion)
}
