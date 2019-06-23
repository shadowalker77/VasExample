package ir.ayantech.ayanvas.core

import android.app.Activity
import ir.ayantech.ayannetworking.api.AyanApi
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayannetworking.api.AyanCommonCallStatus
import ir.ayantech.ayannetworking.ayanModel.FailureType
import ir.ayantech.ayanvas.model.*
import ir.ayantech.ayanvas.ui.AuthenticationActivity

internal class VasAuthentication {

    lateinit var requestHandler: RequestHandler

    private fun startSubscription(
        activity: Activity,
        applicationUniqueToken: String,
        callback: (SubscriptionResult) -> Unit
    ) {
        requestHandler = RequestHandler(
            activity, AuthenticationActivity.getProperIntent(
                activity,
                applicationUniqueToken
            ),
            callback
        )
        requestHandler.startForResult()
    }

    fun start(
        activity: Activity,
        applicationUniqueToken: String,
        callback: (SubscriptionResult) -> Unit
    ) {
        if (VasUser.getSession(activity).isEmpty()) {
            startSubscription(activity, applicationUniqueToken, callback)
        } else {
            AyanApi(
                { VasUser.getSession(activity) },
                "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
            ).ayanCall<ReportEndUserStatusOutput>(
                AyanCallStatus {
                    success {
                        if (it.response?.Parameters?.RegistrationStatus == "NotCompleted")
                            startSubscription(activity, applicationUniqueToken, callback)
                        else if (it.response?.Parameters?.RegistrationStatus == "Completed" && it.response?.Parameters?.Subscribed == false) {
                            logout(activity)
                            startSubscription(activity, applicationUniqueToken, callback)
                        } else if (it.response?.Parameters?.Subscribed == true)
                            callback(SubscriptionResult.OK)
                    }
                },
                EndPoint.ReportEndUserStatus,
                ReportEndUserStatusInput(
                    AuthenticationActivity.getApplicationVersion(activity), ReportNewDeviceInput(
                        AuthenticationActivity.getApplicationName(activity),
                        AuthenticationActivity.getApplicationType(activity),
                        VasUser.getApplicationUniqueId(activity),
                        applicationUniqueToken,
                        AuthenticationActivity.getApplicationVersion(activity),
                        ReportNewDeviceExtraInfo(
                            activity.packageName,
                            AuthenticationActivity.getInstalledApps(activity),
                            AuthenticationActivity.getApplicationVersion(activity)
                        ),
                        AuthenticationActivity.getOperatorName(activity)
                    ), AuthenticationActivity.getOperatorName(activity)
                ),
                commonCallStatus = AyanCommonCallStatus {
                    failure {
                        when {
                            it.failureType == FailureType.NO_INTERNET_CONNECTION -> callback(SubscriptionResult.NO_INTERNET_CONNECTION)
                            it.failureType == FailureType.TIMEOUT -> callback(SubscriptionResult.TIMEOUT)
                            else -> callback(SubscriptionResult.UNKNOWN)
                        }
                    }
                }
            )
        }
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
}