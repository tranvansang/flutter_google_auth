package me.transang.plugins.google_auth

import android.content.Intent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import me.transang.plugins.google_auth.GoogleAuthDelegate.Companion.ERR_OTHER
import me.transang.plugins.google_auth.GoogleAuthDelegate.Companion.ERR_PARAM_REQUIRED
import me.transang.plugins.google_auth.GoogleAuthDelegate.Companion.REQUEST_CODE_SIGN_IN
import me.transang.plugins.google_auth.GoogleAuthDelegate.Companion.REQUEST_ONE_TAP

class GoogleAuthPlugin : FlutterPlugin, ActivityAware {
	private var detachFromEngine: (() -> Unit)? = null
	private var detachFromActivity: (() -> Unit)? = null
	private var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null

	// BEGIN attach to engine
	override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
		flutterPluginBinding = binding
		detachFromEngine = {
			detachFromEngine = null
			flutterPluginBinding = null
		}
	}

	override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
		detachFromEngine?.invoke()
	}
	// END attach to engine

	// BEGIN attach to activity
	override fun onAttachedToActivity(binding: ActivityPluginBinding) {
		val pluginBinding = flutterPluginBinding!!
		val methodChannel =
			MethodChannel(pluginBinding.binaryMessenger, "me.transang.plugins.google_auth/channel")
		val delegate = GoogleAuthDelegate(binding.activity, pluginBinding.applicationContext)

		methodChannel.setMethodCallHandler { call, result ->
			try {
				when (call.method) {
					"signIn" -> //				List<String> requestedScopes = call.argument("scopes");
						//				String hostedDomain = call.argument("hostedDomain");
					{
						val clientId: String? = call.argument("clientId")
						if (clientId == null) result.error(
							ERR_PARAM_REQUIRED, "clientId is required", null
						)
						else delegate.signIn(
							clientId, result
						)
					}

					"signOut" -> delegate.signOut(result)

					else -> result.notImplemented()
				}
			} catch (e: Exception) {
				result.error(ERR_OTHER, e.message, e)
			}
		}

		val activityResultListener = object : PluginRegistry.ActivityResultListener {
			override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
				when (requestCode) {
					REQUEST_ONE_TAP -> {
						delegate.onRequestOnTap(data)
						return true
					}

					REQUEST_CODE_SIGN_IN -> {
						delegate.onRequestCodeSignIn(data)
						return true
					}
				}
				return false
			}
		}
		binding.addActivityResultListener(activityResultListener)

		detachFromActivity = {
			detachFromActivity = null
			binding.removeActivityResultListener(activityResultListener)
			methodChannel.setMethodCallHandler(null)
		}
	}

	override fun onDetachedFromActivity() {
		detachFromActivity?.invoke()
	}
	// END attach to activity

	// BEGIN temporary detach from activity
	override fun onDetachedFromActivityForConfigChanges() {
		detachFromActivity?.invoke()
	}

	override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
		onAttachedToActivity(binding)
	}
	// END temporary detach from activity
}