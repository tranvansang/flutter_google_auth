package me.transang.plugins.google_auth

import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

class GoogleAuthDelegate(private val context: Context) : Activity() {
	private val signInClient: SignInClient = Identity.getSignInClient(context)
	private val googleAuthActivity: GoogleAuthActivity = GoogleAuthActivity(signInClient)
	private var detachFromActivityRunnable: Runnable? = null
	private var activity: Activity? = null
	private var resultConsumer: ResultConsumer<String>? = null

	fun attachToActivity(activityPluginBinding: ActivityPluginBinding) {
		activityPluginBinding.addActivityResultListener(googleAuthActivity)
		activity = activityPluginBinding.getActivity()
		detachFromActivityRunnable = Runnable {
			activity = null
			activityPluginBinding.removeActivityResultListener(googleAuthActivity)
		}
	}

	fun detachFromActivity() {
		detachFromActivityRunnable!!.run()
		detachFromActivityRunnable = null
	}

	fun signIn(
		clientId: String,
		result: MethodChannel.Result
	) {
		assert(resultConsumer == null)
		resultConsumer = ResultConsumer(result) { resultConsumer = null }
		googleAuthActivity.setResultConsumer(resultConsumer)
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
					activity!!.startIntentSenderForResult(
						beginSignInResult.pendingIntent.intentSender,
						GoogleAuthActivity.REQUEST_ONE_TAP,
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
			.addOnFailureListener { e ->
				activity!!.startActivityForResult(
					GoogleSignIn
						.getClient(
							context,
							GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //								.requestEmail()
								.requestIdToken(clientId) //								.requestServerAuthCode(clientId)
								.build()
						)
						.signInIntent,
					GoogleAuthActivity.REQUEST_CODE_SIGN_IN
				)
			}
	}

	fun signOut() {
		signInClient.signOut()
	}

	companion object {
		private const val TAG = "GoogleSignInDelegate"
	}
}