pluginManagement {
    repositories {
        google() // Repositorio oficial de Android
        mavenCentral() // Repositorio Maven Central
        gradlePluginPortal() // Plugins adicionales para Gradle
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Agreducamandroid" // Nombre del proyecto
include(":app") // Incluye el módulo principal de la app