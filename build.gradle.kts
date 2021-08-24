plugins {
    java
    application
    idea
}

val junitVersion = "5.7.2"

group = "gwsoftwaretools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("vertexCover.application.TimeBenchmark")
}

idea.module {
    isDownloadJavadoc = true
    isDownloadSources = true
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
}
