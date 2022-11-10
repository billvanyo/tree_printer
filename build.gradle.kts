import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.1.8")
}

group = "tech.vanyo"
version = "1.1"
description = "Print binary trees in the terminal neatly with minimal overhead. Small dependency."

java {
    toolchain {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register<Test>("testLogging") {
    description = "Run test task with stdout/stderr logged"
    group = "verification"
    testLogging {
        outputs.upToDateWhen { false } // don't cache this task's result
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STARTED)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}