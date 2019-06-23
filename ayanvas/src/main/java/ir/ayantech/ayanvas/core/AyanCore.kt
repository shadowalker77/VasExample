package ir.ayantech.ayanvas.core

import android.app.Activity
import android.content.Context
import ir.ayantech.pushnotification.core.PushNotificationCore

class AyanCore {

    lateinit var applicationUniqueToken: String
    private lateinit var vasAuthentication: VasAuthentication

    companion object {

        var ayanCore: AyanCore? = null

        private fun getInstance(): AyanCore = ayanCore ?: AyanCore().also { ayanCore = it }

        fun initialize(context: Context, applicationUniqueToken: String) {
            getInstance().applicationUniqueToken = applicationUniqueToken
            getInstance().vasAuthentication = VasAuthentication()
            PushNotificationCore.start(context)
        }

        fun startVasSubscription(
            activity: Activity,
            callback: (SubscriptionResult) -> Unit
        ) {
            getInstance().vasAuthentication.start(activity, getInstance().applicationUniqueToken, callback)
        }

        fun isUserSubscribed(activity: Activity, callback: (Boolean?) -> Unit) {
            getInstance().vasAuthentication.isUserSubscribed(activity, callback)
        }

        fun logout(activity: Activity) {
            getInstance().vasAuthentication.logout(activity)
        }
    }
}