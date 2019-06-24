package ir.ayantech.ayanvas.core

import android.app.Activity
import android.content.Context
import android.support.v4.app.ShareCompat
import android.widget.Toast
import ir.ayantech.ayannetworking.api.AyanApi
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayannetworking.api.AyanCommonCallStatus
import ir.ayantech.ayannetworking.api.WrappedPackage
import ir.ayantech.ayanvas.dialog.AyanVersionControlDialog
import ir.ayantech.ayanvas.helper.InformationHelper
import ir.ayantech.ayanvas.model.*

internal class VersionControl(val activity: Activity, ayanCommonCallStatus: AyanCommonCallStatus) {

    val ayanApi = AyanApi(
        defaultBaseUrl = "http://versioncontrol.infra.ayantech.ir/WebServices/App.svc/",
        commonCallStatus = ayanCommonCallStatus
    )

    var checkVersion: WrappedPackage<*, CheckVersionOutput>? = null
    var getLastVersion: WrappedPackage<*, GetLastVersionOutput>? = null

    fun checkForNewVersion(callback: (updateStatus: Boolean) -> Unit) {
        checkVersion = ayanApi.ayanCall(
            AyanCallStatus {
                success {
                    if (it.response?.Parameters?.UpdateStatus == VersionControlUpdateStatus.NOT_REQUIRED) {
                        callback(true)
                        return@success
                    }
                    getLastVersion(callback)
                }
            },
            "CheckVersion",
            CheckVersionInput(
                InformationHelper.getApplicationName(activity),
                InformationHelper.getApplicationType(activity),
                InformationHelper.getApplicationCategory(activity),
                InformationHelper.getApplicationVersion(activity),
                AppExtraInfo(VasUser.getSession(activity))
            )
        )
    }

    fun getLastVersion(callback: (updateStatus: Boolean) -> Unit) {
        getLastVersion = ayanApi.ayanCall(
            AyanCallStatus {
                success {
                    AyanVersionControlDialog(
                        activity,
                        checkVersion?.response?.Parameters!!,
                        getLastVersion?.response?.Parameters!!,
                        callback
                    ).show()
                }
            },
            "GetLastVersion",
            GetLastVersionInput(
                InformationHelper.getApplicationName(activity),
                InformationHelper.getApplicationType(activity),
                InformationHelper.getApplicationCategory(activity),
                InformationHelper.getApplicationVersion(activity),
                AppExtraInfo(VasUser.getSession(activity))
            )
        )
    }

    fun shareApp(context: Context) {
        if (getLastVersion != null) {
            try {
                share(context, getLastVersion?.response?.Parameters?.TextToShare!!)
            } catch (e: Exception) {
            }

        } else {
            getLastVersion = ayanApi.ayanCall(
                AyanCallStatus {
                    success {
                        share(context, it.response?.Parameters?.TextToShare!!)
                    }
                },
                "GetLastVersion",
                GetLastVersionInput(
                    InformationHelper.getApplicationName(context),
                    InformationHelper.getApplicationType(context),
                    InformationHelper.getApplicationCategory(context),
                    InformationHelper.getApplicationVersion(context),
                    AppExtraInfo(VasUser.getSession(context))
                )
                , commonCallStatus = AyanCommonCallStatus {
                    failure {
                        Toast.makeText(
                            context,
                            "لطفا اتصال اینترنت خود را بررسی کرده و دوباره تلاش نمایید.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun share(context: Context, shareBody: String) {
        ShareCompat.IntentBuilder.from(context as Activity)
            .setText(shareBody)
            .setType("text/plain")
            .setChooserTitle("به اشتراک گذاری از طریق:")
            .startChooser()
    }
}