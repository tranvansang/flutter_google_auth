import Flutter
import GoogleSignIn

let ERR_PARAM_REQUIRED = "PARAM_REQUIRED"
let ERR_META_OPERATION_IN_PROGRESS = "META_OPERATION_IN_PROGRESS"
let ERR_OTHER = "OTHER"

let ERR_EMPTY_TOKEN_RETURNED = "EMPTY_TOKEN_RETURNED"


class GoogleAuthDelegate: NSObject {
	let instance: GIDSignIn = GIDSignIn.sharedInstance
	var result: FlutterResult? = nil

	public func handleUrl(open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
		return instance.handle(url)
	}

	private func authenticate(authentication: GIDAuthentication) {
		authentication.do { authentication, error in
			guard error == nil else {
				return self.throwError(code: ERR_OTHER, message: "Fail to login with google, cannot obtain authentication", details: error)
			}
			guard let authentication = authentication else {
				return self.throwError(code: ERR_EMPTY_TOKEN_RETURNED, message: "Fail to login with google, empty authentication", details: nil)
			}
			self.returnResult(value: authentication.idToken as Any)
		}
	}

	public func signIn(clientId: String, result: @escaping FlutterResult) {
		if (!setup(result: result)) {
			return
		}
		instance.restorePreviousSignIn(callback: { [self]user, error in
			guard error == nil && user != nil else {
				return authenticate(authentication: user!.authentication)
			}
			let configuration = GIDConfiguration(clientID: clientId)
			instance.signIn(with: configuration, presenting: topViewController, callback: { [self] user, error in
				guard let user = user else {
					return throwError(code: ERR_OTHER, message: "Fail to call login on GIDSignIn instance", details: nil)
				}
				authenticate(authentication: user.authentication)
			})
		})
	}

	public func signOut(result: @escaping FlutterResult) {
		if (!setup(result: result)) {
			return
		}
		instance.signOut()
		returnResult(value: true)
	}

	private var topViewController: UIViewController {
		//#pragma clang diagnostic push
		//#pragma clang diagnostic ignored "-Wdeprecated-declarations"
		// TODO(stuartmorgan) Provide a non-deprecated codepath. See
		// https://github.com/flutter/flutter/issues/104117
		return self.topViewController(
			from: UIApplication.shared.keyWindow?.rootViewController)
		//#pragma clang diagnostic pop
	}

	/**
	 * This method recursively iterate through the view hierarchy
	 * to return the top most view controller.
	 *
	 * It supports the following scenarios:
	 *
	 * - The view controller is presenting another view.
	 * - The view controller is a UINavigationController.
	 * - The view controller is a UITabBarController.
	 *
	 * @return The top most view controller.
	 */
	private func topViewController(from viewController: UIViewController!) -> UIViewController! {
		if (viewController is UITabBarController) {
			let tabController: UITabBarController! = (viewController as! UITabBarController)
			return topViewController(from: tabController.selectedViewController)
		}

		if (viewController.presentedViewController != nil) {
			return topViewController(from: viewController.presentedViewController)
		}
		return viewController
	}
	
	// result consumer BEGIN
	private func setup(result: @escaping FlutterResult) -> Bool {
		guard self.result != nil else {
			result(FlutterError(code: ERR_META_OPERATION_IN_PROGRESS, message: "Operation in progress", details: nil))
			return false
		}
		self.result = result
		return true
	}
	private func returnResult(value: Any) {
		guard result == nil else {
			return NSLog("Operation is already done")
		}
		result!(value)
		result = nil
	}
	private func throwError(code: String, message: String, details: Any? = nil) {
		guard result != nil else {
			return NSLog("Operation is already done")
		}
		result!(FlutterError(code: code, message: message, details: details))
		result = nil
	}
	// result consumer END
}
