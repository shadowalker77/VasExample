package ir.ayantech.ayanvas.ui

import android.view.View
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.dialog.AyanYesNoDialog
import ir.ayantech.ayanvas.helper.loadBase64
import ir.ayantech.ayanvas.helper.setHtmlText
import ir.ayantech.ayanvas.helper.setOnTextChange
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.model.EndUserStatus
import ir.ayantech.ayanvas.model.GetServiceInfoAction
import ir.ayantech.ayanvas.model.RequestMciSubscriptionInput
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.ayan_vas_fragment_get_mobile.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity


internal class GetMobileFragment : FragmentationFragment() {

    var endUserStatus: String? = null

    private var guideView: GuideView? = null

    override fun getLayoutId(): Int = R.layout.ayan_vas_fragment_get_mobile

    override fun onCreate() {
        super.onCreate()
        mobileNumberEt.setText(VasUser.getMobile(activity!!))
        mobileNumberEt.setOnTextChange { if (it.length == 11) hideSoftInput() }
        with(getResponseOfGetServiceInfo()!!) {
            if (!CanChangeOperator) chooseOperatorIv.visibility = View.GONE
            iconIv.loadBase64(ImageBase64)
            descriptionTv.setHtmlText(FirstPageContent)
            if (AgreementLabel.isNullOrEmpty())
                showAgreementTv.visibility = View.GONE
            showAgreementTv.setHtmlText(AgreementLabel)
            nextTv.setHtmlText(FirstPageButton)
            if (PriceText.isNullOrEmpty()) priceTv.visibility = View.GONE else priceTv.setHtmlText(PriceText)
            showAgreementTv.setOnClickListener {
                val yesNoDialog = AyanYesNoDialog(activity!!, AgreementTitle, AgreementContent)
                yesNoDialog.removeNegativeButton()
                yesNoDialog.setPositiveText(AgreementButton)
                yesNoDialog.setPositiveAction(View.OnClickListener { yesNoDialog.dismiss() })
                yesNoDialog.show()
            }
            nextTv.setOnClickListener {
                if (getResponseOfGetServiceInfo()?.Action == GetServiceInfoAction.MCI_REGISTER) {
                    getAyanApi().ayanCall<Void>(
                        AyanCallStatus {
                            success {
                                start(EnterActivationFragment().also {
                                    VasUser.saveMobile(activity!!, mobileNumberEt.text.toString())
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
            chooseOperatorIv.setOnClickListener {
                start(ChooseOperatorFragment())
            }
        }
        if (endUserStatus == EndUserStatus.SecondPage) start(EnterActivationFragment())
    }

    fun keyboardStatusHandler(it: Boolean) {
        if (getResponseOfGetServiceInfo()?.MobileNumberInputHint != null)
            if (it) {
                try {
                    guideView = GuideView.Builder(activity)
                        .setContentText(getResponseOfGetServiceInfo()?.MobileNumberInputHint)
                        .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                        .setTargetView(mobileNumberEt)
                        .setGravity(Gravity.auto)
                        .setContentTextSize(14)//optional
                        .build()
                    guideView?.show()
                } catch (e: Exception) {
                }
            } else
                try {
                    guideView?.dismiss()
                    guideView = null
                } catch (e: Exception) {

                }
    }
}