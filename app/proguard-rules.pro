# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\stpl\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn java.lang.invoke**
-dontwarn javax.annotation.Nullable
-dontwarn com.google.common.**
-dontwarn com.google.api.**
-dontwarn com.twitter.**

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-dontwarn rx.internal.util.unsafe.**
-keep class com.google.gson** { *; }
-keepclassmembers class com.google.gson** {*;}
-keep public class com.google.common.**


