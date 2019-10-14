-keep class com.unitech.boardtonote.** {*;}

#uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Coroutine
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#Glide
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule