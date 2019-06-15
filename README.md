# AyanVas Authentication
This library aims to do all the logic of authentication in VAS related android apps belongs to AyanTech.

# How to add this to your project
This project has beed made by kotlin. So if you build your app completely in JAVA and you didn't config kotlin for your project, follow [this](https://developer.android.com/studio/projects/add-kotlin) link.
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
If your app needs to have Irancell authentication, you need to add inAppPurchase.aar and inAppSDK.aar files. In order to add them, from new menu, choose module and then import .JAR/.AAR package and add them to your project. Then add these lines to your gradle file:
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
implementation 'com.github.shadowalker77:vasexample:0.0.6'
```
After syncing gradle, create a values xml file in project values folder and config this strings properly with given values:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="applicationName">applicationName</string>
    <string name="applicationType">android</string>
</resources>
```
**Attention:** You don't need to set application type in most of projects. But configuring application name will be needed in all projects.
# Authentication
Depends on which language you are coding (JAVA or kotlin) use one of this approaches.
* JAVA:
```java
new VasAuthentication(context).startSubscription("APP_UNIQUE_TOKEN",
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
```
In the callback function, subscriptionResult variable determines the result of user subscription.
* kotlin:
```kotlin
VasAuthentication(context).startSubscription("APP_UNIQUE_TOKEN") {
    if (it == SubscriptionResult.OK)
        Log.d("Subscription", "OK")
    else
        Log.d("Subscription", "CANCEL")
}
```
**Attention:** context in constructor of VasAuthentications class should be activity context.
# Access user token
For accessing user token, you should call:
```java
VasUser.getSession(context)
```
# Logout user
For log user out, call:
* JAVA:
```java
new VasAuthentication(context).logout();
```
In the callback function, subscriptionResult variable determines the result of user subscription.
* kotlin:
```kotlin
VasAuthentication(context).logout()
```
# Check for subscription status of user
For checking user subscription status, use below method:
* JAVA:
```java
new VasAuthentication(this).isUserSubscribed(new Function1<Boolean, Unit>() {
    @Override
    public Unit invoke(Boolean aBoolean) {
        Log.d("SubscriptionStatus", aBoolean.toString())
        return null;
    }
});
```
**Attention:** aBoolean determines the status of user subscription.
**Important:** aBoolean may be null which means checking for user subscription has been failed due to some reason like lack of the internet.

* kotlin:
```kotlin
VasAuthentication(this).isUserSubscribed {
    Log.d("SubscriptionStatus", it.toString())
}
```
**Attention:** it variable is a Boolead which determines the status of user subscription.
**Important:** it variable may be null which means checking for user subscription has been failed due to some reason like lack of the internet.
# Progurad
If progurad is enabled for your project, you need to add Retrofit, Gson and OkHttp proguard rules depending of which version you are using. Also, you need to add this line to your proguard file:
```java
-keep public class ir.ayantech.ayannetworking.** { *; }
-keep public class ir.ayantech.ayanvas.** { *; }
```
Also if your app doesn't supports Irancell authentication, you need to add following lines too:
```java
-dontwarn com.android.billingclient.**
-dontwarn net.jhoobin.jhub.**
```