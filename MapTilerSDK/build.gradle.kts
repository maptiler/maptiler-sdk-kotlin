plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.21"

    `maven-publish`
    signing
}

tasks.named("ktlintFormat") {
    enabled = false
}

tasks.register<Copy>("copyPreCommitHook") {
    description = "Copy pre-commit git hook from the scripts to the .git/hooks folder."
    group = "git hooks"
    outputs.upToDateWhen { false }

    val root = rootProject.projectDir
    from("$root/scripts/pre-commit")
    into("$root/.git/hooks/")
}

tasks.preBuild {
    dependsOn("copyPreCommitHook")
}

// Library coordinates
group = "com.maptiler"
version = "1.0.0"

android {
    namespace = "com.maptiler.maptilersdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        aarMetadata {
            minCompileSdk = 30
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testFixtures {
        enable = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.material3:material3")

    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.runtime:runtime-android:1.8.3")
    implementation("androidx.compose.ui:ui-android:1.8.3")
    implementation("androidx.compose.foundation:foundation-android:1.8.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

// Generate a javadoc jar from Dokka HTML output (required by Maven Central)
val dokkaHtml by tasks.existing(org.jetbrains.dokka.gradle.DokkaTask::class)

tasks.register<org.gradle.jvm.tasks.Jar>("javadocJar") {
    dependsOn(dokkaHtml)
    from(dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<org.gradle.api.publish.maven.MavenPublication>("release") {
            // Defer attaching the Android 'release' component until it's created
            val releaseComponent = components.findByName("release")
            if (releaseComponent != null) {
                from(releaseComponent)
            } else {
                afterEvaluate {
                    from(components["release"])
                }
            }

            groupId = "com.maptiler"
            artifactId = "maptiler-sdk-kotlin"
            artifact(tasks["javadocJar"])

            pom {
                name.set("MapTiler SDK Kotlin")
                description.set(
                    "SDK designed to work with the well-established MapTiler Cloud service.",
                )
                url.set("https://github.com/maptiler/maptiler-sdk-kotlin")

                licenses {
                    license {
                        name.set("BSD-3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }

                developers {
                    developer {
                        id.set("maptiler")
                        name.set("MapTiler")
                        url.set("https://www.maptiler.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/maptiler/maptiler-sdk-kotlin.git")
                    developerConnection.set("scm:git:ssh://git@github.com/maptiler/maptiler-sdk-kotlin.git")
                    url.set("https://github.com/maptiler/maptiler-sdk-kotlin")
                }
            }
        }
    }
}

// Only sign if signing keys are provided via Gradle properties or environment
val shouldSign =
    (
        project.findProperty("signing.keyId") != null ||
            project.findProperty("signingKeyId") != null ||
            System.getenv("SIGNING_KEY") != null
    )

if (shouldSign) {
    signing {
        sign(publishing.publications)
    }
}
