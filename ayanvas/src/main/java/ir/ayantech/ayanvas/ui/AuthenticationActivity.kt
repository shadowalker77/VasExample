package ir.ayantech.ayanvas.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import com.android.billingclient.util.IabHelper
import com.android.billingclient.util.MarketIntentFactorySDK
import com.batch.android.Batch
import com.irozon.sneaker.Sneaker
import ir.ayantech.ayannetworking.api.*
import ir.ayantech.ayannetworking.ayanModel.Failure
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasUser
import ir.ayantech.ayanvas.dialog.WaiterDialog
import ir.ayantech.ayanvas.helper.InformationHelper.Companion.getApplicationName
import ir.ayantech.ayanvas.helper.InformationHelper.Companion.getApplicationType
import ir.ayantech.ayanvas.helper.InformationHelper.Companion.getApplicationVersion
import ir.ayantech.ayanvas.helper.InformationHelper.Companion.getInstalledApps
import ir.ayantech.ayanvas.helper.InformationHelper.Companion.getOperatorName
import ir.ayantech.ayanvas.model.*
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationActivity
import ir.ayantech.pushnotification.core.PushNotificationCore
import kotlinx.android.synthetic.main.ayan_vas_activity_authentication.*
import kotlinx.android.synthetic.main.ayan_vas_activity_authentication.iconIv
import kotlinx.android.synthetic.main.ayan_vas_fragment_get_mobile.*
import net.jhoobin.jhub.CharkhoneSdkApp
import net.jhoobin.jhub.util.AccountUtil
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


internal class AuthenticationActivity : FragmentationActivity() {

    private lateinit var iabHelper: IabHelper
    private lateinit var waiterDialog: WaiterDialog

    companion object {
        private const val VAS_APPLICATION_UNIQUE_TOKEN = "vasApplicationUniqueTokenTag"
        private const val VAS_SDK_IS_PRODUCTION = "vasApplicationIsProduction"
        const val END_USER_STATUS = "vasEndUserStatusTag"

        fun getProperIntent(
            context: Context,
            vasApplicationUniqueToken: String,
            endUserStatus: String,
            isProduction: Boolean
        ): Intent {
            val intent = Intent(context, AuthenticationActivity::class.java)
            intent.putExtra(VAS_APPLICATION_UNIQUE_TOKEN, vasApplicationUniqueToken)
            intent.putExtra(END_USER_STATUS, endUserStatus)
            intent.putExtra(VAS_SDK_IS_PRODUCTION, isProduction)
            return intent
        }
    }

    var getServiceInfo: WrappedPackage<*, GetServiceInfoOutput>? = null

