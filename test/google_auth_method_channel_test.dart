import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:google_auth/google_auth_method_channel.dart';

void main() {
  MethodChannelGoogleAuth platform = MethodChannelGoogleAuth();
  const MethodChannel channel = MethodChannel('google_auth');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
