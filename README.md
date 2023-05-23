# google_auth

Simple google authentication plugin for flutter.

# Configuration

## Android
No configuration required.

## iOS

In `info.plist` add the following:

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>$(DEFINE_GOOGLE_CLIENT_ID_IOS)</string>
        </array>
    </dict>
</array>
```

with `$(DEFINE_GOOGLE_CLIENT_ID_IOS)` being the client id of your app.

# Usage

- Add the plugin to `pubspec.yaml`: `flutter pub add google_auth`.
- Import the plugin in your dart code: `import 'package:google_auth/google_auth.dart';`.
- To authenticate user: `GoogleAuth().signIn(clientId)` with `clientId: string` being the client id.
This function returns a `Future` object resolving a string of the id token.
  - For iOS, use the client id setup for iOS app.
  - For Android, use the client id setup for **Web** app. Note: *if you use the client id setup for Android app, you will get an error saying that the developer console is not setup correctly*.
- To logout user: `GoogleAuth().signOut(): Future<void>`.
- Possible error code: check the error code with
```flutter
try {
  return {'idToken': await GoogleAuth().signIn(clientId)};
} on PlatformException catch (e) {
  if (e.code == 'API_CANCELED' || e.code == 'GSI_SIGN_IN_CANCELLED') {
    throw AppError('User has cancelled');
  }
  rethrow;
}
```
  - For iOS: see https://github.com/tranvansang/flutter_google_auth/tree/master/ios/Classes/GoogleAuthDelegate.swift#L4
  - For Android: see https://github.com/tranvansang/flutter_google_auth/tree/master/android/src/main/kotlin/me/transang/plugins/google_auth/GoogleAuthDelegate.kt#L20
