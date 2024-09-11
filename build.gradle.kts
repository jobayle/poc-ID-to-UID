plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"
val bcVersion = "1.78.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcprov-jdk18on:$bcVersion")
    implementation("org.bouncycastle:bcpkix-jdk18on:$bcVersion")
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

application {
    mainClass = "poc.Main"
}

kotlin {
    jvmToolchain(17)
}
