import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.24"
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.weesli"

dependencies {
    // API dependency
    api(project(":RClaim-api"))

    // Paper
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    // Vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")

    // DecentHolograms
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.5")

    // UpgradeableSpawners
    compileOnly("com.github.angeschossen:UpgradeableSpawnersAPI:4.1.1")

    // PlayerPoints
    compileOnly("org.black_ixx:playerpoints:3.2.6")

    // SpawnerMeta
    compileOnly("com.github.OfficialRell:SpawnerMeta:24.8")

    // LitMinions
    compileOnly("com.github.WaterArchery:LitMinionsAPI:3.1.7")

    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // Triumph commands
    implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.3")

    // Dynmap
    compileOnly("us.dynmap:DynmapCoreAPI:3.7-beta-6")

    // CombatLogX API
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")

    // BlueSlimeCore
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")

    // PvPManager
    compileOnly("me.NoChance.PvPManager:pvpmanager:3.18.21")

    // SmartSpawner
    compileOnly("com.github.NighterDevelopment:smartspawner:1.5.7.1")

    // RozsDB-Lite
    implementation("com.github.Weesli:RozsDB-Lite:1.1.1")

    // Rlib
    implementation("com.github.Weesli:Rlib:2.4.6")

    // FancyHolograms
    compileOnly("de.oliver:FancyHolograms:2.7.0")

    // DeluxeCombat
    compileOnly("com.github.timderspieler:DeluxeCombat-API:1.5.1")

    // RoseStacker
    compileOnly("dev.rosewood:rosestacker:1.5.37")

    implementation("com.github.Weesli:RozsConfig:1.2.1")

    implementation("com.tcoded:FoliaLib:0.5.1")
}

dependencies {
    compileOnly(files("lib/SilkSpawners_v2.jar"))
    compileOnly(files("lib/AxMinions-1.0.11-all.jar"))
    compileOnly(files("lib/JetsMinions-API.jar"))
    compileOnly(files("lib/BetterRTP-3.6.13.jar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

description = "RClaim-plugin"

tasks.shadowJar {
    archiveClassifier.set("")
    archiveFileName.set("RClaim-$version.jar")
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    archiveVersion.set(project.version.toString())
    mergeServiceFiles()

    relocate("org.bstats", "net.weesli.libs.bstats")
    relocate("com.tcoded.foliolib", "net.weesli.libs.foliolib")

}

tasks.jar { enabled = false }
configurations.configureEach {
    exclude(group = "org.bukkit", module = "bukkit")
    exclude(group = "org.spigotmc", module = "spigot-api")
}
tasks.named("build") { dependsOn(tasks.named("shadowJar")) }
tasks.named<ProcessResources>("processResources") {
    filteringCharset = "UTF-8"
    val projectVersion = project.version.toString()
    inputs.property("version", projectVersion)

    filesMatching("plugin.yml") {
        expand("version" to projectVersion)
    }
}