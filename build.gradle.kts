import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
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
    //implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.hibernate:hibernate-core:5.5.3.Final")
    //implementation("org.springdoc:springdoc-openapi-ui:1.6.3")
    implementation("javax.validation:validation-api:1.1.0.Final")
    implementation("org.springdoc","springdoc-openapi-starter-webmvc-ui", "2.0.2")
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