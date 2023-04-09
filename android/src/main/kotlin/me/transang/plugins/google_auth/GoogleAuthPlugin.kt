package me.transang.plugins.google_auth

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

class GoogleAuthPlugin : FlutterPlugin, ActivityAware {
	private var delegate: GoogleAuthDelegate? = null
	private var detachFromEngineCallback: Runnable? = null
	override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
		val methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
		delegate = GoogleAuthDelegate(flutterPluginBinding.applicationContext)
		methodChannel.setMethodCallHandler(GoogleAuthMethodCallHandler(delegate!!))
		detachFromEngineCallback = Runnable {
			methodChannel.setMethodCallHandler(null)
			delegate = null
		}
	}

	override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
		detachFromEngineCallback!!.run()
		detachFromEngineCallback = null
	}

	override fun onAttachedToActivity(binding: ActivityPluginBinding) {
		delegate!!.attachToActivity(binding)
	}

	override fun onDetachedFromActivityForConfigChanges() {
		delegate!!.detachFromActivity()
	}

	override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
		delegate!!.attachToActivity(binding)
	}

	override fun onDetachedFromActivity() {
		delegate!!.detachFromActivity()
	}

	companion object {
		private const val CHANNEL_NAME = "me.transang.plugins.google_auth/channel"
	}
}