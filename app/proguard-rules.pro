# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn java.lang.invoke**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep public class ir.ayantech.ayannetworking.** { *; }
-keep public class ir.ayantech.ayanvas.** { *; }
-keep public class ir.ayantech.pushnotification.** { *; }
#-----------------------------------Batch-----------------------------------
-keep class com.batch.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class android.support.v7.app.** { *; }
-keep class android.support.v4.** { *; }
-dontwarn com.batch.android.mediation.**
-dontwarn com.batch.android.BatchPushService
#-----------------------------------------------------------------------------