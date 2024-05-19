import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "dksu.ru"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    //implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.h2database:h2:2.2.222")

    //implementation("org.springdoc:springdoc-openapi-ui:1.6.3")
    implementation("javax.validation:validation-api:1.1.0.Final")
    implementation("org.springdoc","springdoc-openapi-starter-webmvc-ui", "2.0.2")

    implementation("org.telegram:telegrambots-spring-boot-starter:6.8.0")
    implementation("org.telegram:telegrambotsextensions:6.8.0")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3") // for JVM platform

    implementation("org.postgresql:postgresql")
//    implementation("tech.ydb.dialects:hibernate-ydb-dialect:0.9.0")
//    implementation("tech.ydb.jdbc:ydb-jdbc-driver:2.1.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    test {
        useJUnitPlatform()
    }
}


kotlin {
    jvmToolchain(17)
}