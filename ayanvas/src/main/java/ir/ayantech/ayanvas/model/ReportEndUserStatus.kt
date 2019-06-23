package ir.ayantech.ayanvas.model

internal data class ReportEndUserStatusInput(
    val ApplicationVersion: String,
    val ExtraInfo: Any?,
    val OperatorName: String
)

internal data class ReportEndUserStatusOutput(
    val RegistrationStatus: String,
    val Subscribed: Boolean
)