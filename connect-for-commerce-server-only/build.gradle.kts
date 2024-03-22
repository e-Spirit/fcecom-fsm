plugins {
    id("de.espirit.firstspirit-module-annotations") version "4.4.2"
}

dependencies {
    implementation(project(":connect-for-commerce-scope-module"))
    implementation(project(":connect-for-commerce-scope-server"))

    implementation(group = "com.espirit.moddev.fcaf", name = "fcaf-module-scope", version = "1.9.0")
    implementation(group = "info.clearthought", name = "table-layout", version = "4.3.0")
    compileOnly(group = "com.espirit.caas", name = "caas-connect-global", version = "3.50.0", classifier = "all")
    implementation(group = "com.google.guava", name = "guava", version = "33.0.0-jre")
    implementation(group = "com.konghq", name = "unirest-java", version = "3.13.4")

    constraints {
        implementation("commons-codec:commons-codec:1.15") {
            because("version < 1.13 has cve https://devhub.checkmarx.com/cve-details/Cxeb68d52e-5509/")
        }
        implementation("com.google.code.gson:gson:2.8.9") {
            because("version < 2.8.9 has cve https://devhub.checkmarx.com/cve-details/CVE-2022-25647/")
        }
    }
}
