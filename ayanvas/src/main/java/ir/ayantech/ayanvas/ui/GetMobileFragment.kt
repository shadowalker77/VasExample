package ir.ayantech.ayanvas.ui

import android.view.View
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.dialog.YesNoDialog
import ir.ayantech.ayanvas.helper.loadBase64
import ir.ayantech.ayanvas.helper.setHtmlText
import ir.ayantech.ayanvas.helper.setOnTextChange
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.model.GetServiceInfoAction
import ir.ayantech.ayanvas.model.RequestMciSubscriptionInput
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.ayan_vas_fragment_get_mobile.*

class GetMobileFragment : FragmentationFragment() {

    override fun getLayoutId(): Int = R.layout.ayan_vas_fragment_get_mobile

    override fun onCreate() {
        super.onCreate()
        mobileNumberEt.setOnTextChange { if (it.length == 11) hideSoftInput() }
        with(getResponseOfGetServiceInfo()!!) {
            if (!CanChangeOperator) chooseOperatorTv.visibility = View.GONE
            iconIv.loadBase64(ImageBase64)
            descriptionTv.setHtmlText(FirstPageContent)
            if (AgreementLabel.isNullOrEmpty())
                showAgreementTv.visibility = View.GONE
            showAgreementTv.setHtmlText(AgreementLabel)
            nextTv.text = FirstPageButton
            if (PriceText.isNullOrEmpty()) priceTv.visibility = View.GONE else priceTv.text = PriceText
            showAgreementTv.setOnClickListener {
                val yesNoDialog = YesNoDialog(activity!!, "قوانین و مقررات", AgreementContent)
                yesNoDialog.removeNegativeButton()
                yesNoDialog.setPositiveText("باشه")
                yesNoDialog.setPositiveAction(View.OnClickListener { yesNoDialog.dismiss() })
                yesNoDialog.show()
            }
            nextTv.setOnClickListener {
                if (getResponseOfGetServiceInfo()?.Action == GetServiceInfoAction.MCI_REGISTER) {
                    getAyanApi().ayanCall<Void>(
                        AyanCallStatus {
                            success {
                                start(EnterActivationFragment().also {
                                    it.mobileNumber = mobileNumberEt.text.toString()
                                })
                            }
                        },
                        EndPoint.RequestMciSubscription,
                        RequestMciSubscriptionInput(mobileNumberEt.text.toString())
                    )
                } else if (getResponseOfGetServiceInfo()?.Action == GetServiceInfoAction.MTN_REGISTER) {
                    (activity as AuthenticationActivity).irancellSubscription()
                }
            }
            chooseOperatorTv.setOnClickListener {
                start(ChooseOperatorFragment())
            }
        }
    }
}