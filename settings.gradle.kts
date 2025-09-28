rootProject.name = "RClaim"

include(":plugin")
include(":api")

project(":api").name = "RClaim-api"
project(":plugin").name = "RClaim-plugin"


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/groups/public/")

        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")

        // PlaceholderAPI
        maven("https://repo.helpch.at/releases")

        // WorldGuard / EngineHub
        maven("https://maven.enginehub.org/repo/")

        // Dynmap
        maven("https://repo.mikeprimm.com/")

        // SirBlobman / CombatLogX & BlueSlimeCore
        maven("https://nexus.sirblobman.xyz/public/")

        // TriumphTeam
        maven("https://repo.triumphteam.dev/snapshots")

        // CodeMC (PvPManager)
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")

        // Dustplanet (SilkSpawners releases)
        maven("https://repo.dustplanet.de/artifactory/libs-release-local")

        // EldoNexus
        maven("https://eldonexus.de/repository/maven-releases/")

        // Rosewood
        maven("https://repo.rosewooddev.io/repository/public/")

        // Bluecolored
        maven("https://repo.bluecolored.de/releases")
        maven {
            name = "fancyinnovationsReleases"
            url = uri("https://repo.fancyinnovations.com/releases")
        }
        // FoliaLib
        maven("https://repo.tcoded.com/releases")
    }
}