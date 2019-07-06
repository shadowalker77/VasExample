package ir.ayantech.ayanvas.ui

import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.helper.loadBase64
import ir.ayantech.ayanvas.helper.setHtmlText
import ir.ayantech.ayanvas.helper.setOnTextChange
import ir.ayantech.ayanvas.model.ConfirmMciSubscriptionInput
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.ayan_vas_fragment_enter_activation.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity

internal class EnterActivationFragment : FragmentationFragment() {

    private var guideView: GuideView? = null

    override fun getLayoutId(): Int = R.layout.ayan_vas_fragment_enter_activation

    override fun onCreate() {
        super.onCreate()
        backIv.setOnClickListener { pop() }
        with(getResponseOfGetServiceInfo()!!) {
            iconIv.loadBase64(ImageBase64)
            descriptionTv.setHtmlText(SecondPageContent)
            nextTv.setHtmlText(SecondPageButton)
            activationCodeEt.setOnTextChange { if (it.length == 4) hideSoftInput() }
            nextTv.setOnClickListener {
                getAyanApi().ayanCall<Void>(
                    AyanCallStatus {
                        success {
                            (activity as AuthenticationActivity).doCallBack(SubscriptionResult.OK)
                        }
                    },
                    EndPoint.ConfirmMciSubscription,
                    ConfirmMciSubscriptionInput(activationCodeEt.text.toString())
                )
            }
            userMobileTv.text = ActivationCodeInputHint?.replace(
                "شماره خود",
                VasUser.getMobile(activity!!)
            )
        }
    }

    fun keyboardStatusHandler(it: Boolean) {
        if (getResponseOfGetServiceInfo()?.ActivationCodeInputHint != null)
            if (it) {
                try {
                    guideView = GuideView.Builder(activity)
                        .setContentText(
                            getResponseOfGetServiceInfo()?.ActivationCodeInputHint?.replace(
                                "شماره خود",
                                VasUser.getMobile(activity!!)
                            )
                        )
                        .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                        .setTargetView(activationCodeEt)
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