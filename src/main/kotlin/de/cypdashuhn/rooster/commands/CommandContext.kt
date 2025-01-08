package de.cypdashuhn.rooster.commands

class CommandContext(private val innerMap: MutableMap<String, Any> = mutableMapOf()) {
    operator fun get(key: String): Any? = innerMap[key]

    operator fun set(key: String, value: Any) {
        innerMap[key] = value
    }

    fun putIfAbsent(key: String, value: Any) = innerMap.putIfAbsent(key, value)

    fun toMap(): Map<String, Any> = innerMap.toMap()

    override fun toString(): String = innerMap.toString()
}