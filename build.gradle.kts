val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgresql_version: String by project
val hikari_version: String by project
val exposed_version: String by project
val commons_codec_version: String by project

plugins {
    application
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.7"
}

group = "com.madteam"
version = "0.0.1"

application {
    mainClass.set("com.madteam.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    //PostgreSQL DB connection
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")

    //Negotiation
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")

    //Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    //Call logging
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")

    //Auth
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")

    implementation("commons-codec:commons-codec:$commons_codec_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
