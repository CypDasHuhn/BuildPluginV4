package de.cypdashuhn.rooster.demo

abstract class DemoManager {
    abstract fun demoPrep()
}

fun List<DemoManager>.init() = this.forEach { it.demoPrep() }

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RoosterDemoTable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RoosterDemoManager