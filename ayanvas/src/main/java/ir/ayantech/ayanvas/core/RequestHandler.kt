package ir.ayantech.ayanvas.core

import android.app.Activity
import android.app.Fragment
import android.content.Intent

internal class RequestHandler(
    val activity: Activity,
    val intent: Intent,
    val callback: (SubscriptionResult) -> Unit
) {

    fun startForResult(
    ) {
        getRequestFragment(activity)?.startForResult(intent, callback)
            ?: callback(SubscriptionResult.CANCELED)
    }

    private fun getRequestFragment(activity: Activity): RequestFragment? {
        var requestFragment = activity
            .fragmentManager
            .findFragmentByTag(REQUEST_FRAGMENT_TAG) as? RequestFragment?
        return if (requestFragment == null) {
            requestFragment = RequestFragment()
            activity.fragmentManager
                .beginTransaction()
                .add(requestFragment, REQUEST_FRAGMENT_TAG)
                .commitAllowingStateLoss()
            activity.fragmentManager.executePendingTransactions()
            requestFragment.requestHandler = this
            requestFragment
        } else {
            requestFragment
        }
    }

    companion object {
        private const val REQUEST_FRAGMENT_TAG = "request_fragment"
    }

    class RequestFragment : Fragment() {

        lateinit var requestHandler: RequestHandler

        private var callback: ((SubscriptionResult) -> Unit)? = null

        fun startForResult(intent: Intent, callback: (SubscriptionResult) -> Unit) {
            this.callback = callback
            startActivityForResult(intent, RC)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == 88858) {
                requestHandler.startForResult()
                return
            }
            callback?.invoke(SubscriptionResult.from(resultCode))
            callback = null
        }

        companion object {
            private const val RC = 45909
        }
    }
}