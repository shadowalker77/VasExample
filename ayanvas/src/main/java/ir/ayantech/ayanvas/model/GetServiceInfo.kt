package ir.ayantech.ayanvas.model

internal data class GetServiceInfoOutput(
    val Action: String,
    val ActivationKey: String,
    val AgreementContent: String,
    val AgreementButton: String,
    val AgreementLabel: String?,
    val AgreementTitle: String?,
    val CanChangeOperator: Boolean,
    val MobileNumberInputHint: String?,
    val ActivationCodeInputHint: String?,
    val FirstPageButton: String,
    val FirstPageContent: String,
    val HeadNumber: String,
    val ImageBase64: String,
    val ImageUrl: String,
    val LandingPage: String,
    val Price: Long,
    val PriceText: String?,
    val PublicKeyBase64: String,
    val SecondPageButton: String,
    val SecondPageContent: String,
    val Secrets: List<String>,
    val ShowSliders: Boolean,
    val Sku: String,
    val Sliders: ArrayList<Slider>
)

internal data class Slider(
    val Description: String,
    val Image: String,
    val ImageBase64: String,
    val Title: String?
)

internal class GetServiceInfoAction {
    companion object {
        const val MCI_REGISTER = "MciRegister"
        const val MTN_REGISTER = "MtnRegister"
        const val CHOOSE_OPERATOR = "ChooseOperator"
        const val NOTHING = "Nothing"
    }
}