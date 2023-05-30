buildscript {
    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
    }
}

repositories {
    maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
        credentials {
            username = extra.properties["artifactory_hosting_username"] as String
            password = extra.properties["artifactory_hosting_password"] as String
        }
    }
}

plugins {
    id ("java-library")
}

subprojects {
    val fsRuntimeVersion: String by project

    apply(plugin = "java-library")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(11)
    }

    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
    }

    dependencies {
        compileOnly (group = "de.espirit.firstspirit", name = "fs-isolated-runtime", version = fsRuntimeVersion)
    }
}
