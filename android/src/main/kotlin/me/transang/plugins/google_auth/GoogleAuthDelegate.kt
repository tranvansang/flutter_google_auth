package me.transang.plugins.google_auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.flutter.plugin.common.MethodChannel

class GoogleAuthDelegate(private val activity: Activity, private val applicationContext: Context) {
	private val signInClient: SignInClient = Identity.getSignInClient(applicationContext)
	private var resultConsumer: ResultConsumer<Any>? = null

	fun onRequestOnTap(intent: Intent?) {
		try {
//					SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(data);
//					String idToken = signInCredential.getGoogleIdToken();
//					String username = signInCredential.getId();
//					String password = signInCredential.getPassword();
			val token = signInClient.getSignInCredentialFromIntent(intent).googleIdToken
			if (token != null) resultConsumer?.consume(token)
			else resultConsumer?.throwError(Exception("Empty token returned"))
		} catch (e: Exception) {
			resultConsumer?.throwError(e)
		}
	}

	fun onRequestCodeSignIn(intent: Intent?) {
		if (intent != null) {
			GoogleSignIn
				.getSignedInAccountFromIntent(intent)
				.addOnSuccessListener { account ->
					run {
						val token = account.idToken
						if (token == null) resultConsumer?.throwError(Exception("Empty token from account"))
						else resultConsumer?.consume(token)
					}
				}
				.addOnFailureListener { e -> resultConsumer?.throwError(e) }
		} else {
		}
	}

	private fun setup(result: MethodChannel.Result) {
		if (resultConsumer != null) {
			resultConsumer?.throwError(Exception("New operation arrived before the current one finished"))
		}
		resultConsumer = ResultConsumer(result) { resultConsumer = null }
	}

	fun signIn(
		clientId: String,
		result: MethodChannel.Result
	) {
		setup(result)
		signInClient
			.beginSignIn(
				BeginSignInRequest
					.builder()
					.setGoogleIdTokenRequestOptions(
						BeginSignInRequest.GoogleIdTokenRequestOptions
							.builder()
							.setSupported(true)
							.setServerClientId(clientId)
							.setFilterByAuthorizedAccounts(false)
							.build()
					)
					.setAutoSelectEnabled(false)
					.build()
			)
			.addOnSuccessListener { beginSignInResult ->
				try {
					activity.startIntentSenderForResult(
						beginSignInResult.pendingIntent.intentSender,
						REQUEST_ONE_TAP,
						null,
						0,
						0,
						0,
						null
					)
				} catch (e: SendIntentException) {
					resultConsumer?.throwError(e)
				} catch (e: Exception) {
					resultConsumer?.throwError(e)
				}
			}
			.addOnFailureListener {
				activity.startActivityForResult(
					GoogleSignIn
						.getClient(
							applicationContext,
							GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //								.requestEmail()
								.requestIdToken(clientId) //								.requestServerAuthCode(clientId)
								.build()
						)
						.signInIntent,
					REQUEST_CODE_SIGN_IN
				)
			}
	}

	fun signOut(result: MethodChannel.Result) {
		setup(result)
		signInClient.signOut()
		resultConsumer?.consume(true)
	}

	companion object {
		const val REQUEST_ONE_TAP = 3000
		const val REQUEST_CODE_SIGN_IN = 3001
	}
}