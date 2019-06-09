package ir.ayantech.ayanvas.core

enum class SubscriptionResult(val value: Int) {
    OK(1), CANCELED(0);

    companion object {
        fun from(findValue: Int): SubscriptionResult = SubscriptionResult.values().first { it.value == findValue }
    }
}