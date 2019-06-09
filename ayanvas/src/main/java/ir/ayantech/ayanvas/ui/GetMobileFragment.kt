package ir.ayantech.ayanvas.ui

import android.content.Intent
import android.view.View
import com.android.billingclient.util.IabHelper
import com.android.billingclient.util.MarketIntentFactorySDK
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.dialog.YesNoDialog
import ir.ayantech.ayanvas.helper.loadBase64
import ir.ayantech.ayanvas.helper.setHtmlText
import ir.ayantech.ayanvas.helper.setOnTextChange
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.model.GetServiceInfoAction
import ir.ayantech.ayanvas.model.ReportMtnSubscriptionInput
import ir.ayantech.ayanvas.model.RequestMciSubscriptionInput
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.fragment_get_mobile.*
import net.jhoobin.jhub.CharkhoneSdkApp

class GetMobileFragment : FragmentationFragment() {

    lateinit var iabHelper: IabHelper

    override fun getLayoutId(): Int = R.layout.fragment_get_mobile

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
                    CharkhoneSdkApp.initSdk(
                        activity?.applicationContext,
                        getResponseOfGetServiceInfo()?.Secrets?.toTypedArray()
                    )
                    val fillInIntent = Intent()
                    fillInIntent.putExtra("msisdn", mobileNumberEt.text.toString())
                    fillInIntent.putExtra("editAble", false)
                    iabHelper = IabHelper(
                        activity,
                        getResponseOfGetServiceInfo()?.PublicKeyBase64,
                        MarketIntentFactorySDK(true)
                    )
                    iabHelper.setFillInIntent(fillInIntent)
                    iabHelper.startSetup {
                        if (!it.isSuccess) return@startSetup
                        iabHelper.launchSubscriptionPurchaseFlow(
                            activity,
                            getResponseOfGetServiceInfo()?.Sku,
                            400
                        ) { iabResult, purchase ->
                            if (iabResult.isFailure) {
                                (activity as AuthenticationActivity).doCallBack(SubscriptionResult.CANCELED)
                                return@launchSubscriptionPurchaseFlow
                            }
                            getAyanApi().ayanCall<Void>(
                                AyanCallStatus {
                                    success {
                                        VasUser.saveMobile(activity!!, mobileNumberEt.text.toString())
                                        (activity as AuthenticationActivity).doCallBack(SubscriptionResult.OK)
                                    }
                                },
                                EndPoint.ReportMtnSubscription,
                                ReportMtnSubscriptionInput(
                                    purchase.developerPayload,
                                    purchase.isAutoRenewing,
                                    purchase.itemType,
                                    purchase.msisdn,
                                    purchase.orderId,
                                    purchase.originalJson,
                                    purchase.packageName,
                                    purchase.purchaseTime,
                                    purchase.purchaseState,
                                    purchase.signature,
                                    purchase.sku,
                                    purchase.token
                                )
                            )
                        }
                    }
                }
            }
            chooseOperatorTv.setOnClickListener {
                start(ChooseOperatorFragment())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }
}