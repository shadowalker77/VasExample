package ir.ayantech.vasexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import ir.ayantech.ayanvas.core.AyanCore;
import ir.ayantech.ayanvas.core.SubscriptionResult;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AyanCore.Companion.startVasSubscription(this,
                new Function1<SubscriptionResult, Unit>() {
                    @Override
                    public Unit invoke(SubscriptionResult subscriptionResult) {
                        if (SubscriptionResult.OK == subscriptionResult) {
                            Log.d("Subscription", "OK");
                        } else if (SubscriptionResult.CANCELED == subscriptionResult) {
                            Log.d("Subscription", "CANCELED");
                        } else if (SubscriptionResult.NO_INTERNET_CONNECTION == subscriptionResult) {
                            Log.d("Subscription", "NO_INTERNET_CONNECTION");
                        } else if (SubscriptionResult.TIMEOUT == subscriptionResult) {
                            Log.d("Subscription", "TIMEOUT");
                        } else if (SubscriptionResult.UNKNOWN == subscriptionResult) {
                            Log.d("Subscription", "UNKNOWN");
                        }
                        return null;
                    }
                });
    }

    private void checkUserSubscription() {
        AyanCore.Companion.isUserSubscribed(this, new Function1<Boolean, Unit>() {
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
