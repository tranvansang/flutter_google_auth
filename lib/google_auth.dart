import 'package:flutter/services.dart';

class GoogleAuth {
  final methodChannel =
      const MethodChannel('me.transang.plugins.google_auth/channel');
  GoogleAuth._internal();
  static final _instance = GoogleAuth._internal();
  factory GoogleAuth() => _instance;

  /// @return
  /// iOS
  /// {
  /// "idToken"?: String
  /// "idTokenExpire"?: int
  /// "accessToken": String
  /// "accessTokenExpire"?: int
  /// "userID"?: String
  /// "refreshToken": String
  /// "refreshTokenExpire"?: int
  /// "email"?: String
  /// "name"?: String
  /// "givenName"?: String
  /// "familyName"?: String
  /// "image"?: String
  /// }
  /// Android
  /// {
  /// "idToken": String
  /// }
  Future<Map<String, dynamic>> signIn(String clientId) async {
    return Map<String, dynamic>.from(
        await methodChannel.invokeMethod('signIn', {'clientId': clientId}));
  }

  Future<void> signOut() async {
    await methodChannel.invokeMethod('signOut');
  }
}
