# LibGDX specific rules
-keep class com.badlogic.** { *; }
-keepclassmembers class com.badlogic.** { *; }

# Keep all kotlin stuff
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }
-dontwarn kotlin.**
-dontwarn kotlin.reflect.**

# Keep game classes
-keep class com.arrows.game.** { *; }
-keepclassmembers class com.arrows.game.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep view constructors for inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
