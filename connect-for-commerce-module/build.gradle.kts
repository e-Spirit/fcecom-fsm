val fsModuleName: String by project
val fsDisplayName: String by project
val fsDescription: String by project
val fsVendor: String by project

plugins {
    id ("de.espirit.firstspirit-module") version "4.4.2"
    id ("maven-publish")
}

dependencies {
    fsServerCompile(project(":connect-for-commerce-scope-server"))

    fsModuleCompile(project(":connect-for-commerce-scope-module"))
    fsModuleCompile(project(":connect-for-commerce-server-only"))

    fsWebCompile(project(":connect-for-commerce-scope-server"))
    fsWebCompile(project(":connect-for-commerce-scope-module"))
}

firstSpiritModule {
    moduleName = fsModuleName
    displayName = fsDisplayName
    vendor = fsVendor
    description = fsDescription
}
