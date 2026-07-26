import java.util.Properties
import org.gradle.api.Project

/**
 * Resolves a secret build property, checking in order:
 *
 * 1. Environment variable of the same name.
 * 2. Gradle property of the same name (`-P` flag or `~/.gradle/gradle.properties`).
 * 3. `local.properties` at the repository root.
 * 4. [default], when provided.
 *
 * Surrounding double quotes are stripped so values read identically from every
 * source; call sites that feed a `String` buildConfigField add their own quotes.
 */
fun Project.secretProperty(name: String, default: String? = null): String {
    val value = providers.environmentVariable(name).orNull
        ?: providers.gradleProperty(name).orNull
        ?: rootLocalProperties().getProperty(name)
        ?: default
        ?: error(
            "Missing secret '$name'. Provide it as an environment variable, " +
                "a Gradle property, or an entry in local.properties.",
        )
    return value.removeSurrounding("\"")
}

private fun Project.rootLocalProperties(): Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}
