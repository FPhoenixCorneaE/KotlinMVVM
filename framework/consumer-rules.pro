#----------------------------------------------------------------------------
# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
#----------------------------------------------------------------------------

# 本地代码通过反射调用其他的类，但是经过了混淆之后，就会出现异常：ClassNotFoundException,NoSuchMethodError,InvocationTargetException...
# 调用了JNI之后，C或者C++和java代码进行交互的时候找不到java的类或者方法，导致发生了异常......等等，还有好多
# 只需要将被调用的java类标注为不混淆即可
-keep class tv.danmaku.ijk.media.** { *; }