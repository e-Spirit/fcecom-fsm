dependencies {
    api(rootProject.libs.fcaf.server.scope)

    implementation(rootProject.libs.gson)
    compileOnly(rootProject.libs.guava)
    implementation(rootProject.libs.minifier)
    implementation(rootProject.libs.nimbus.jose.jwt)
}
