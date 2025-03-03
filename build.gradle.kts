plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
    id("io.freefair.lombok") version "8.6"
}

group = "si.vegamind"
version = "1.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.2.1.10")
    type.set("AI") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set("251.*")
    }

    signPlugin {
        val certChainEnv = providers.environmentVariable("CERTIFICATE_CHAIN_PATH")
        val privateKeyEnv = providers.environmentVariable("PRIVATE_KEY_PATH")
        val keyPassEnv = providers.environmentVariable("PRIVATE_KEY_PASSWORD")

        if (certChainEnv.isPresent && privateKeyEnv.isPresent && keyPassEnv.isPresent) {
            certificateChainFile.set(file(certChainEnv))
            privateKeyFile.set(file(privateKeyEnv))
            password.set(keyPassEnv)
        }
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
