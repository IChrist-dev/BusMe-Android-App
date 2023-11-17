pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                // This should always be `mapbox`
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "sk.eyJ1IjoiaWNocmlzdGlhbi1kZXYtc3R1ZGVudCIsImEiOiJjbHAxcm42aDYwanl0MmttanRvbjlxNDQ0In0.T9IoCYFJijkumhSC0y0XSA"
            }
        }
    }
}

rootProject.name = "TransitApp"
include(":app")
 