package ir.ayantech.ayanvas.core

import android.app.Activity
import android.app.Application
import android.content.Context
import ir.ayantech.ayanvas.dialog.AyanCheckStatusDialog
import ir.ayantech.ayanvas.model.AppExtraInfo
import ir.ayantech.ayanvas.model.TokenInfo
import ir.ayantech.pushsdk.core.AyanNotification

class AyanCore {

    lateinit var applicationUniqueToken: String
    lateinit var ayanCheckStatusDialog: AyanCheckStatusDialog
    private var isProduction = true

    companion object {

        var ayanCore: AyanCore? = null

        fun getInstance(): AyanCore = ayanCore ?: AyanCore().also { ayanCore = it }

        fun initialize(application: Application, applicationUniqueToken: String, isProduction: Boolean = true) {
            getInstance().applicationUniqueToken = applicationUniqueToken
            getInstance().isProduction = isProduction
            BatchHandler.initialize(application)
            AyanNotification.initialize(application)
            AyanNotification.reportExtraInfo(AppExtraInfo(VasUser.getSession(application)))
        }

        fun startVasSubscription(
            activity: Activity,
            callback: (SubscriptionResult) -> Unit
        ) {
            getInstance().ayanCheckStatusDialog =
                AyanCheckStatusDialog(
                    activity,
                    getInstance().applicationUniqueToken,
                    getInstance().isProduction,
                    callback
                )
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

        fun getUserInfo(context: Context, callback: (TokenInfo) -> Unit) {
            getInstance().ayanCheckStatusDialog.getTokenInfo(context, callback)
        }

        inline fun <reified T> getRemoteConfig(
            context: Context,
            fieldName: String,
            defaultValue: T,
            crossinline callback: (T) -> Unit
        ) {
            getInstance().ayanCheckStatusDialog.getRemoteConfig(context, fieldName, defaultValue, callback)
        }
    }
}