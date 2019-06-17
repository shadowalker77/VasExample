package ir.ayantech.vasexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import ir.ayantech.ayanvas.core.SubscriptionResult;
import ir.ayantech.ayanvas.core.VasAuthentication;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new VasAuthentication(this).start("social",
                new Function1<SubscriptionResult, Unit>() {
                    @Override
                    public Unit invoke(SubscriptionResult subscriptionResult) {
                        if (subscriptionResult == SubscriptionResult.OK)
                            Log.d("Subscription", "OK");
                        else
                            Log.d("Subscription", "CANCEL");
                        return null;
                    }
                });
    }

    private void checkUserSubscription() {
        new VasAuthentication(this).isUserSubscribed(new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                if (aBoolean == null) Log.d("SubscriptionStatus", "checking failed for some reasons");
                else if (aBoolean) Log.d("SubscriptionStatus", "user is subscribed");
                else Log.d("SubscriptionStatus", "user is not subscribed");
                return null;
            }
        });
    }
}
