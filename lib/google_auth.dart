import 'package:flutter/services.dart';

class GoogleAuth {
  final methodChannel =
      const MethodChannel('me.transang.plugins.google_auth/channel');

  Future<String> signIn(String clientId) async {
    return await methodChannel.invokeMethod('sign-in', {
      'clientId': clientId,
    });
  }

  Future<void> signOut() async {
    await methodChannel.invokeMethod('sign-out');
  }
}
