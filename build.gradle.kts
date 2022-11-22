import net.minecraftforge.gradle.user.patch.UserPatchExtension
import net.minecraftforge.gradle.tasks.user.reobf.ReobfTask

plugins {
    idea
    java
}

apply {
    plugin("forge")
}

base.archivesName.set("ExampleMod")
version = "1.0"
group = "com.joaoshiozo"

println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + '(' + System.getProperty("java.vendor") + ") Arch: " + System.getProperty("os.arch"))

configure<UserPatchExtension> {
    version = "1.7.10-10.13.4.1614-1.7.10"
    runDir = "run"
    mappings = "stable_12"
}

sourceSets.main {
    java.srcDirs("src/main/java")
    resources.srcDirs("src/main/resources")
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://jcenter.bintray.com")
    maven("https://jitpack.io/")
}

val shade: Configuration by configurations.creating

dependencies {
    fun ModuleDependency.exclude(moduleName: String) = exclude(mapOf("module" to moduleName))
    shade("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        exclude("launchwrapper")
        exclude("guava")
        exclude("gson")
        exclude("commons-io")
        exclude("log4j-core")
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation(shade)
}

val MixinClient = "mixins.examplemod.json"
val MixinrefMap = "mixins.examplemod.refmap.json"

val refMap = "${tasks.compileJava.get().temporaryDir}" + File.separator + File(MixinrefMap)
val mixinSrg = "${tasks.getByName("reobf").temporaryDir}" + File.separator + File("mixins.srg")

val reobf: ReobfTask by tasks

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
        options.compilerArgs = listOf(
            "-Xlint:-sunapi", "-XDenableSunApiLintControl", "-XDignore.symbol.file",
            "-AreobfSrgFile=${reobf.srg}", "-AoutSrgFile=${mixinSrg}", "-AoutRefMapFile=${refMap}"
        )
    }

    processResources {
        inputs.property("version", "1.0")
        inputs.property("mcversion", "1.7.10")
        filesMatching("mcmod.info") {
            expand(mapOf("version" to "1.0", "mcversion" to "1.7.10"))
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                "FMLCorePluginContainsFMLMod" to "true",
                "FMLCorePlugin" to "dev.joaoshiozo.examplemod.core.ExampleModMixinLoader",
                "MixinConfigs" to "$MixinClient",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "TweakOrder" to 0,
                "ForceLoadAsMod" to "true"
            )
        }
        from(
            refMap,
            shade.map {
                if (it.isDirectory) it
                else zipTree(it)
            }
        )
        exclude(
            "com/jcraft/**",
            "ibxm/**",
            "org/apache/**",
            "org/intellij/**",
            "org/jetbrains/**",
            "LICENSE.txt",
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/MUMFREY.RSA",
            "META-INF/*.kotlin_module"
        )
    }

    named<ReobfTask>("reobf") {
        addExtraSrgFile(mixinSrg)
    }
}
