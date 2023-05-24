import Flutter
import GoogleSignIn

let ERR_META_OPERATION_IN_PROGRESS = "META_OPERATION_IN_PROGRESS"
let ERR_OTHER = "OTHER"

class GoogleAuthDelegate: NSObject {
	let instance: GIDSignIn = GIDSignIn.sharedInstance

	public func handleUrl(open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
		return instance.handle(url)
	}

	public func signIn(result: @escaping FlutterResult) {
		if (!setup(result: result)) {
			return
		}
		instance.signIn(withPresenting: topViewController) { [self] result, error in
			guard let result = result else {
				return finish(code: ERR_OTHER, message: "Fail to call login on GIDSignIn instance", details: nil)
			}
			finish(value: getReturnData(user: result.user))
		}
	}

	public func signOut(result: @escaping FlutterResult) {
		if (!setup(result: result)) {
			return
		}
		instance.signOut()
		finish(value: true)
	}

	private func getReturnData(user: GIDGoogleUser) -> [String : Any?] {
		let profile = user.profile
		let data = [
			"idToken": user.idToken?.tokenString,
			"idTokenExpire": user.idToken?.expirationDate?.timeIntervalSince1970 == nil
				? nil
				: Int64(user.idToken!.expirationDate!.timeIntervalSince1970 * 1000),
			"accessToken": user.accessToken.tokenString,
			"accessTokenExpire": user.accessToken.expirationDate == nil
				? nil
				: Int64(user.accessToken.expirationDate!.timeIntervalSince1970 * 1000),
			"userID": user.userID,
			"refreshToken": user.refreshToken.tokenString,
			"refreshTokenExpire": user.refreshToken.expirationDate == nil
				? nil
				: Int64(user.refreshToken.expirationDate!.timeIntervalSince1970 * 1000),
			"email": profile?.email,
			"name": profile?.name,
			"givenName": profile?.givenName,
			"familyName": profile?.familyName,
			"image": profile?.imageURL(withDimension: 1024)?.absoluteString,
		] as [String : Any?]
		print("debug: \(data)")
		return data
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
	var result: FlutterResult? = nil
	private func setup(result: @escaping FlutterResult) -> Bool {
		guard self.result == nil else {
			result(FlutterError(code: ERR_META_OPERATION_IN_PROGRESS, message: "Operation in progress", details: nil))
			return false
		}
		self.result = result
		return true
	}
	private func finish(value: Any) {
		guard result != nil else {
			return NSLog("Operation is already done")
		}
		result!(value)
		result = nil
	}
	private func finish(code: String, message: String, details: Any? = nil) {
		guard result != nil else {
			return NSLog("Operation is already done")
		}
		result!(FlutterError(code: code, message: message, details: details))
		result = nil
	}
	// result consumer END
}
