# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/gg/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# -dontshrink 关闭压缩(默认开启)

# -dontoptimize  关闭优化(默认开启)
# -optimizationpasses n 表示proguard对代码进行迭代优化的次数，Android一般为5

# -dontobfuscate 关闭混淆(默认开启)

# -keep class cn.hadcn.test.** 表示只是保持该包下的类名，而子包下的类名还是会被混淆
# -keep class cn.hadcn.test.* 表示把本包和所含子包下的类名都保持