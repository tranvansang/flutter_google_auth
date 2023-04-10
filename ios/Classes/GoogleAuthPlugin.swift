import Flutter
import UIKit

struct MethodName {
	static let METHOD_SIGN_IN = "signIn"
	static let METHOD_SIGN_OUT = "signOut"
}

public class GoogleAuthPluginSwift: NSObject, FlutterPlugin {
	static let CHANNEL_NAME = "me.transang.plugins.google_auth/channel"
	let delegate = GoogleAuthDelegate()

	public static func register(with registrar: FlutterPluginRegistrar) {
		let channel = FlutterMethodChannel(name: CHANNEL_NAME, binaryMessenger: registrar.messenger())
		let instance = GoogleAuthPluginSwift()
        registrar.addApplicationDelegate(instance)
		registrar.addMethodCallDelegate(instance, channel: channel)
	}

	public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
		let args = call.arguments as? [String: Any]

		switch call.method {

		case MethodName.METHOD_SIGN_IN:
            delegate.signIn(with: args?["clientId"] as! String, result: result)
			break
		case MethodName.METHOD_SIGN_OUT:
			delegate.signOut()
            result(nil)
			break
		default:
			result(FlutterMethodNotImplemented)
			break
		}
	}

    public func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        return delegate.handleUrl(open: url, options: options)
    }
}
