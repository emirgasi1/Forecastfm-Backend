import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

val secrets = Properties().apply {
    val f = rootProject.file("secrets.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.resources)

    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.contentNegotiation)

    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation("org.jetbrains.exposed:exposed-java-time:1.0.0")
    implementation(libs.postgresql)

    implementation(libs.jbcrypt)
    implementation("com.auth0:java-jwt:4.4.0")

    implementation(libs.logback.classic)
    implementation(libs.ktor.server.freemarker)

    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation(ktorLibs.server.testHost)
}

tasks.withType<JavaExec> {
    systemProperty("YOUTUBE_API_KEY", secrets.getProperty("YOUTUBE_API_KEY", ""))
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("started", "passed", "skipped", "failed")
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}