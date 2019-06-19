package ir.ayantech.ayanvas.model

data class ReportEndUserStatusInput(
    val ApplicationVersion: String,
    val ExtraInfo: Any?,
    val OperatorName: String
)

data class ReportEndUserStatusOutput(
    val RegistrationStatus: String,
    val Subscribed: Boolean
)