import Flutter
import UIKit

public class GoogleAuthPlugin: NSObject, FlutterPlugin {
	let delegate = GoogleAuthDelegate()
	
	public static func register(with registrar: FlutterPluginRegistrar) {
		let channel = FlutterMethodChannel(name:"me.transang.plugins.google_auth/channel", binaryMessenger: registrar.messenger())
		let instance = GoogleAuthPlugin()
		registrar.addApplicationDelegate(instance)
		registrar.addMethodCallDelegate(instance, channel: channel)
	}
	
	public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
		switch call.method {
			
		case "signIn":
			delegate.signIn(result: result)
			break
		case "signOut":
			delegate.signOut(result: result)
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
