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
        VasAuthentication(this).startSubscription(
            "charkhoneh"
        ) {
            if (it == SubscriptionResult.OK)
                Log.d("Subscription", "OK")
            else
                Log.d("Subscription", "CANCEL")
        }
    }
}
