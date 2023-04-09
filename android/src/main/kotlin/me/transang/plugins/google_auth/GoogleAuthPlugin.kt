package me.transang.plugins.google_auth

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

class GoogleAuthPlugin : FlutterPlugin, ActivityAware {
	private var delegate: GoogleAuthDelegate? = null
	private var detachFromEngine: (() -> Unit)? = null
	private var detachFromActivity: (() -> Unit)? = null

	// BEGIN attach to engine
	override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
		val methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
		delegate = GoogleAuthDelegate(flutterPluginBinding.applicationContext)
		methodChannel.setMethodCallHandler(GoogleAuthMethodCallHandler(delegate!!))
		detachFromEngine = {
			methodChannel.setMethodCallHandler(null)
			delegate = null
			detachFromEngine = null
		}
	}

	override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
		detachFromEngine?.invoke()
	}
	// END attach to engine

	// BEGIN attach to activity
	override fun onAttachedToActivity(binding: ActivityPluginBinding) {
		binding.addActivityResultListener(delegate!!.activityResultListener)
		delegate!!.activity = binding.activity
		detachFromActivity = {
			delegate!!.activity = null
			binding.removeActivityResultListener(delegate!!.activityResultListener)
			detachFromActivity = null
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


	companion object {
		private const val CHANNEL_NAME = "me.transang.plugins.google_auth/channel"
	}
}