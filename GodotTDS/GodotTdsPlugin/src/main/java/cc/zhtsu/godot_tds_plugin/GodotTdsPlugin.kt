package cc.zhtsu.godot_tds_plugin

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import cc.zhtsu.godot_tds_plugin.tapadn.BannerAD
import cc.zhtsu.godot_tds_plugin.tapadn.FeedAD
import cc.zhtsu.godot_tds_plugin.tapadn.InterstitialAD
import cc.zhtsu.godot_tds_plugin.tapadn.RewardVideoAD
import cc.zhtsu.godot_tds_plugin.tapadn.SplashAD
import cc.zhtsu.godot_tds_plugin.tapsdk.Account
import cc.zhtsu.godot_tds_plugin.tapsdk.Achievement
import cc.zhtsu.godot_tds_plugin.tapsdk.Compliance
import cc.zhtsu.godot_tds_plugin.tapsdk.Gift
import cc.zhtsu.godot_tds_plugin.tapsdk.Leaderboard
import cc.zhtsu.godot_tds_plugin.tapsdk.Moment
import com.tapsdk.tapad.TapAdConfig
import com.tapsdk.tapad.TapAdCustomController
import com.tapsdk.tapad.TapAdManager
import com.tapsdk.tapad.TapAdNative
import com.tapsdk.tapad.TapAdSdk
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotFragment
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


class GodotTdsPlugin(godot : Godot) : GodotPlugin(godot)
{
    override fun getPluginName() = "GodotTdsPlugin"

    override fun getPluginSignals(): MutableSet<SignalInfo>
    {
        return mutableSetOf(
            SignalInfo("onLogInReturn", Integer::class.java, String::class.java),
            SignalInfo("onComplianceReturn", Integer::class.java, String::class.java),
            SignalInfo("onTapMomentReturn", Integer::class.java, String::class.java),
            SignalInfo("onAchievementReturn", Integer::class.java, String::class.java),
            SignalInfo("onGiftReturn", Integer::class.java, String::class.java),
            SignalInfo("onLeaderboardReturn", Integer::class.java, String::class.java),
            SignalInfo("onSplashAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onRewardVideoAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onBannerAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onInterstitialAdReturn", Integer::class.java, String::class.java),
            SignalInfo("onFeedAdReturn", Integer::class.java, String::class.java)
        )
    }

    private var _isTapSDKConfigValid : Boolean = true
    private var _isTapADNConfigValid : Boolean = true

    private val _tapAccount = Account(activity!!, this)
    private val _tapCompliance = Compliance(activity!!, this)
    private val _tapMoment = Moment(activity!!, this)
    private val _tapAchievement = Achievement(activity!!, this)
    private val _tapGift = Gift(activity!!, this)
    private val _tapLeaderboard = Leaderboard(activity!!, this)

    private var _tapAdNative : TapAdNative? = null
    private lateinit var _tapAdnCallback : TapAdCustomController

    private val _bannerAd = BannerAD(activity!!, this)
    private val _feedAd = FeedAD(activity!!, this)
    private val _interstitialAd = InterstitialAD(activity!!, this)
    private val _rewardVideoAd = RewardVideoAD(activity!!, this)
    private val _splashAd = SplashAD(activity!!, this)

    fun getTapAccount() : Account { return _tapAccount; }

    @UsedByGodot
    fun initTapSdk(clientId: String, clientToken: String)
    {
        if (clientId == "" || clientToken == "")
        {
            _isTapSDKConfigValid = false
        }

        _checkTapSdkConfig {
            _tapAccount.init(clientId, clientToken)
            _tapMoment.init()
            _tapAchievement.init()
            _tapGift.init(clientId)
            _tapLeaderboard.init()
        }
    }

    @UsedByGodot
    fun initTapAdn(mediaId: Long, mediaName: String, mediaKey: String, clientId: String)
    {
        if (mediaId == -1L || mediaName == "" || mediaKey == "")
        {
            _isTapADNConfigValid = false
        }

        _checkTapSdkConfig {
            _initAdSdk(mediaId, mediaName, mediaKey, clientId)
        }
    }

    @UsedByGodot
    fun logIn()
    {
        _checkTapSdkConfig {
            _tapAccount.logIn()
        }
    }

    @UsedByGodot
    fun logOut()
    {
        _checkTapSdkConfig {
            _tapAccount.logOut()
        }
    }

    @UsedByGodot
    fun getCurrentTapAccountAsString() : String
    {
        var userProfile = ""

        _checkTapSdkConfig {
            userProfile = _tapAccount.getCurrentTapAccountAsString()
        }

        return userProfile
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        var loggedIn = false

        _checkTapSdkConfig {
            loggedIn = _tapAccount.isLoggedIn()
        }

        return loggedIn
    }

    @UsedByGodot
    fun startUpCompliance()
    {
        _checkTapSdkConfig {
            _tapCompliance.startUp()
        }
    }

