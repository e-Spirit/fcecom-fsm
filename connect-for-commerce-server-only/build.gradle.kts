plugins {
    id("de.espirit.firstspirit-module-annotations") version "6.4.1"
}

dependencies {
    implementation(project(":connect-for-commerce-scope-module"))
    implementation(project(":connect-for-commerce-scope-server"))

    implementation(rootProject.libs.fcaf.module.scope)
    implementation(rootProject.libs.table.layout)
    implementation(rootProject.libs.guava)
    implementation(rootProject.libs.unirest.java)
}