    private val ayanCommonCallingStatus = AyanCommonCallStatus {
        failure {
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

    private fun initializeBatch() {
        Batch.onStart(this)
        Batch.User.editor()
            .setAttribute("INSTALL", getApplicationUniqueToken())
            .save()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ayan_vas_activity_authentication)
        initializeBatch()
        waiterDialog = WaiterDialog(this)
        iconIv.setImageDrawable(getApplicationIcon())
        wp10pbar.showProgressBar()
        KeyboardVisibilityEvent.setEventListener(this) {
            if (topFragment is EnterActivationFragment)
                (topFragment as EnterActivationFragment).keyboardStatusHandler(it)
            else if (topFragment is GetMobileFragment)
                (topFragment as GetMobileFragment).keyboardStatusHandler(it)
        }
        if (VasUser.getSession(this).isEmpty())
            ayanApi.ayanCall<ReportNewDeviceOutput>(
                AyanCallStatus {
                    success {
                        VasUser.saveSession(this@AuthenticationActivity, it.response?.Parameters?.Token!!)
                        PushNotificationCore.reportExtraInfo(
                            this@AuthenticationActivity,
                            AppExtraInfo(it.response?.Parameters?.Token!!)
                        )
                        callGetServiceInfo()
                    }
                },
                EndPoint.ReportNewDevice,
                ReportNewDeviceInput(
                    getApplicationName(this),
                    getApplicationType(this),
                    VasUser.getApplicationUniqueId(this@AuthenticationActivity),
                    getApplicationUniqueToken(),
                    getApplicationVersion(this),
                    ReportNewDeviceExtraInfo(packageName, getInstalledApps(this), getApplicationVersion(this)),
                    getOperatorName(this)
                )
                , hasIdentity = false
            )
        else if (getEndUserStatus() == EndUserStatus.SecondPage)
            loadRootFragment(
                R.id.fragmentContainerFl,
                GetMobileFragment().also { it.endUserStatus = getEndUserStatus() })
        else
            callGetServiceInfo()
    }

    private fun callGetServiceInfo() {
        getServiceInfo = ayanApi.ayanCall(
            AyanCallStatus {
                success {
                    VasUser.saveGetServiceInfo(this@AuthenticationActivity, it.response?.Parameters!!)
                    when {
                        it.response?.Parameters?.Action == GetServiceInfoAction.NOTHING -> {
                            if (isProduction())
                                doCallBack(SubscriptionResult.OK)
                            else
                                loadRootFragment(
                                    R.id.fragmentContainerFl,
                                    GetMobileFragment().also { it.endUserStatus = getEndUserStatus() })
                        }
                        it.response?.Parameters?.Action == GetServiceInfoAction.CHOOSE_OPERATOR -> loadRootFragment(
                            R.id.fragmentContainerFl,
                            ChooseOperatorFragment()
                        )
                        it.response?.Parameters?.ShowSliders == true -> loadRootFragment(
                            R.id.fragmentContainerFl,
                            IntroductionFragment().also { frag -> frag.sliders = it.response?.Parameters?.Sliders!! }
                        )
                        else -> loadRootFragment(
                            R.id.fragmentContainerFl,
                            GetMobileFragment().also { it.endUserStatus = getEndUserStatus() })
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

    fun isProduction() = intent.getBooleanExtra(VAS_SDK_IS_PRODUCTION, true)

    private fun getApplicationUniqueToken() = intent.getStringExtra(VAS_APPLICATION_UNIQUE_TOKEN)

    private fun getEndUserStatus() = intent.getStringExtra(END_USER_STATUS)

    fun irancellSubscription() {
        CharkhoneSdkApp.initSdk(
            applicationContext,
            getServiceInfo?.response?.Parameters?.Secrets?.toTypedArray()
        )
        AccountUtil.removeAccount()
        val fillInIntent = Intent()
        fillInIntent.putExtra("msisdn", mobileNumberEt.text.toString())
        fillInIntent.putExtra("editAble", false)
        iabHelper = IabHelper(
            this,
            getServiceInfo?.response?.Parameters?.PublicKeyBase64,
            MarketIntentFactorySDK(true)
        )
        iabHelper.setFillInIntent(fillInIntent)
        iabHelper.startSetup {
            if (!it.isSuccess) return@startSetup
            iabHelper.launchSubscriptionPurchaseFlow(
                this,
                getServiceInfo?.response?.Parameters?.Sku,
                400
            ) { iabResult, purchase ->
                if (iabResult.isFailure) {
                    doCallBack(SubscriptionResult.CANCELED)
                    return@launchSubscriptionPurchaseFlow
                }
                ayanApi.ayanCall<Void>(
                    AyanCallStatus {
                        success {
                            VasUser.saveMobile(this@AuthenticationActivity, mobileNumberEt.text.toString())
                            doCallBack(SubscriptionResult.OK)
                        }
                    },
                    EndPoint.ReportMtnSubscription,
                    ReportMtnSubscriptionInput(
                        purchase.developerPayload,
                        purchase.isAutoRenewing,
                        purchase.itemType,
                        purchase.msisdn,
                        purchase.orderId,
                        purchase.originalJson,
                        purchase.packageName,
                        purchase.purchaseTime,
                        purchase.purchaseState,
                        purchase.signature,
                        purchase.sku,
                        purchase.token
                    )
                )
            }
        }
    }

    fun restart() {
        setResult(88858)
        this.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }
}

