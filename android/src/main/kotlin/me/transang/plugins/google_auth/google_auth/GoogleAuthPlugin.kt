package me.transang.plugins.google_auth

import me.transang.plugins.google_auth.GoogleAuthActivity.REQUEST_CODE_SIGN_IN
import me.transang.plugins.google_auth.GoogleAuthActivity.REQUEST_ONE_TAP
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthDelegate(context: android.content.Context) : Activity() {
	private val signInClient: SignInClient
	private val googleAuthActivity: GoogleAuthActivity
	private var detachFromActivityRunnable: Runnable? = null
	private var activity: Activity? = null
	private val context: android.content.Context
	private var resultConsumer: ResultConsumer<String>? = null

	init {
		this.context = context
		signInClient = Identity.getSignInClient(context)
		googleAuthActivity = GoogleAuthActivity(signInClient)
	}

	fun attachToActivity(activityPluginBinding: ActivityPluginBinding) {
		activityPluginBinding.addActivityResultListener(googleAuthActivity)
		activity = activityPluginBinding.getActivity()
		detachFromActivityRunnable = Runnable {
			activity = null
			activityPluginBinding.removeActivityResultListener(googleAuthActivity)
		}
	}

	fun detachFromActivity() {
		detachFromActivityRunnable.run()
		detachFromActivityRunnable = null
	}

	fun signIn(
		clientId: String?,
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
					activity.startIntentSenderForResult(
						beginSignInResult.getPendingIntent().getIntentSender(),
						REQUEST_ONE_TAP,
						null,
						0,
						0,
						0,
						null
					)
				} catch (e: SendIntentException) {
					resultConsumer.throwError(e)
				} catch (e: Exception) {
					resultConsumer.throwError(e)
				}
			}
			.addOnFailureListener { e ->
				activity.startActivityForResult(
					GoogleSignIn
						.getClient(
							context,
							Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //								.requestEmail()
								.requestIdToken(clientId) //								.requestServerAuthCode(clientId)
								.build()
						)
						.getSignInIntent(),
					REQUEST_CODE_SIGN_IN
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