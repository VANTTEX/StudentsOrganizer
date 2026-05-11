plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    application
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.auth.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("auth-server.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-default-headers")

    // Database
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // JSON manipulation
    implementation("org.json:json:20240303")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
