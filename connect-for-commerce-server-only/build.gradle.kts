val caasConnectVersion: String by project
val guavaVersion: String by project
val unirestVersion: String by project
val gsonVersion: String by project

plugins {
    id("de.espirit.firstspirit-module-annotations") version "4.4.2"
}

dependencies {
    implementation(project(":connect-for-commerce-scope-module"))
    implementation(project(":connect-for-commerce-scope-server"))

    implementation(group = "com.espirit.moddev.fcaf", name = "fcaf-module-scope", version = "1.9.0")
    implementation(group = "info.clearthought", name = "table-layout", version = "4.3.0")
    compileOnly(group = "com.espirit.caas", name = "caas-connect-global", version = caasConnectVersion, classifier = "all")
    implementation(group = "com.google.guava", name = "guava", version = guavaVersion)
    implementation(group = "com.konghq", name = "unirest-java", version = unirestVersion)
}
