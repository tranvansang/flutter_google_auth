package me.transang.plugins.google_auth

import androidx.annotation.NonNull
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class GoogleAuthMethodCallHandler(private val delegate: GoogleAuthDelegate) :
	MethodChannel.MethodCallHandler {
	override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
		when (call.method) {
			METHOD_SIGN_IN -> //				List<String> requestedScopes = call.argument("scopes");
//				String hostedDomain = call.argument("hostedDomain");
				try {
					val clientId: String? = call.argument ("clientId")
					if (clientId == null) result.error("Error initializing sign in", "clientId is required", null)
					else delegate.signIn(
						clientId,
						result
					)
				} catch (e: Exception) {
					result.error("Error when sign in", e.message, null)
				}
			METHOD_SIGN_OUT -> try {
				delegate.signOut()
				result.success(true)
			} catch (e: Exception) {
				result.error("Error when sign out", e.message, null)
			}
			else -> result.notImplemented()
		}
	}

	companion object {
		private const val METHOD_SIGN_IN = "sign-in"
		private const val METHOD_SIGN_OUT = "sign-out"
	}
}