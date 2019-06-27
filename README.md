# AyanCore
This library aims to do all the logic of authentication, push notification and version control in VAS related android apps belongs to AyanTech.

# How to add this to your project
This project is made by kotlin. So if you build your app completely in JAVA and you didn't config kotlin for your project, you may need to add kotlin to the project. Follow [this](https://developer.android.com/studio/projects/add-kotlin) link for instructions.

This library depends on AppCompat and CardView support libraries. So you need to add them if you don't already have them in your project:
```java
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:cardview-v7:28.0.0'
```
Also it depends on Retrofit with Gson converter. So add these lines if you don't have them:
```java
implementation 'com.google.code.gson:gson:2.8.2'
implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
implementation 'com.squareup.okhttp3:okhttp:3.12.0'
```
If your app needs to have Irancell authentication, you need to add inAppPurchase.aar and inAppSDK.aar files. You can get them from this repo. In order to add them, from new menu, choose module and then import .JAR/.AAR package and add them to your project. Then add these lines to your gradle file:
```java
implementation project(':inAppPurchase')
implementation project(':inAppSDK')
```
Finally add JitPack maven repository to your project level gradle file, so allProjects tag of it should be something like this:
```java
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
And then add this line to module level gradle file:
```java
implementation 'com.github.shadowalker77:vasexample:0.4.0'
```
After syncing gradle, create a values xml file in project values folder and config this strings properly with given values:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="applicationName">applicationName</string>
    <string name="applicationCategory">android</string>
    <string name="applicationType">android</string>
</resources>
```
**Attention:** You don't need to set application type in most of projects. But configuring application name and category will be needed in all projects.
# Usage
You should communicate with this SDK using AyanCore class. In java you should use AyanCore.Companion.* and in kotlin you should use AyanCore.*. Here is an example:
You should initialize the SDK in your application class. So, here is how you should do it:
* JAVA:
```java
AyanCore.Companion.initialize(this, "APP_UNIQUE_TOKEN")
```
In the callback function, subscriptionResult variable determines the result of user subscription.
* kotlin:
```kotlin
AyanCore.initialize(this, "APP_UNIQUE_TOKEN")
```
On each start of application, in your main activity, depends on which language you are coding (JAVA or kotlin) use one of this approaches. After get the result in the callback method, you should proceed with proper logic.
* JAVA:
```java
AyanCore.Companion.startVasSubscription(activity,
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
```
In the callback function, subscriptionResult variable determines the result of user subscription.
* kotlin:
```kotlin
AyanCore.startVasSubscription(activity) {
  when (it) {
        SubscriptionResult.OK -> Log.d("Subscription", "OK")
        SubscriptionResult.CANCELED -> Log.d("Subscription", "CANCELED")
        SubscriptionResult.NO_INTERNET_CONNECTION -> Log.d("Subscription", "NO_INTERNET_CONNECTION")
        SubscriptionResult.TIMEOUT -> Log.d("Subscription", "TIMEOUT")
        SubscriptionResult.UNKNOWN -> Log.d("Subscription", "UNKNOWN")
    }
}
```
**Attention:** In this method you need to pass reference to your activity.
# Access user token
For accessing user token, you should call:
* JAVA:
```java
AyanCore.Companion.getUserToken(context);
```
* kotlin:
```java
AyanCore.getUserToken(context)
```
# Logout user
For log user out, call:
* JAVA:
```java
AyanCore.Companion.logout(context);
```
In the callback function, subscriptionResult variable determines the result of user subscription.
* kotlin:
```kotlin
AyanCore.logout(context);
```
# Check for subscription status of user
For checking user subscription status, use below method:
* JAVA:
```java
AyanCore.Companion.isUserSubscribed(this, new Function1<Boolean, Unit>() {
    @Override
	public Unit invoke(Boolean aBoolean) {
        if (aBoolean == null) Log.d("SubscriptionStatus", "checking failed for some reasons");
		else if (aBoolean) Log.d("SubscriptionStatus", "user is subscribed");
		else Log.d("SubscriptionStatus", "user is not subscribed");
		return null;
	}
});
```
**Attention:** aBoolean determines the status of user subscription.
**Important:** aBoolean may be null which means checking for user subscription has been failed due to some reason like lack of the internet.

* kotlin:
```kotlin
AyanCore.isUserSubscribed(this) {
  when (it) {
        null -> Log.d("SubscriptionStatus", "checking failed for some reasons")
        true -> Log.d("SubscriptionStatus", "user is subscribed")
        false -> Log.d("SubscriptionStatus", "user is not subscribed")
    }
}
```
**Attention:** it variable is a Boolead which determines the status of user subscription.
**Important:** it variable may be null which means checking for user subscription has been failed due to some reason like lack of the internet.
# Share app
In order to properly share a valid link of app with proper descriptions, just call this method:
* JAVA:
```java
AyanCore.Companion.shareApp(context)
```
* kotlin:
```kotlin
AyanCore.shareApp(context)
```
# Progurad
If progurad is enabled for your project, you need to add Retrofit, Gson and OkHttp proguard rules depending of which version you are using. Also, you need to add this line to your proguard file:
```java
-keep public class ir.ayantech.ayannetworking.** { *; }
-keep public class ir.ayantech.ayanvas.** { *; }
-keep public class ir.ayantech.pushnotification.** { *; }
```
Also if your app **doesn't** supports Irancell authentication, you need to add following lines too:
```java
-dontwarn com.android.billingclient.**
-dontwarn net.jhoobin.jhub.**
```
