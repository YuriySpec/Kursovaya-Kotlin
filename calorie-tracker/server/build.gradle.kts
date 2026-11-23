// Серверная часть приложения «Счётчик калорий».
// Ktor 3 — реализация REST API, Exposed — доступ к базе данных,
// HikariCP — пул соединений с СУБД PostgreSQL.
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

val ktorVersion = "3.1.2"
val exposedVersion = "0.59.0"

dependencies {
    // Платформа Ktor: маршрутизация, сериализация, аутентификация, обработка ошибок
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Доступ к СУБД
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:6.2.1")
    runtimeOnly("org.xerial:sqlite-jdbc:3.47.1.0")

    // Хеширование паролей
    implementation("at.favre.lib:bcrypt:0.10.2")

    implementation("ch.qos.logback:logback-classic:1.5.12")

    // Тестирование
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Используем Java 21 (которая встроена в Android Studio)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Компилируем Kotlin под ту же версию 21
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.example.calorietracker.server.ApplicationKt")
}