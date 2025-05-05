package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.taptap.sdk.moment.TapTapMoment

class Moment(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTdsInterface
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _tapMomentCallback : TapTapMoment.TapTapMomentCallback

    fun init()
    {
        _initCallbacks()

        TapTapMoment.setCallback(_tapMomentCallback)
    }

    fun openPage()
    {
        TapTapMoment.open()
    }

    fun _initCallbacks()
    {
        _tapMomentCallback = object : TapTapMoment.TapTapMomentCallback
        {
            override fun onCallback(code: Int, msg: String?)
            {
                _godotTdsPlugin.emitPluginSignal("onTapMomentReturn", code, "TapMoment: $msg")
            }
        }
    }
}