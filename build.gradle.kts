plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
}

var paperweightVersion = "26.1.2.build.+"
var minecraftVersion = "26.1.2"
var bStatsVersion = "3.2.1"

dependencies {
    paperweight.paperDevBundle(paperweightVersion)

    implementation("org.bstats:bstats-bukkit:$bStatsVersion")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion(minecraftVersion)
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    shadowJar {
        configurations = project.configurations.runtimeClasspath.map { setOf(it) }

        dependencies {
            exclude { it.moduleGroup != "org.bstats" }
        }

        relocate("org.bstats", project.group.toString())
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
