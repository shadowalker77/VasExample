package ir.ayantech.ayanvas.model

internal data class ReportEndUserStatusInput(
    val ApplicationVersion: String,
    val ExtraInfo: Any?,
    val OperatorName: String
)

internal data class ReportEndUserStatusOutput(
    val PageState: String
)

internal class EndUserStatus {
    companion object {
        const val FirstPage = "FirstPage"
        const val SecondPage = "SecondPage"
        const val Subscribe = "Subscribe"
        const val Unsubscribe = "Unsubscribe"
    }
}