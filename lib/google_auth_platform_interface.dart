import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'google_auth_method_channel.dart';

abstract class GoogleAuthPlatform extends PlatformInterface {
  /// Constructs a GoogleAuthPlatform.
  GoogleAuthPlatform() : super(token: _token);

  static final Object _token = Object();

  static GoogleAuthPlatform _instance = MethodChannelGoogleAuth();

  /// The default instance of [GoogleAuthPlatform] to use.
  ///
  /// Defaults to [MethodChannelGoogleAuth].
  static GoogleAuthPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GoogleAuthPlatform] when
  /// they register themselves.
  static set instance(GoogleAuthPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
