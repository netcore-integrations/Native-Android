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

# Smartech Base SDK
-dontwarn com.netcore.android.**
-keep class com.netcore.android.**{*;}
-keep class * implements com.netcore.android.**.* {*;}
-keep class * extends com.netcore.android.**.* {*;}


# Smartech Push SDK
-dontwarn com.netcore.android.smartechpush.**
-keep class com.netcore.android.smartechpush.**{*;}
-keep class * implements com.netcore.android.smartechpush.**.* {*;}
-keep class * extends com.netcore.android.smartechpush.**.* {*;}
# Smartech AppInbox SDK
-dontwarn com.netcore.android.smartechappinbox.**
-keep class com.netcore.android.smartechappinbox.**{*;}
-keep class * implements com.netcore.android.smartechappinbox.**.* {*;}
-keep class * extends com.netcore.android.smartechappinbox.**.* {*;}