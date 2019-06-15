package ir.ayantech.ayanvas.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.ViewGroup
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import ir.ayantech.ayannetworking.api.*
import ir.ayantech.ayannetworking.ayanModel.Failure
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.dialog.WaiterDialog
import ir.ayantech.ayanvas.model.*
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationActivity
import kotlinx.android.synthetic.main.ayan_vas_activity_authentication.*


class AuthenticationActivity : FragmentationActivity() {

    companion object {
        private const val VAS_APPLICATION_UNIQUE_TOKEN = "vasApplicationUniqueTokenTag"

        fun getProperIntent(
            context: Context,
            vasApplicationUniqueToken: String
        ): Intent {
            val intent = Intent(context, AuthenticationActivity::class.java)
            intent.putExtra(VAS_APPLICATION_UNIQUE_TOKEN, vasApplicationUniqueToken)
            return intent
        }
    }

    lateinit var waiterDialog: WaiterDialog

    var getServiceInfo: WrappedPackage<*, GetServiceInfoOutput>? = null

    private val ayanCommonCallingStatus = AyanCommonCallStatus {
        failure {
            if (it.failureCode == "G00002") {
                VasUser.removeSession(this@AuthenticationActivity)
                VasUser.removeUserMobileNumber(this@AuthenticationActivity)
                finish()
                startActivity(Intent(this@AuthenticationActivity, AuthenticationActivity::class.java))
            }
            showNoInternetLayout(it)
        }
        changeStatus {
            when (it) {
                CallingState.NOT_USED -> hideProgressDialog()
                CallingState.LOADING -> {
                    showProgressDialog()
                    try {
                        Sneaker.hide()
                    } catch (e: Exception) {
                    }
                }
                CallingState.FAILED -> hideProgressDialog()
                CallingState.SUCCESSFUL -> hideProgressDialog()
            }
        }
    }

    val ayanApi = AyanApi(
        { VasUser.getSession(this) },
        "https://subscriptionmanager.vas.ayantech.ir/WebServices/App.svc/",
        ayanCommonCallingStatus
    )

    private fun getApplicationIcon(): Drawable = packageManager.getApplicationIcon(packageName)

    private fun showNoInternetLayout(failure: Failure) {
        Sneaker.with(this)
            .setTitle("خطا")
            .autoHide(false)
            .setMessage(failure.failureMessage + " تلاش مجدد؟")
            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            .setOnSneakerClickListener { failure.reCallApi() }
            .sneakError()
    }

    private fun showProgressDialog() {
        waiterDialog.showDialog()
    }

    private fun hideProgressDialog() {
        waiterDialog.hideDialog()
    }

    fun showError(message: String) {
        Sneaker.with(this)
            .setTitle("خطا")
            .autoHide(false)
            .setMessage(message)
            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            .sneakError()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ayan_vas_activity_authentication)
        waiterDialog = WaiterDialog(this)
        iconIv.setImageDrawable(getApplicationIcon())
        wp10pbar.showProgressBar()
        if (VasUser.getSession(this).isEmpty())
            ayanApi.ayanCall<ReportNewDeviceOutput>(
                AyanCallStatus {
                    success {
                        VasUser.saveSession(this@AuthenticationActivity, it.response?.Parameters?.Token!!)
                        callGetServiceInfo()
                    }
                },
                EndPoint.ReportNewDevice,
                ReportNewDeviceInput(
                    getApplicationName(),
                    getApplicationType(),
                    VasUser.getApplicationUniqueId(this@AuthenticationActivity),
                    getApplicationUniqueToken(),
                    getApplicationVersion(),
                    ReportNewDeviceExtraInfo(packageName, getInstalledApps(), getApplicationVersion()),
                    getOperatorName()
                )
                , hasIdentity = false
            )
        else {
            ayanApi.ayanCall<DoesEndUserSubscribedOutput>(
                AyanCallStatus {
                    success {
                        if (it.response?.Parameters?.Subscribed == true) doCallBack(SubscriptionResult.OK) else callGetServiceInfo()
                    }
                },
                EndPoint.ReportEndUserStatus,
                ReportEndUserStatusInput(
                    getApplicationVersion(), ReportNewDeviceInput(
                        getApplicationName(),
                        getApplicationType(),
                        VasUser.getApplicationUniqueId(this@AuthenticationActivity),
                        getApplicationUniqueToken(),
                        getApplicationVersion(),
                        ReportNewDeviceExtraInfo(packageName, getInstalledApps(), getApplicationVersion()),
                        getOperatorName()
                    ), getOperatorName()
                )
            )
        }
    }

    fun getInstalledApps(): String {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0)
        return Gson().toJson(pkgAppsList.map { it.activityInfo.processName })
    }

    fun callGetServiceInfo() {
        getServiceInfo = ayanApi.ayanCall(
            AyanCallStatus {
                success {
                    when {
                        it.response?.Parameters?.Action == GetServiceInfoAction.NOTHING -> doCallBack(SubscriptionResult.OK)
                        it.response?.Parameters?.ShowSliders == true -> loadRootFragment(
                            R.id.fragmentContainerFl,
                            IntroductionFragment().also { frag -> frag.sliders = it.response?.Parameters?.Sliders!! }
                        )
                        else -> loadRootFragment(R.id.fragmentContainerFl, GetMobileFragment())
                    }
                }
            },
            EndPoint.GetServiceInfo
        )
    }

    fun doCallBack(subscriptionResult: SubscriptionResult) {
        setResult(subscriptionResult.value)
        finish()
    }

    private fun getApplicationName() = resources.getString(R.string.applicationName)

    private fun getApplicationType() = resources.getString(R.string.applicationType)

    private fun getApplicationUniqueToken() = intent.getStringExtra(VAS_APPLICATION_UNIQUE_TOKEN)

    private fun getApplicationVersion() = packageManager.getPackageInfo(packageName, 0).versionName

    private fun getOperatorName() =
        (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
}

