package me.transang.plugins.google_auth

import android.content.IntentSender.SendIntentException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import io.flutter.plugin.common.MethodChannel

class ResultConsumer<T>(
	private val result: MethodChannel.Result,
	private val onDone: () -> Unit
) {
	private var isDone = false
	fun throwError(e: Exception?) {
		if (isDone) throw Exception("ResultConsumer is already done")
		when (e) {
			is SendIntentException -> {
				result.error("FAIL_TO_SEND_INTENT", e.message, e)
			}
			is ApiException -> {
				when ((e as ApiException?)?.statusCode) {
					CommonStatusCodes.CANCELED -> result.error("API_CANCELLED", e.message, e)
					CommonStatusCodes.NETWORK_ERROR -> result.error("API_NETWORK_ERROR", e.message, e)
					GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> result.error(
						"API_SIGN_IN_CANCELLED",
						e.message,
						e
					)
					CommonStatusCodes.SIGN_IN_REQUIRED -> result.error(
						"API_SIGN_IN_REQUIRED",
						e.message,
						e
					)
					else -> result.error("API_OTHER", e.message, e)
				}
			}
			null -> result.error(
				"NO_DATA",
				"No data provided",
				null
			)
			else -> result.error("OTHER", e.message, e)
		}
		end()
	}

	fun consume(value: T) {
		if (isDone) throw Exception("ResultConsumer is already done")
		result.success(value)
		end()
	}

	private fun end() {
		isDone = true
		onDone()
	}
}