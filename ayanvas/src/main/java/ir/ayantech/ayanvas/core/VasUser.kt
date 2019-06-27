package ir.ayantech.ayanvas.core

import android.content.Context
import android.provider.Settings
import com.google.gson.Gson
import ir.ayantech.ayanvas.model.GetServiceInfoOutput
import java.util.*

internal class VasUser {
    companion object {
        const val VAS_USER_SESSION = "vasUserSession"
        const val VAS_USER_MOBILE = "vasUserMobile"
        //        const val VAS_USER_SUBSCRIBED = "vasUserSubscribed"
        const val VAS_USER_APPLICATION_UNIQUE_ID = "vasUserApplicationUniqueId"
        const val VAS_GET_SERVICE_INFO = "vasGetServiceInfo"

        internal fun saveGetServiceInfo(context: Context, getServiceInfo: GetServiceInfoOutput) {
            PreferencesManager.getInstance(context)
                .saveToSharedPreferences(VAS_GET_SERVICE_INFO, Gson().toJson(getServiceInfo))
        }

        internal fun getGetServiceInfo(context: Context): GetServiceInfoOutput {
            return Gson().fromJson(PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_GET_SERVICE_INFO), GetServiceInfoOutput::class.java)
        }

        internal fun saveSession(context: Context, session: String) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_SESSION, session)
        }

        internal fun getSession(context: Context): String {
            return PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_SESSION)
        }

        private fun saveApplicationUniqueId(context: Context, applicationUniqueId: String) {
            PreferencesManager.getInstance(context)
                .saveToSharedPreferences(VAS_USER_APPLICATION_UNIQUE_ID, applicationUniqueId)
        }

        internal fun getApplicationUniqueId(context: Context): String {
            return if (PreferencesManager.getInstance(context).readStringFromSharedPreferences(
                    VAS_USER_APPLICATION_UNIQUE_ID
                ).isNotEmpty()
            )
                PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_APPLICATION_UNIQUE_ID)
            else
                createApplicationUniqueId(context).also { saveApplicationUniqueId(context, it) }
        }

        internal fun saveMobile(context: Context, mobile: String) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_MOBILE, mobile)
        }

        internal fun removeSession(context: Context) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_SESSION, "")
        }

        internal fun getMobile(context: Context): String {
            return PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_MOBILE)
        }

        internal fun removeUserMobileNumber(context: Context) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_MOBILE, "")
        }

//        fun isUserSubscribed(context: Context): Boolean {
//            return PreferencesManager.getInstance(context).readBooleanFromSharedPreferences(VAS_USER_SUBSCRIBED)
//        }
//
//        fun saveUserSubscribed(context: Context, b: Boolean) {
//            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_SUBSCRIBED, b)
//        }

        private fun createApplicationUniqueId(context: Context) =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ?: UUID.randomUUID().toString()
    }
}