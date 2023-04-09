import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'google_auth_platform_interface.dart';

/// An implementation of [GoogleAuthPlatform] that uses method channels.
class MethodChannelGoogleAuth extends GoogleAuthPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('google_auth');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
