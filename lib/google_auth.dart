
import 'google_auth_platform_interface.dart';

class GoogleAuth {
  Future<String?> getPlatformVersion() {
    return GoogleAuthPlatform.instance.getPlatformVersion();
  }
}
