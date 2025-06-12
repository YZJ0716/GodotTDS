package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.taptap.sdk.compliance.TapTapCompliance
import com.taptap.sdk.compliance.TapTapComplianceCallback

class Compliance(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTdsInterface
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _antiComplianceCallback : TapTapComplianceCallback

    init
    {
        _initCallbacks()
    }

    fun startUp()
    {
        if (_godotTdsPlugin.isLoggedIn())
        {
            val userIdentifier = _godotTdsPlugin.getTapAccount().getAccountOpenId();
            TapTapCompliance.registerComplianceCallback(_antiComplianceCallback)
            TapTapCompliance.startup(activity = _activity, userId = userIdentifier)
        }
    }

    fun _initCallbacks()
    {
        // https://developer.taptap.cn/docs/sdk/anti-addiction/guide/

        _antiComplianceCallback = object : TapTapComplianceCallback
        {
            override fun onComplianceResult(code: Int, extra: Map<String, Any>?)
            {
                _godotTdsPlugin.emitPluginSignal("onComplianceReturn", code, extra.toString())
            }
        }
    }
}
