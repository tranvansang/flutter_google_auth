import 'package:flutter/services.dart';

class GoogleAuth {
  final methodChannel =
      const MethodChannel('me.transang.plugins.google_auth/channel');
  GoogleAuth._internal();
  static final _instance = GoogleAuth._internal();
  factory GoogleAuth() => _instance;

  Future<String> signIn(String clientId) async {
    return await methodChannel.invokeMethod('signIn', {
      'clientId': clientId,
    });
  }

  Future<void> signOut() async {
    await methodChannel.invokeMethod('signOut');
  }
}
