val guavaVersion: String by project
val gsonVersion: String by project

dependencies {
    api(group = "com.espirit.moddev.fcaf", name = "fcaf-server-scope", version = "1.9.0")

    implementation(group = "com.google.code.gson", name = "gson", version = gsonVersion)
    compileOnly(group = "com.google.guava", name = "guava", version = guavaVersion)
}
