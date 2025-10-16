import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

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
                        email.set("support@maptiler.com")
                        organization.set("MapTiler")
                        organizationUrl.set("https://maptiler.com")
                        url.set("https://www.maptiler.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/maptiler/maptiler-sdk-kotlin.git")
                    developerConnection.set("scm:git:ssh://git@github.com/maptiler/maptiler-sdk-kotlin.git")
                    url.set("https://github.com/maptiler/maptiler-sdk-kotlin")
                }
            }

            // Suppress POM metadata warnings for test fixtures variants that
            // cannot be represented in Maven POM but are fine in Gradle module metadata.
            suppressPomMetadataWarningsFor("releaseTestFixturesVariantReleaseApiPublication")
            suppressPomMetadataWarningsFor("releaseTestFixturesVariantReleaseRuntimePublication")
        }
    }
    repositories {
        val isSnapshot = version.toString().endsWith("SNAPSHOT")
        maven {
            name = if (isSnapshot) "central-snapshots" else "ossrh-staging-api"
            url =
                uri(
                    if (isSnapshot) {
                        // Maven Central snapshots repository via Central Portal
                        "https://central.sonatype.com/repository/maven-snapshots/"
                    } else {
                        // Staging API compatibility endpoint for releases
                        "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
                    },
                )
            authentication {
                create<org.gradle.authentication.http.BasicAuthentication>("basic")
            }
            credentials(PasswordCredentials::class) {
                username =
                    (
                        (findProperty("sonatypeMaptilerTokenUsername") as String?)
                            ?.takeIf { it.isNotBlank() }
                    )
                        ?: System.getenv("SONATYPE_MAPTILER_TOKEN_USERNAME")
                password =
                    (
                        (findProperty("sonatypeMaptilerTokenPassword") as String?)
                            ?.takeIf { it.isNotBlank() }
                    )
                        ?: System.getenv("SONATYPE_MAPTILER_TOKEN_PASSWORD")
            }
        }
    }
}

// Sign all publications using in-memory PGP keys (required by Maven Central)
signing {
    val keyId =
        (
            (findProperty("sonatypeMaptilerSigningKeyId") as String?)
                ?.takeIf { it.isNotBlank() }
        )
            ?: System.getenv("SONATYPE_MAPTILER_SIGNING_KEY_ID")
    val rawKey =
        (
            (findProperty("sonatypeMaptilerSigningKey") as String?)
                ?.takeIf { it.isNotBlank() }
        )
            ?: System.getenv("SONATYPE_MAPTILER_SIGNING_KEY")
    val password =
        (
            (findProperty("sonatypeMaptilerSigningPassword") as String?)
                ?.takeIf { it.isNotBlank() }
        )
            ?: System.getenv("SONATYPE_MAPTILER_SIGNING_PASSWORD")

    // Only initialize/log for publish/sign tasks
    val signingRequired =
        gradle.startParameter.taskNames.any {
            it.contains("publish", ignoreCase = true) || it.contains("sign", ignoreCase = true)
        }

    if (signingRequired) {
        val useKeyNoId =
            (
                (findProperty("sonatypeMaptilerUseKeyNoId") as String?)
                    ?.equals("true", ignoreCase = true) == true
            ) || (System.getenv("SONATYPE_MAPTILER_USE_KEY_NO_ID")?.equals("true", ignoreCase = true) == true)

        if (!rawKey.isNullOrBlank() && !password.isNullOrBlank()) {
            if (useKeyNoId) {
                useInMemoryPgpKeys(rawKey, password)
            } else if (!keyId.isNullOrBlank()) {
                useInMemoryPgpKeys(keyId, rawKey, password)
            } else {
                logger.lifecycle(
                    "Signing not configured: SONATYPE_MAPTILER_SIGNING_KEY_ID missing;",
                )
            }

            // Use for local testing or when using different key formats.
            // useGpgCmd()

            sign(publishing.publications)
        } else {
            // Defer failure to tasks that require signing; helps local tasks (e.g., publishToMavenLocal)
            logger.lifecycle(
                "Signing not configured: provide SONATYPE_MAPTILER_SIGNING_KEY_ID/KEY/PASSWORD or matching -P properties.",
            )
        }
    }
}

