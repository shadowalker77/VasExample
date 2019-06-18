package ir.ayantech.vasexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ir.ayantech.ayanvas.core.SubscriptionResult
import ir.ayantech.ayanvas.core.VasAuthentication

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VasAuthentication(this).start(
            "charkhoneh"
        ) {
            when (it) {
                SubscriptionResult.OK ->  Log.d("Subscription", "OK")
                SubscriptionResult.CANCELED ->  Log.d("Subscription", "CANCELED")
                SubscriptionResult.NO_INTERNET_CONNECTION ->  Log.d("Subscription", "NO_INTERNET_CONNECTION")
                SubscriptionResult.TIMEOUT ->  Log.d("Subscription", "TIMEOUT")
                SubscriptionResult.UNKNOWN ->  Log.d("Subscription", "UNKNOWN")
            }
        }
    }

    fun checkUserSubscription() {
        VasAuthentication(this).isUserSubscribed {
            when (it) {
                null -> Log.d("SubscriptionStatus", "checking failed for some reasons")
                true -> Log.d("SubscriptionStatus", "user is subscribed")
                false -> Log.d("SubscriptionStatus", "user is not subscribed")
            }
        }
    }
}
