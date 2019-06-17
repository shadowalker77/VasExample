package ir.ayantech.ayanvas.core

enum class SubscriptionResult(val value: Int) {
    OK(1),
    CANCELED(0),
    NO_INTERNET_CONNECTION(-1),
    TIMEOUT(-2),
    UNKNOWN(-3);

    companion object {
        fun from(findValue: Int): SubscriptionResult = values().first { it.value == findValue }
    }
}