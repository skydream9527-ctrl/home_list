# Coil
-keepnames class coil.ImageLoader
-keep class coil.key.Keyer { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler

# Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
