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

# Using Google's License Verification Library
-keep class com.android.vending.licensing.ILicensingService

# Specifies to write out some more information during processing.
# If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

# Annotations are represented by attributes that have no direct effect on the execution of the code.
-keepattributes *Annotation*

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}

# These options let obfuscated applications or libraries produce stack traces that can still be deciphered later on
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Enable proguard with Cordova
-keep class org.apache.cordova.** { *; }
-keep public class * extends org.apache.cordova.CordovaPlugin

-keep class com.google.android.gms.dynamite.DynamiteModule$DynamiteLoaderClassLoader { java.lang.ClassLoader sClassLoader; }
-keep class com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor { int MODULE_VERSION; }
-keep class com.google.android.gms.dynamite.descriptors.com.google.android.gms.flags.ModuleDescriptor { java.lang.String MODULE_ID; }

-keep class org.apache.cordova.CordovaBridge { org.apache.cordova.PluginManager pluginManager; }
-keep class org.apache.cordova.CordovaInterfaceImpl { org.apache.cordova.PluginManager pluginManager; }
-keep class org.apache.cordova.CordovaResourceApi { org.apache.cordova.PluginManager pluginManager; }
-keep class org.apache.cordova.CordovaWebViewImpl { org.apache.cordova.PluginManager pluginManager; }
-keep class org.apache.cordova.ResumeCallback { org.apache.cordova.PluginManager pluginManager; }
-keep class org.apache.cordova.engine.SystemWebViewEngine { org.apache.cordova.PluginManager pluginManager; }

-keep class com.google.gson.internal.UnsafeAllocator { ** theUnsafe; }
-keep class me.leolin.shortcutbadger.ShortcutBadger { ** extraNotification; }
-keep class me.leolin.shortcutbadger.impl.XiaomiHomeBadger { ** messageCount; }
-keep class me.leolin.shortcutbadger.impl.XiaomiHomeBadger { ** extraNotification; }

-dontnote org.apache.harmony.xnet.provider.jsse.NativeCrypto
-dontnote sun.misc.Unsafe

-keep class com.worklight.androidgap.push.** { *; }
-keep class com.worklight.wlclient.push.** { *; }

# Enable proguard with Google libs
-keep class com.google.** { *; }
-dontwarn com.google.common.**
-dontwarn com.google.ads.**

# apache.http
-optimizations !class/merging/vertical*,!class/merging/horizontal*,!code/simplification/arithmetic,!field/*,!code/allocation/variable

-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

-keep class org.codehaus.** { *; }
-keepattributes *Annotation*,EnclosingMethod

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove debug logs in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);
}

# These classes contain references to external jars which are not included in the default MobileFirst project.
-dontwarn com.worklight.common.internal.WLTrusteerInternal*
-dontwarn com.worklight.jsonstore.**
-dontwarn org.codehaus.jackson.map.ext.*
-dontwarn com.worklight.androidgap.push.GCMIntentService
-dontwarn com.worklight.androidgap.plugin.WLInitializationPlugin

-dontwarn android.support.v4.**
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.net.http.*

-keepnames class * implements java.io.Serializable
-keep public class * implements java.io.Serializable {
   public *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Annotation
-keep class android.support.annotation.** { *; }
-keep interface android.support.annotation.** { *; }

-dontwarn javax.annotation.**
-keep class javax.annotation.**{*;}

-dontwarn javax.lang.model.element.**
-keep class javax.lang.model.element.** { *;}

# ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# okhttp
-dontwarn okio.**

#enity
-keep class com.ut.database.entity.**{*;}
-keep class com.ut.base.model.**{*;}

# FastJson 混淆代码
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#glide如果你的API级别<=Android API 27 则需要添加
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# Rxjava and RxAndroid
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-keep class com.squareup.okhttp.** { *; }
-dontwarn okio.**
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn io.reactivex.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-dontwarn sun.misc.**
-keep class sun.misc.** { *; }

-dontwarn java.lang.invoke.*
-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}
-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class io.reactivex.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontwarn io.reactivex.internal.util.unsafe.**

-ignorewarnings


