package ir.ayantech.ayanvas.core

import android.app.Activity
import ir.ayantech.ayannetworking.api.AyanApi
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.model.DoesEndUserSubscribedOutput
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.ui.AuthenticationActivity

class VasAuthentication(private val activity: Activity) {

    private val requestHandler: RequestHandler by lazy {
        RequestHandler()
    }

    fun startSubscription(
        applicationUniqueToken: String,
        callback: (SubscriptionResult) -> Unit
    ) {
        requestHandler.startForResult(
            activity,
            AuthenticationActivity.getProperIntent(
                activity,
                applicationUniqueToken
            ),
            callback
        )
    }

    fun isUserSubscribed(callback: (Boolean) -> Unit) {
        AyanApi(
            { VasUser.getSession(activity) },
            "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/"
        ).ayanCall<DoesEndUserSubscribedOutput>(
            AyanCallStatus {
                success {
                    callback(it.response?.Parameters?.Subscribed ?: false)
                }
            },
            EndPoint.DoesEndUserSubscribed
        )
    }

    fun logout() {
        VasUser.removeUserMobileNumber(activity)
        VasUser.removeSession(activity)
    }
}