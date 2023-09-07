val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val swagger_codegen_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"
}

group = "llesha"
version = "0.0.1"

application {
    mainClass.set("llesha.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:$swagger_codegen_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.4")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.4")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.4")
}
