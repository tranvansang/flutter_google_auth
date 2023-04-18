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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import io.flutter.plugin.common.MethodChannel

class GoogleAuthDelegate(private val activity: Activity, private val applicationContext: Context) :
	ResultConsumer() {
	companion object {
		const val ERR_EMPTY_TOKEN_RETURNED = "EMPTY_TOKEN_RETURNED"
		const val ERR_FAIL_TO_SEND_INTENT = "FAIL_TO_SEND_INTENT"
		const val ERR_API_SIGN_IN_REQUIRED = "API_SIGN_IN_REQUIRED"
		const val ERR_API_INVALID_ACCOUNT = "API_INVALID_ACCOUNT"
		const val ERR_API_RESOLUTION_REQUIRED = "API_RESOLUTION_REQUIRED"
		const val ERR_API_NETWORK_ERROR = "API_NETWORK_ERROR"
		const val ERR_API_INTERNAL_ERROR = "API_INTERNAL_ERROR"
		const val ERR_API_DEVELOPER_ERROR = "API_DEVELOPER_ERROR"
		const val ERR_API_ERROR = "API_ERROR"
		const val ERR_API_INTERRUPTED = "API_INTERRUPTED"
		const val ERR_API_TIMEOUT = "API_TIMEOUT"
		const val ERR_API_CANCELED = "API_CANCELED"
		const val ERR_API_API_NOT_CONNECTED = "API_API_NOT_CONNECTED"
		const val ERR_API_REMOTE_EXCEPTION = "API_REMOTE_EXCEPTION"
		const val ERR_API_CONNECTION_SUSPENDED_DURING_CALL = "API_CONNECTION_SUSPENDED_DURING_CALL"
		const val ERR_API_RECONNECTION_TIMED_OUT_DURING_UPDATE = "API_RECONNECTION_TIMED_OUT_DURING_UPDATE"
		const val ERR_API_RECONNECTION_TIMED_OUT = "API_RECONNECTION_TIMED_OUT"
		const val ERR_GSI_SIGN_IN_FAILED = "GSI_SIGN_IN_FAILED"
		const val ERR_GSI_SIGN_IN_CANCELLED = "GSI_SIGN_IN_CANCELLED"
		const val ERR_GSI_SIGN_IN_CURRENTLY_IN_PROGRESS = "GSI_SIGN_IN_CURRENTLY_IN_PROGRESS"
		const val ERR_OTHER = "OTHER"

		const val REQUEST_ONE_TAP = 3000
		const val REQUEST_CODE_SIGN_IN = 3001
	}

	private val signInClient: SignInClient = Identity.getSignInClient(applicationContext)

	fun onRequestOnTap(intent: Intent?) {
		try {
//					SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(data);
//					String idToken = signInCredential.getGoogleIdToken();
//					String username = signInCredential.getId();
//					String password = signInCredential.getPassword();
			val token = signInClient.getSignInCredentialFromIntent(intent).googleIdToken
			if (token != null) returnResult(token)
			else throwError(ERR_EMPTY_TOKEN_RETURNED, "Empty token returned", null)
		} catch (e: Exception) {
			throwOtherError(e)
		}
	}

	fun onRequestCodeSignIn(intent: Intent?) {
		if (intent != null) {
			GoogleSignIn
				.getSignedInAccountFromIntent(intent)
				.addOnSuccessListener { account ->
					run {
						val token = account.idToken
						if (token == null) throwError(ERR_EMPTY_TOKEN_RETURNED, "Empty token returned", null)
						else returnResult(token)
					}
				}
				.addOnFailureListener { e -> throwError(ERR_OTHER, e.message, e) }
		}
	}

	fun signIn(
		clientId: String,
		result: MethodChannel.Result
	) {
		if (!setup(result)) return
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
				} catch (e: Exception) {
					throwOtherError(e)
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
		if (!setup(result)) return
		signInClient.signOut()
		returnResult(true)
	}

	private fun throwOtherError(e: Exception) {
		when (e) {
			is SendIntentException -> throwError(ERR_FAIL_TO_SEND_INTENT, e.message, e)
			is ApiException -> {
				when (e.statusCode) {
					CommonStatusCodes.SIGN_IN_REQUIRED -> throwError(ERR_API_SIGN_IN_REQUIRED, e.message, e)
					CommonStatusCodes.INVALID_ACCOUNT -> throwError(ERR_API_INVALID_ACCOUNT, e.message, e)
					CommonStatusCodes.RESOLUTION_REQUIRED -> throwError(ERR_API_RESOLUTION_REQUIRED, e.message, e)
					CommonStatusCodes.NETWORK_ERROR -> throwError(ERR_API_NETWORK_ERROR, e.message, e)
					CommonStatusCodes.INTERNAL_ERROR -> throwError(ERR_API_INTERNAL_ERROR, e.message, e)
					CommonStatusCodes.DEVELOPER_ERROR -> throwError(ERR_API_DEVELOPER_ERROR, e.message, e)
					CommonStatusCodes.ERROR -> throwError(ERR_API_ERROR, e.message, e)
					CommonStatusCodes.INTERRUPTED -> throwError(ERR_API_INTERRUPTED, e.message, e)
					CommonStatusCodes.TIMEOUT -> throwError(ERR_API_TIMEOUT, e.message, e)
					CommonStatusCodes.CANCELED -> throwError(ERR_API_CANCELED, e.message, e)
					CommonStatusCodes.API_NOT_CONNECTED -> throwError(ERR_API_API_NOT_CONNECTED, e.message, e)
					CommonStatusCodes.REMOTE_EXCEPTION -> throwError(ERR_API_REMOTE_EXCEPTION, e.message, e)
					CommonStatusCodes.CONNECTION_SUSPENDED_DURING_CALL -> throwError(ERR_API_CONNECTION_SUSPENDED_DURING_CALL, e.message, e)
					CommonStatusCodes.RECONNECTION_TIMED_OUT_DURING_UPDATE -> throwError(ERR_API_RECONNECTION_TIMED_OUT_DURING_UPDATE, e.message, e)
					CommonStatusCodes.RECONNECTION_TIMED_OUT -> throwError(ERR_API_RECONNECTION_TIMED_OUT, e.message, e)

					GoogleSignInStatusCodes.SIGN_IN_FAILED -> throwError(ERR_GSI_SIGN_IN_FAILED, e.message, e)
					GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> throwError(ERR_GSI_SIGN_IN_CANCELLED, e.message, e)
					GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> throwError(ERR_GSI_SIGN_IN_CURRENTLY_IN_PROGRESS, e.message, e)

					else -> throwError(ERR_OTHER, e.message, e)
				}
			}
		}
	}

}