import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.21"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.github.simokhin"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

tasks.jar {
    enabled = false
}

val springCloudVersion = "2022.0.2"
val tgBotApiVersion = "7.0.1"
val openFeignVersion = "4.0.2"
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
    implementation("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    implementation("dev.inmo:tgbotapi:$tgBotApiVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
