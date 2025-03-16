plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "de.CypDasHuhn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    implementation(project(":ksp-processor"))
    ksp(project(":ksp-processor"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //bukkitLibrary("com.github.CypDasHuhn:Rooster:2fd1a0fa65")

    bukkitLibrary("com.google.code.gson:gson:2.10.1")
    testImplementation("com.google.code.gson:gson:2.10.1")

    implementation("org.reflections:reflections:0.9.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.2")
    implementation("com.google.code.gson:gson:2.11.0")

    bukkitLibrary("io.github.classgraph:classgraph:4.8.170")
    testImplementation("io.github.classgraph:classgraph:4.8.170")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.127.1")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.127.1")

    // exposed
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    bukkitLibrary("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")

    implementation(platform("com.intellectualsites.bom:bom-newest:1.52")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
}

val targetJavaVersion = 21
bukkit {
    name = "BuildPlugin"
    main = "de.cypdashuhn.build.BuildPlugin"
    apiVersion = "1.21.4"

    commands {
        register("!create")
        register("!delete")
        register("!edit")
        register("!load")
        register("!settings")
    }

    permissions {

    }

    depend = listOf("FastAsyncWorldEdit")
}
tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
    shadowJar {
        mergeServiceFiles()
    }
}



tasks.build {
    dependsOn("shadowJar")
}

