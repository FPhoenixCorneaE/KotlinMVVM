# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

#忽略警告
-ignorewarnings

# 代码混淆压缩比，在0~7之间，默认为5,一般不需要修改
-optimizationpasses 5

# 不启用shrink。shrink操作默认启用，主要的作用是将一些无效代码给移除，即没有被显示调用的代码
-dontshrink

# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下需要加入此配置(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不进行预校验，preverify是proguard的四个步骤之一
# 预校验是作用在Java平台上的，Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
# 不进行优化，不优化输入的类文件
-dontoptimize

# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

# 混淆时是否记录日志
# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose

#################### 记录生成的日志数据，gradle build时 在本项目根目录输出-start ####################
# apk 包内所有 class 的内部结构
#-dump proguard/class_files.txt
# 未混淆的类和成员
-printseeds proguard/seeds.txt
# 列出从 apk 中删除的代码
-printusage proguard/unused.txt
# 混淆前后的映射
-printmapping proguard/mapping.txt
#################### 记录生成的日志数据，gradle build时 在本项目根目录输出-end ####################

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 保留注解参数
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 保留内部类
-keepattributes InnerClasses

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持自定义控件类不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    *** set*(...);
    *** get*();
}

-keepclassmembers class * {
    void *(**On*Event);
}

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类有可能被外部调用
# 比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
    *** set*(...);
    *** get*();
}

# 保留Serializable序列化的类不被混淆
-keep class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
   *** set*(...);
   *** get*();
}

# 保留enum类不被混淆
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 继承JavascriptInterface的类
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 对R文件不混淆
-keep class **.R$* {*;}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {*;}

# BuildConfig文件不混淆
-keep class **.BuildConfig {*;}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
# 如果引用了v4或者v7包
-keep public class * extends android.support.** { *; }
# 忽略support包因为版本兼容产生的警告
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

# @Retention注解的类不混淆
-keep @java.lang.annotation.Retention class * {*;}

# 本地代码通过反射调用其他的类，但是经过了混淆之后，就会出现异常：ClassNotFoundException，NoSuchMethodError
# 调用了JNI之后，C或者C++和java代码进行交互的时候找不到java的类或者方法，导致发生了异常......等等，还有好多
# 只需要将被调用的java类标注为不混淆即可
#-keep class package.classname{*;}

# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，
# 这里可以作为禁止log打印的功能使用，另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int e(...);
    public static int i(...);
    public static int v(...);
    public static int w(...);
}

# 当工程从support库迁移至androidx后，若使用了混淆，则必须在混淆文件中添加以下配置，否则使用了androidx的地方都将有可能出问题
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

#---------------------------------------------------------------------------
# WebView
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keepclassmembers class fqcn.of.javascript.interface.for.Webview { public *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
#----------------------------------------------------------------------------









