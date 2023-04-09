package me.transang.plugins.google_auth

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.PluginRegistry

class GoogleAuthActivityResultListener(
	private val onRequestOnTap: (Intent?) -> Unit,
	private val onRequestCodeSignIn: (Intent?) -> Unit,
) :
	PluginRegistry.ActivityResultListener {
	@RequiresApi(Build.VERSION_CODES.N)
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
		when (requestCode) {
			REQUEST_ONE_TAP -> {
				onRequestOnTap(data)
				return true
			}
			REQUEST_CODE_SIGN_IN -> {
				onRequestCodeSignIn(data)
				return true
			}
		}
		return false
	}

	companion object {
		const val REQUEST_ONE_TAP = 3000
		const val REQUEST_CODE_SIGN_IN = 3001
	}
}