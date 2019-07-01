package ir.ayantech.ayanvas.helper

import android.content.Context
import android.telephony.TelephonyManager
import ir.ayantech.ayanvas.R

internal class InformationHelper {
    companion object {
        fun getApplicationName(context: Context) = context.resources.getString(R.string.applicationName)

        fun getApplicationType(context: Context) = context.resources.getString(R.string.applicationType)

        fun getApplicationVersion(context: Context) =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName

        fun getOperatorName(context: Context) =
            (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName

//        fun getInstalledApps(context: Context): String {
//            val mainIntent = Intent(Intent.ACTION_MAIN, null)
//            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//            val pkgAppsList = context.packageManager.queryIntentActivities(mainIntent, 0)
//            return Gson().toJson(pkgAppsList.map { it.activityInfo.processName })
//        }

        fun getInstalledApps(context: Context): String = ""

        fun getApplicationCategory(applicationUniqueToken: String) = when (applicationUniqueToken.toLowerCase()) {
            "playstore" -> "playstore"
            "charkhoneh" -> "charkhoneh"
            "cafebazaar" -> "cafebazaar"
            "myket" -> "myket"
            else -> "socialmedia"
        }
    }
}