package ir.ayantech.ayanvas.core

import android.content.Context
import android.provider.Settings
import java.util.*

class VasUser {
    companion object {
        const val VAS_USER_SESSION = "vasUserSession"
        const val VAS_USER_MOBILE = "vasUserMobile"
//        const val VAS_USER_SUBSCRIBED = "vasUserSubscribed"
        const val VAS_USER_APPLICATION_UNIQUE_ID = "vasUserApplicationUniqueId"

        fun saveSession(context: Context, session: String) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_SESSION, session)
        }

        fun getSession(context: Context): String {
            return PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_SESSION)
        }

        private fun saveApplicationUniqueId(context: Context, applicationUniqueId: String) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_APPLICATION_UNIQUE_ID, applicationUniqueId)
        }

        fun getApplicationUniqueId(context: Context): String {
            return if (PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_APPLICATION_UNIQUE_ID).isNotEmpty())
                PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_APPLICATION_UNIQUE_ID)
            else
                createApplicationUniqueId(context).also { saveApplicationUniqueId(context, it) }
        }

        fun saveMobile(context: Context, mobile: String) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_MOBILE, mobile)
        }

        fun removeSession(context: Context) {
            PreferencesManager.getInstance(context).saveToSharedPreferences(VAS_USER_SESSION, "")
        }

        fun getMobile(context: Context): String {
            return PreferencesManager.getInstance(context).readStringFromSharedPreferences(VAS_USER_MOBILE)
        }

        fun removeUserMobileNumber(context: Context) {
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