    @UsedByGodot
    fun tapMoment()
    {
        _checkTapSdkConfig {
            _tapMoment.openPage()
        }
    }

    @UsedByGodot
    fun showAchievementPage()
    {
        _checkTapSdkConfig {
            _tapAchievement.showAchievementPage()
        }
    }

    @UsedByGodot
    fun unlockAchievement(achievementId : String)
    {
        _checkTapSdkConfig {
            _tapAchievement.unlockAchievement(achievementId)
        }
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _checkTapSdkConfig {
            _tapAchievement.growAchievementSteps(displayId, steps)
        }
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _checkTapSdkConfig {
            _tapAchievement.setShowAchievementToast(show)
        }
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _checkTapSdkConfig {
            _tapGift.submitGiftCode(giftCode)
        }
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        _checkTapSdkConfig {
            _tapLeaderboard.fetchLeaderboardUserAroundRankings(leaderboardName, count)
        }
    }

    @UsedByGodot
    fun pushLog(msg : String, error : Boolean)
    {
        if (error)
        {
            Log.e("GodotTdsPlugin", msg)
        }
        else
        {
            Log.v("GodotTdsPlugin", msg)
        }
    }

    @UsedByGodot
    fun getCacheDirPath() : String
    {
        return activity!!.baseContext.cacheDir.absolutePath
    }

    @UsedByGodot
    fun loadSplashAd(spaceId : Int)
    {
        _checkTapAdnConfig {
            _splashAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showSplashAd()
    {
        _checkTapAdnConfig {
            _splashAd.show()
        }
    }

    @UsedByGodot
    fun disposeSplashAd()
    {
        _checkTapAdnConfig {
            _splashAd.dispose()
        }
    }

    @UsedByGodot
    fun loadRewardVideoAd(
        spaceId : Int,
        rewardName : String,
        rewardAmount : Int,
        extraInfo : String,
        gameUserId : String,
    )
    {
        _checkTapAdnConfig {
            _rewardVideoAd.load(spaceId, rewardName, rewardAmount, extraInfo, gameUserId)
        }
    }

    @UsedByGodot
    fun showRewardVideoAd()
    {
        _checkTapAdnConfig {
            _rewardVideoAd.show()
        }
    }

    @UsedByGodot
    fun loadBannerAd(spaceId : Int)
    {
        _checkTapAdnConfig {
            _bannerAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showBannerAd(gravity : Int, height : Int)
    {
        _checkTapAdnConfig {
            _bannerAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun disposeBannerAd()
    {
        _checkTapAdnConfig {
            _bannerAd.dispose()
        }
    }

    @UsedByGodot
    fun loadInterstitialAd(spaceId : Int)
    {
        _checkTapAdnConfig {
            _interstitialAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showInterstitialAd()
    {
        _checkTapAdnConfig {
            _interstitialAd.show()
        }
    }

    @UsedByGodot
    fun loadFeedAd(spaceId : Int, query : String)
    {
        _checkTapAdnConfig {
            _feedAd.load(spaceId, query)
        }
    }

    @UsedByGodot
    fun showFeedAd(gravity : Int, height : Int)
    {
        _checkTapAdnConfig {
            _feedAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun showToast(msg : String)
    {
        activity!!.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Useful for emit signal
    fun emitPluginSignal(signal : String, code : Int, msg : String)
    {
        emitSignal(signal, code, msg)
    }

    fun getTapAdNative() : TapAdNative
    {
        return if (_tapAdNative == null)
        {
            TapAdManager.get().createAdNative(activity)
        }
        else
        {
            _tapAdNative!!
        }
    }

    fun _checkTapSdkConfig(block : () -> Unit)
    {
        if (_isTapSDKConfigValid)
        {
            block()
        }
        else
        {
            val msg : String = "Invalid SDK config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    fun _checkTapAdnConfig(block : () -> Unit)
    {
        if (_isTapADNConfigValid)
        {
            block()
        }
        else
        {
            val msg : String = "Invalid ADN config!"

            showToast(msg);

            Log.e("GodotTdsPlugin", msg)
        }
    }

    private fun _initAdSdk(mediaId : Long, mediaName : String, mediaKey : String, clientId : String)
    {
        TapAdManager.get().requestPermissionIfNecessary(activity)

        _initTapAdnCallback()

        val config = TapAdConfig.Builder()
            .withMediaId(mediaId)
            .withMediaName(mediaName)
            .withMediaKey(mediaKey)
            .withMediaVersion("1")
            .withGameChannel("taptap2")
            .withTapClientId(clientId)
            .shakeEnabled(false)
            .enableDebug(true)
            .withCustomController(_tapAdnCallback)
            .build()

        TapAdSdk.init(activity, config)
    }

    private fun _initTapAdnCallback()
    {
        _tapAdnCallback = object : TapAdCustomController()
        {
            // https://developer.taptap.cn/docs/sdk/tap-adn/tds-tapad/#%E5%88%9D%E5%A7%8B%E5%8C%96
        }
    }
}