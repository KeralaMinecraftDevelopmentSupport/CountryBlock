plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.bloodredx"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.json:json:20231013")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }
    
    shadowJar {
        archiveFileName.set("CountryBlock-${project.version}.jar")
        relocate("org.bstats", "com.github.bloodredx.countryblock.library.org.bstats")
        relocate("org.json", "com.github.bloodredx.countryblock.library.org.json")
    }
    
    build {
        dependsOn(shadowJar)
    }
}