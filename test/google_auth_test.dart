import 'package:flutter_test/flutter_test.dart';
import 'package:google_auth/google_auth.dart';
import 'package:google_auth/google_auth_platform_interface.dart';
import 'package:google_auth/google_auth_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGoogleAuthPlatform
    with MockPlatformInterfaceMixin
    implements GoogleAuthPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final GoogleAuthPlatform initialPlatform = GoogleAuthPlatform.instance;

  test('$MethodChannelGoogleAuth is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGoogleAuth>());
  });

  test('getPlatformVersion', () async {
    GoogleAuth googleAuthPlugin = GoogleAuth();
    MockGoogleAuthPlatform fakePlatform = MockGoogleAuthPlatform();
    GoogleAuthPlatform.instance = fakePlatform;

    expect(await googleAuthPlugin.getPlatformVersion(), '42');
  });
}
