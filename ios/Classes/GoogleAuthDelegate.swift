import GoogleSignIn

class GoogleAuthDelegate: NSObject {
    let instance: GIDSignIn = GIDSignIn.sharedInstance

    public func handleUrl(open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        return instance.handle(url)
    }

    private func authenticate(authentication: GIDAuthentication, result: @escaping FlutterResult) {
        authentication.do { authentication, error in
            guard error == nil else {
                result(FlutterError(code: "FAIL_TO_GET_AUTHENTICATION", message: "Fail to login with google, cannot obtain authentication", details: nil))
                return
            }
            guard let authentication = authentication else {
                result(FlutterError(code: "AUTHENTICATION_EMPTY", message: "Fail to login with google, empty authentication", details: nil))
                return
            }
            result(authentication.idToken)
        }
    }

    public func signIn(with clientId: String, result: @escaping FlutterResult) {
        instance.restorePreviousSignIn(callback: { [self]user, error in
            if error != nil || user == nil {
                let configuration = GIDConfiguration(clientID: clientId)
                instance.signIn(with: configuration, presenting: topViewController, callback: { [self]
                    user,
                    error in
                    guard let user = user else {
                        result(FlutterError(code: "FAIL_TO_LOGIN", message: "Fail to login with google", details: nil))
                        return
                    }
                    authenticate(authentication: user.authentication, result: result)
                })
            } else {
                authenticate(authentication: user!.authentication, result: result)
            }
        })
    }

    public func signOut(with result: @escaping FlutterResult) {
        instance.signOut()
        result(nil)
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
}
