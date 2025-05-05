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

    private var isTapSDKConfigValid : Boolean = true
    private var isTapADNConfigValid : Boolean = true

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
            isTapSDKConfigValid = false
        }

        _tapAccount.init(clientId, clientToken)
    }

    @UsedByGodot
    fun initTapAdn(mediaId: Long, mediaName: String, mediaKey: String, clientId: String)
    {
        if (mediaId == -1L || mediaName == "" || mediaKey == "")
        {
            isTapADNConfigValid = false
        }

        _checkTapSDKConfig {
            _tapMoment.init()
            _tapAchievement.init()
            _tapGift.init(clientId)
            _tapLeaderboard.init()

            _initAdSdk(mediaId, mediaName, mediaKey, clientId)
        }
    }

    @UsedByGodot
    fun logIn()
    {
        _checkTapSDKConfig {
            _tapAccount.logIn()
        }
    }

    @UsedByGodot
    fun logOut()
    {
        _checkTapSDKConfig {
            _tapAccount.logOut()
        }
    }

    @UsedByGodot
    fun getCurrentTapAccountAsString() : String
    {
        var userProfile = ""

        _checkTapSDKConfig {
            userProfile = _tapAccount.getCurrentTapAccountAsString()
        }

        return userProfile
    }

    @UsedByGodot
    fun isLoggedIn() : Boolean
    {
        var loggedIn = false

        _checkTapSDKConfig {
            loggedIn = _tapAccount.isLoggedIn()
        }

        return loggedIn
    }

    @UsedByGodot
    fun startUpCompliance()
    {
        _checkTapSDKConfig {
            _tapCompliance.startUp()
        }
    }

    @UsedByGodot
    fun tapMoment()
    {
        _checkTapSDKConfig {
            _tapMoment.openPage()
        }
    }

    @UsedByGodot
    fun showAchievementPage()
    {
        _checkTapSDKConfig {
            _tapAchievement.showAchievementPage()
        }
    }

    @UsedByGodot
    fun unlockAchievement(achievementId : String)
    {
        _checkTapSDKConfig {
            _tapAchievement.unlockAchievement(achievementId)
        }
    }

    @UsedByGodot
    fun growAchievementSteps(displayId : String, steps : Int)
    {
        _checkTapSDKConfig {
            _tapAchievement.growAchievementSteps(displayId, steps)
        }
    }

    @UsedByGodot
    fun setShowAchievementToast(show : Boolean)
    {
        _checkTapSDKConfig {
            _tapAchievement.setShowAchievementToast(show)
        }
    }

    @UsedByGodot
    fun submitGiftCode(giftCode : String)
    {
        _checkTapSDKConfig {
            _tapGift.submitGiftCode(giftCode)
        }
    }

    @UsedByGodot
    fun submitLeaderboardScore(leaderboardName : String, score : Long)
    {
        _checkTapSDKConfig {
            _tapLeaderboard.submitLeaderboardScore(leaderboardName, score)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardSectionRankings(leaderboardName : String, start : Int, end : Int)
    {
        _checkTapSDKConfig {
            _tapLeaderboard.fetchLeaderboardSectionRankings(leaderboardName, start, end)
        }
    }

    @UsedByGodot
    fun fetchLeaderboardUserAroundRankings(leaderboardName : String, count : Int)
    {
        _checkTapSDKConfig {
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
        _checkTapADNConfig {
            _splashAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showSplashAd()
    {
        _checkTapADNConfig {
            _splashAd.show()
        }
    }

    @UsedByGodot
    fun disposeSplashAd()
    {
        _checkTapADNConfig {
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
        _checkTapADNConfig {
            _rewardVideoAd.load(spaceId, rewardName, rewardAmount, extraInfo, gameUserId)
        }
    }

    @UsedByGodot
    fun showRewardVideoAd()
    {
        _checkTapADNConfig {
            _rewardVideoAd.show()
        }
    }

    @UsedByGodot
    fun loadBannerAd(spaceId : Int)
    {
        _checkTapADNConfig {
            _bannerAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showBannerAd(gravity : Int, height : Int)
    {
        _checkTapADNConfig {
            _bannerAd.show(gravity, height)
        }
    }

    @UsedByGodot
    fun disposeBannerAd()
    {
        _checkTapADNConfig {
            _bannerAd.dispose()
        }
    }

    @UsedByGodot
    fun loadInterstitialAd(spaceId : Int)
    {
        _checkTapADNConfig {
            _interstitialAd.load(spaceId)
        }
    }

    @UsedByGodot
    fun showInterstitialAd()
    {
        _checkTapADNConfig {
            _interstitialAd.show()
        }
    }

    @UsedByGodot
    fun loadFeedAd(spaceId : Int, query : String)
    {
        _checkTapADNConfig {
            _feedAd.load(spaceId, query)
        }
    }

    @UsedByGodot
    fun showFeedAd(gravity : Int, height : Int)
    {
        _checkTapADNConfig {
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

    fun _checkTapSDKConfig(block : () -> Unit)
    {
        if (isTapSDKConfigValid)
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

    fun _checkTapADNConfig(block : () -> Unit)
    {
        if (isTapADNConfigValid)
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