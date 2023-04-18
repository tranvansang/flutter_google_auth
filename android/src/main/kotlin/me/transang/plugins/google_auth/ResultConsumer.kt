package me.transang.plugins.google_auth

import io.flutter.plugin.common.MethodChannel
import me.transang.plugins.google_auth.GoogleAuthDelegate.Companion.ERR_META_OPERATION_IN_PROGRESS

open class ResultConsumer {
	private var result: MethodChannel.Result? = null
	protected fun setup(newResult: MethodChannel.Result): Boolean {
		if (result != null) {
			newResult.error(ERR_META_OPERATION_IN_PROGRESS, "Operation in progress", null)
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