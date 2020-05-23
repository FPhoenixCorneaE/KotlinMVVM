# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sunfusheng/Android/Studio/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#----------------------------------------------------------------------------
-ignorewarnings

-dontwarn com.wkz.shapeimageview.**
-keep class com.wkz.shapeimageview.progress.OnGlideImageViewListener{*;}
-keep class com.wkz.shapeimageview.progress.OnProgressListener{*;}
-keep class com.wkz.shapeimageview.ShapeImageView{
    public <fields>;
    public <methods>;
}

