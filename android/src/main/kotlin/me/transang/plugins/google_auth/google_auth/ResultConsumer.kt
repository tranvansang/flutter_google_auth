package me.transang.plugins.google_auth

import android.content.IntentSender.SendIntentException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import io.flutter.plugin.common.MethodChannel

class ResultConsumer<T>(
	@NonNull result: MethodChannel.Result?,
	onEnd: Runnable
) {
	private var result: MethodChannel.Result?
	private val onEnd: Runnable

	init {
		this.result = result
		this.onEnd = onEnd
	}

	fun throwError(e: Exception?) {
		assert(result != null)
		if (e is SendIntentException) {
			result.error("FAIL_TO_SEND_INTENT", e.message, e)
		} else if (e is ApiException) {
			val statusCode: Int = (e as ApiException?).getStatusCode()
			when (statusCode) {
				CommonStatusCodes.CANCELED -> result.error("API_CANCELLED", e!!.message, e)
				CommonStatusCodes.NETWORK_ERROR -> result.error("API_NETWORK_ERROR", e!!.message, e)
				GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> result.error(
					"API_SIGN_IN_CANCELLED",
					e!!.message,
					e
				)
				CommonStatusCodes.SIGN_IN_REQUIRED -> result.error("API_SIGN_IN_REQUIRED", e!!.message, e)
				else -> result.error("API_OTHER", e!!.message, e)
			}
		} else if (e == null) result.error(
			"NO_DATA",
			"No data provided",
			null
		) else result.error("OTHER", e.message, e)
		end()
	}

	fun consume(value: T) {
		assert(result != null)
		result.success(value)
		end()
	}

	private fun end() {
		onEnd.run()
		result = null
	}
}