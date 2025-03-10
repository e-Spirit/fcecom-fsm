plugins {
    id("de.espirit.firstspirit-module-annotations") version "6.4.1"
}

dependencies {
    implementation(project(":connect-for-commerce-scope-server"))

    implementation(rootProject.libs.fcaf.module.scope)
    implementation(rootProject.libs.guava)
    implementation(rootProject.libs.gson)
    implementation(rootProject.libs.commons.lang3)
    implementation(rootProject.libs.nimbus.jose.jwt)
}
