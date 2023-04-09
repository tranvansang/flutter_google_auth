package me.transang.plugins.google_auth

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import io.flutter.plugin.common.PluginRegistry

class GoogleAuthActivity(signInClient: SignInClient) : PluginRegistry.ActivityResultListener {
	private val signInClient: SignInClient
	private var resultConsumer: ResultConsumer<String>? = null

	init {
		this.signInClient = signInClient
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
		when (requestCode) {
			REQUEST_ONE_TAP -> {
				try {
//					SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(data);
//					String idToken = signInCredential.getGoogleIdToken();
//					String username = signInCredential.getId();
//					String password = signInCredential.getPassword();
					var token = signInClient.getSignInCredentialFromIntent(data).getGoogleIdToken()
					if (token != null) resultConsumer?.consume(token)
					else resultConsumer?.throwError(Exception("Empty token returned"))
				} catch (e: Exception) {
					resultConsumer?.throwError(e)
				}
				return true
			}
			REQUEST_CODE_SIGN_IN -> {
				if (data != null) {
					GoogleSignIn
						.getSignedInAccountFromIntent(data)
						.addOnSuccessListener { account -> {
							var token = account.getIdToken()
							if (token == null) resultConsumer?.throwError(Exception("Empty token from account"))
							else resultConsumer?.consume(token)
						} }
						.addOnFailureListener { e -> resultConsumer?.throwError(e) }
				} else {
					// data is null which is highly unusual for a sign in result.
					resultConsumer?.throwError(null)
				}
				return true
			}
		}
		return false
	}

	fun setResultConsumer(resultConsumer: ResultConsumer<String>?) {
		this.resultConsumer = resultConsumer
	}

	companion object {
		const val REQUEST_ONE_TAP = 3000
		const val REQUEST_CODE_SIGN_IN = 3001
	}
}