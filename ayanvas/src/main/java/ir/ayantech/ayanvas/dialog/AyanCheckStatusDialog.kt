package ir.ayantech.ayanvas.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.irozon.sneaker.Sneaker
import ir.ayantech.ayannetworking.api.AyanApi
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayannetworking.api.AyanCommonCallStatus
import ir.ayantech.ayannetworking.api.CallingState
import ir.ayantech.ayannetworking.ayanModel.Failure
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.RequestHandler
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.core.VersionControl
import ir.ayantech.ayanvas.helper.InformationHelper
import ir.ayantech.ayanvas.model.*
import ir.ayantech.ayanvas.ui.AuthenticationActivity
import kotlinx.android.synthetic.main.ayan_dialog_check_status.*

class AyanCheckStatusDialog(
    val activity: Activity,
    private val applicationUniqueToken: String,
    private val isProduction: Boolean,
    val callback: (SubscriptionResult) -> Unit
) : Dialog(activity) {

    private lateinit var requestHandler: RequestHandler

    private val ayanCommonCallingStatus = AyanCommonCallStatus {
        failure {
            showNoInternetLayout(it)
        }
        changeStatus {
            when (it) {
                CallingState.NOT_USED -> progressBar.hideProgressBar()
                CallingState.LOADING -> {
                    progressBar.showProgressBar()
                    try {
                        Sneaker.hide()
                    } catch (e: Exception) {
                    }
                }
                CallingState.FAILED -> progressBar.hideProgressBar()
                CallingState.SUCCESSFUL -> progressBar.hideProgressBar()
            }
        }
    }

    private val versionControl = VersionControl(activity, applicationUniqueToken, ayanCommonCallingStatus)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.ayan_dialog_check_status)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        versionControl.checkForNewVersion {
            dismiss()
            if (it)
                start()
            else
                activity.finish()
        }
    }

    private fun showNoInternetLayout(failure: Failure) {
        retryRl.visibility = View.VISIBLE
        errorTv.text = failure.failureMessage
        retryTv.setOnClickListener {
            retryRl.visibility = View.GONE
            failure.reCallApi()
        }
    }

    private fun start() {
        if (VasUser.getSession(activity).isEmpty()) {
            startSubscription(activity, applicationUniqueToken, EndUserStatus.FirstPage, isProduction, callback)
        } else {
            AyanApi(
                { VasUser.getSession(activity) },
                "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
            ).ayanCall<ReportEndUserStatusOutput>(
                AyanCallStatus {
                    success {
                        when (it.response?.Parameters?.PageState) {
                            EndUserStatus.Subscribe -> callback(SubscriptionResult.OK)
                            EndUserStatus.FirstPage -> startSubscription(
                                activity,
                                applicationUniqueToken,
                                it.response?.Parameters?.PageState!!,
                                isProduction,
                                callback
                            )
                            EndUserStatus.SecondPage -> startSubscription(
                                activity,
                                applicationUniqueToken,
                                it.response?.Parameters?.PageState!!,
                                isProduction,
                                callback
                            )
                            EndUserStatus.Unsubscribe -> {
                                logout(activity)
                                startSubscription(
                                    activity,
                                    applicationUniqueToken,
                                    it.response?.Parameters?.PageState!!,
                                    isProduction,
                                    callback
                                )
                            }
                        }
                    }
                },
                EndPoint.ReportEndUserStatus,
                ReportEndUserStatusInput(
                    InformationHelper.getApplicationVersion(activity), ReportNewDeviceInput(
                        InformationHelper.getApplicationName(activity),
                        InformationHelper.getApplicationType(activity),
                        VasUser.getApplicationUniqueId(activity),
                        applicationUniqueToken,
                        InformationHelper.getApplicationVersion(activity),
                        ReportNewDeviceExtraInfo(
                            activity.packageName,
                            InformationHelper.getInstalledApps(activity),
                            InformationHelper.getApplicationVersion(activity)
                        ),
                        InformationHelper.getOperatorName(activity)
                    ), InformationHelper.getOperatorName(activity)
                ),
                commonCallStatus = ayanCommonCallingStatus
            )
        }
    }

    private fun startSubscription(
        activity: Activity,
        applicationUniqueToken: String,
        endUserStatus: String,
        isProduction: Boolean,
        callback: (SubscriptionResult) -> Unit
    ) {
        requestHandler = RequestHandler(
            activity, AuthenticationActivity.getProperIntent(
                activity,
                applicationUniqueToken,
                endUserStatus,
                isProduction
            ),
            callback
        )
        requestHandler.startForResult()
    }

    fun isUserSubscribed(activity: Activity, callback: (Boolean?) -> Unit) {
        if (VasUser.getSession(activity).isEmpty())
            callback(false)
        else
            AyanApi(
                { VasUser.getSession(activity) },
                "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
            ).ayanCall<DoesEndUserSubscribedOutput>(
                AyanCallStatus {
                    success {
                        callback(it.response?.Parameters?.Subscribed ?: false)
                    }
                },
                EndPoint.DoesEndUserSubscribed,
                commonCallStatus = AyanCommonCallStatus {
                    failure {
                        callback(null)
                    }
                }
            )
    }

    fun logout(activity: Activity) {
        AyanApi(
            { VasUser.getSession(activity) },
            "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
        ).ayanCall<Void>(AyanCallStatus { }, EndPoint.ReportUnsubscription)
        VasUser.removeUserMobileNumber(activity)
        VasUser.removeSession(activity)
    }

    fun getTokenInfo(context: Context, callback: (TokenInfo) -> Unit) {
        AyanApi(
            { VasUser.getSession(context) },
            "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
        ).ayanCall<TokenInfo>(
            AyanCallStatus {
                success {
                    callback(it.response?.Parameters!!)
                }
            },
            EndPoint.GetTokenInfo
        )
    }
}