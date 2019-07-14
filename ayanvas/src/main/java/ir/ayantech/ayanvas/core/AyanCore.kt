package ir.ayantech.ayanvas.core

import android.app.Activity
import android.app.Application
import android.content.Context
import ir.ayantech.ayanvas.dialog.AyanCheckStatusDialog
import ir.ayantech.ayanvas.model.AppExtraInfo
import ir.ayantech.pushnotification.core.PushNotificationCore

class AyanCore {

    lateinit var applicationUniqueToken: String
    private lateinit var ayanCheckStatusDialog: AyanCheckStatusDialog

    companion object {

        var ayanCore: AyanCore? = null

        private fun getInstance(): AyanCore = ayanCore ?: AyanCore().also { ayanCore = it }

        fun initialize(application: Application, applicationUniqueToken: String) {
            getInstance().applicationUniqueToken = applicationUniqueToken
            BatchHandler.initialize(application)
            PushNotificationCore.start(application)
            PushNotificationCore.reportExtraInfo(application, AppExtraInfo(VasUser.getSession(application)))
        }

        fun startVasSubscription(
            activity: Activity,
            callback: (SubscriptionResult) -> Unit
        ) {
            getInstance().ayanCheckStatusDialog =
                AyanCheckStatusDialog(activity, getInstance().applicationUniqueToken, callback)
            getInstance().ayanCheckStatusDialog.show()
        }

        fun isUserSubscribed(activity: Activity, callback: (Boolean?) -> Unit) {
            getInstance().ayanCheckStatusDialog.isUserSubscribed(activity, callback)
        }

        fun logout(activity: Activity) {
            getInstance().ayanCheckStatusDialog.logout(activity)
        }

        fun getUserToken(context: Context): String = VasUser.getSession(context)

        fun shareApp(context: Context) {
            VersionControl.shareApp(context, getInstance().applicationUniqueToken)
        }

        fun getDownloadLink(context: Context, callback: (downloadLink: String) -> Unit) {
            VersionControl.getDownloadLink(context, getInstance().applicationUniqueToken, callback)
        }
    }
}