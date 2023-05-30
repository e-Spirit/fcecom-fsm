dependencies {
    api (group = "com.espirit.moddev.fcaf", name = "fcaf-server-scope", version = "1.9.0")

    implementation (group = "com.konghq", name = "unirest-java", version = "3.13.4")
    compileOnly (group = "com.google.guava", name = "guava", version = "30.1-jre")

    constraints {
        implementation ("commons-codec:commons-codec:1.15") {
            because("version < 1.13 has cve https://devhub.checkmarx.com/cve-details/Cxeb68d52e-5509/")
        }
        implementation ("com.google.code.gson:gson:2.8.9") {
            because("version < 2.8.9 has cve https://devhub.checkmarx.com/cve-details/CVE-2022-25647/")
        }
    }
}
