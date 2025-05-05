package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.taptap.sdk.achievement.TapAchievementCallback
import com.taptap.sdk.achievement.TapTapAchievement
import com.taptap.sdk.achievement.TapTapAchievementResult
import org.json.JSONObject

class Achievement(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTdsInterface
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    private lateinit var _achievementCallback : TapAchievementCallback

    init
    {
        _initCallbacks()
    }

    fun init()
    {
        TapTapAchievement.registerCallback(_achievementCallback)
    }

    fun showAchievementPage()
    {
        TapTapAchievement.showAchievements()
    }

    fun unlockAchievement(achievementId : String)
    {
        TapTapAchievement.unlock(achievementId)
    }

    fun growAchievementSteps(achievementId : String, steps : Int)
    {
        TapTapAchievement.increment(achievementId, steps)
    }

    fun setShowAchievementToast(show : Boolean)
    {
        TapTapAchievement.setToastEnable(show)
    }

    fun _initCallbacks()
    {
        _achievementCallback = object : TapAchievementCallback
        {
            override fun onAchievementSuccess(code: Int, result: TapTapAchievementResult?)
            {
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn", code, result!!.achievementId)
            }

            override fun onAchievementFailure(achievementId: String, errorCode: Int, errorMessage: String)
            {
                _godotTdsPlugin.emitPluginSignal("onAchievementReturn", errorCode, errorMessage)
            }
        }
    }
}