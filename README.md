# ReferPesa Offline Demo

Android Studio project. Open this folder in Android Studio, allow Gradle to sync, then choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**. APK output is under `app/build/outputs/apk/debug/app-debug.apk`.

All data is stored locally in Android WebView storage. The three initial campaigns are fictional samples, and the app does not make payments, register real users, or contact a server. WhatsApp sharing uses Android's native share sheet (WhatsApp must be installed to select it).

Requires Android Studio with Android SDK 35, JDK 17, and internet during initial Gradle dependency setup. After installation, the app works offline.