// Helper task to upload a completed "Maven-like" deployment to the Central Publisher Portal
// so it becomes visible/manageable in https://central.sonatype.com/publishing
tasks.register("centralManualUpload") {
    group = "publishing"
    description = "POST to Central OSSRH Staging API manual upload endpoint for this namespace"
    doLast {
        val isSnapshot = version.toString().endsWith("SNAPSHOT")
        if (isSnapshot) {
            println("Snapshot version detected; manual upload is not required.")
            return@doLast
        }

        val username =
            (
                (findProperty("sonatypeMaptilerTokenUsername") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_TOKEN_USERNAME")
        val password =
            (
                (findProperty("sonatypeMaptilerTokenPassword") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_TOKEN_PASSWORD")
        val namespace =
            (
                (findProperty("sonatypeMaptilerNamespace") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_NAMESPACE")
                ?: "com.maptiler"
        val repositoryKey = (
            (findProperty("sonatypeMaptilerRepositoryKey") as String?)
                ?.takeIf { it.isNotBlank() }
        )

        val publishingType =
            (
                (findProperty("sonatypeMaptilerPublishingType") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_PUBLISHING_TYPE")
                ?: "user_managed"

        val allowedPublishingTypes = setOf("user_managed", "automatic", "portal_api")
        require(publishingType in allowedPublishingTypes) {
            "Invalid sonatypeMaptilerPublishingType: '$publishingType'. Allowed: $allowedPublishingTypes"
        }

        require(!username.isNullOrBlank()) { "SONATYPE_MAPTILER_TOKEN_USERNAME (or -PsonatypeMaptilerTokenUsername) is required" }
        require(!password.isNullOrBlank()) { "SONATYPE_MAPTILER_TOKEN_PASSWORD (or -PsonatypeMaptilerTokenPassword) is required" }

        val bearer = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        val base =
            if (repositoryKey != null) {
                "https://ossrh-staging-api.central.sonatype.com/manual/upload/repository/$repositoryKey"
            } else {
                "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$namespace"
            }
        val url = if (publishingType == "user_managed") base else "$base?publishing_type=$publishingType"

        val client = HttpClient.newHttpClient()
        println("Manual upload target: $url (namespace=$namespace, repoKey=${repositoryKey ?: "<default>"}, type=$publishingType)")
        val request =
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $bearer")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("Central manual upload response: ${response.statusCode()}\n${response.body()}")
        if (response.statusCode() >= 300) {
            throw GradleException("Manual upload failed: ${response.statusCode()} ${response.body()}")
        }
    }
}

// Drop a specific staging repository by key (e.g., from error message or search API)
tasks.register("centralDropRepository") {
    group = "publishing"
    description = "DELETE a specific OSSRH Staging API repository by key"
    doLast {
        val username =
            (
                (findProperty("sonatypeMaptilerTokenUsername") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_TOKEN_USERNAME")
        val password =
            (
                (findProperty("sonatypeMaptilerTokenPassword") as String?)
                    ?.takeIf { it.isNotBlank() }
            )
                ?: System.getenv("SONATYPE_MAPTILER_TOKEN_PASSWORD")
        val repositoryKey = (
            (findProperty("sonatypeMaptilerRepositoryKey") as String?)
                ?.takeIf { it.isNotBlank() }
        )

        require(!username.isNullOrBlank()) { "SONATYPE_MAPTILER_TOKEN_USERNAME (or -PsonatypeMaptilerTokenUsername) is required" }
        require(!password.isNullOrBlank()) { "SONATYPE_MAPTILER_TOKEN_PASSWORD (or -PsonatypeMaptilerTokenPassword) is required" }
        require(repositoryKey != null) { "-PsonatypeMaptilerRepositoryKey=<repository key> is required" }

        val bearer = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        val url = "https://ossrh-staging-api.central.sonatype.com/manual/drop/repository/$repositoryKey"

        val client = HttpClient.newHttpClient()
        println("Drop repository target: $url")
        val request =
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $bearer")
                .DELETE()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("Central drop response: ${response.statusCode()}\n${response.body()}")
        if (response.statusCode() >= 300) {
            throw GradleException("Drop failed: ${response.statusCode()} ${response.body()}")
        }
    }
}
