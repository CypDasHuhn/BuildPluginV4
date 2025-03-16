package de.cypdashuhn.rooster.core

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RoosterIgnore

fun hasRoosterIgnore(instance: Any): Boolean {
    return instance::class.annotations.any { it is RoosterIgnore }
}