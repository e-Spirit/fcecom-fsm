pluginManagement {
    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "my-commerce-module"
include("connect-for-commerce-module")
include("connect-for-commerce-scope-module")
include("connect-for-commerce-scope-server")
include("connect-for-commerce-server-only")
