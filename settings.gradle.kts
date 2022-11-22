buildscript {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.minecraftforge.net/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("com.anatawa12.forge:ForgeGradle:1.2-1.1.+")
    }
}

rootProject.name = "Forge-1.7.10-Client-Base"