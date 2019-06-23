package ir.ayantech.ayanvas.model

internal data class ReportMtnSubscriptionInput(
    val DeveloperPayload: String,
    val IsAutoRenewing: Boolean,
    val ItemType: String,
    val MobileNumber: String,
    val OrderID: String,
    val OriginalJson: String,
    val PackageName: String,
    val PurchaseDateTime: Long,
    val PurchaseState: Int,
    val Signature: String,
    val Sku: String,
    val Token: String
)