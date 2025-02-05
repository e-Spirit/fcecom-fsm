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
    apply(plugin = "java-library")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
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
        compileOnly(rootProject.libs.fs.isolated.runtime)

        implementation("com.nimbusds:nimbus-jose-jwt:10.0.1")
    }
}
