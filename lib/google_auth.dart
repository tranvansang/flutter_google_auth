import 'package:flutter/services.dart';

class GoogleAuth {
  final methodChannel =
      const MethodChannel('me.transang.plugins.google_auth/channel');
  GoogleAuth._internal();
  static final _instance = GoogleAuth._internal();
  factory GoogleAuth() => _instance;

  /// @return {
  /// "idToken": String
  /// "idTokenExpire": int // 0 if no idToken, otherwise timeIntervalSince1970 * 1000 (in milisec)
  /// "accessToken": String
  /// "accessTokenExpire": int // otherwise timeIntervalSince1970 * 1000 (in milisec)
  /// "userID"?: String
  /// "refreshToken": String
  /// "refreshTokenExpire": int // otherwise timeIntervalSince1970 * 1000 (in milisec)
  /// "email"?: String
  /// "name"?: String
  /// "givenName"?: String
  /// "familyName"?: String
  /// "image"?: String
  /// }
  Future<Map<String, dynamic>> login(List<String> permissions) async {
    return Map<String, dynamic>.from(await methodChannel.invokeMethod('login', {
      'permissions': permissions,
    }));
  }

  Future<Map<String, dynamic>> signIn() async {
    return Map<String, dynamic>.from(
        await methodChannel.invokeMethod('signIn'));
  }

  Future<void> signOut() async {
    await methodChannel.invokeMethod('signOut');
  }
}
