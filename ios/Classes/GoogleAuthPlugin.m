#import "GoogleAuthPlugin.h"
#if __has_include(<google_auth/google_auth-Swift.h>)
#import <google_auth/google_auth-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "google_auth-Swift.h"
#endif

@implementation GoogleAuthPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [GoogleAuthPluginSwift registerWithRegistrar:registrar];
}
@end
