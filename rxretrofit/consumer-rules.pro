#----------------------------------------------------------------------------
# okhttp3
-keep class okhttp3.internal.**{*;}
-dontwarn okhttp3.**
-dontwarn okio.**
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# Retrofit2
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# rxjava2
-dontwarn rx.*
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# rxAndroid
-dontwarn sun.misc.**

# 解决在6.0系统出现java.lang.InternalError
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#----------------------------------------------------------------------------