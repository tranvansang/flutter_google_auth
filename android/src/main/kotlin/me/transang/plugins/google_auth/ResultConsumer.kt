package me.transang.plugins.google_auth

import android.content.IntentSender.SendIntentException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import io.flutter.plugin.common.MethodChannel

open class ResultConsumer {
	private var result: MethodChannel.Result? = null
	protected fun setup(newResult: MethodChannel.Result): Boolean {
		if (result != null) {
			newResult.error("ALREADY_IN_PROGRESS", "Operation in progress", null)
			return false
		}
		result = newResult
		return true
	}

	protected fun throwError(code: String, message: String?, details: Any?) {
		if (result == null) throw Exception("Operation is already done")
		result!!.error(code, message, details)
		result = null
	}

	protected fun returnResult(value: Any) {
		if (result == null) throw Exception("Operation is already done")
		result!!.success(value)
		result = null
	}
}