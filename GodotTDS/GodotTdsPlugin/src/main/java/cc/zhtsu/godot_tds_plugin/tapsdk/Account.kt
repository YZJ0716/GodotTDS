package cc.zhtsu.godot_tds_plugin.tapsdk

import android.app.Activity
import cc.zhtsu.godot_tds_plugin.GodotTdsPlugin
import cc.zhtsu.godot_tds_plugin.StateCode
import cc.zhtsu.godot_tds_plugin.TapTdsInterface
import com.taptap.sdk.BuildConfig
import com.taptap.sdk.achievement.options.TapTapAchievementOptions
import com.taptap.sdk.compliance.option.TapTapComplianceOptions
import com.taptap.sdk.core.TapTapRegion
import com.taptap.sdk.core.TapTapSdk
import com.taptap.sdk.core.TapTapSdkOptions
import com.taptap.sdk.kit.internal.callback.TapTapCallback
import com.taptap.sdk.kit.internal.exception.TapTapException
import com.taptap.sdk.login.Scopes.SCOPE_PUBLIC_PROFILE
import com.taptap.sdk.login.TapTapAccount
import com.taptap.sdk.login.TapTapLogin
import com.taptap.sdk.login.TapTapLogin.loginWithScopes


class Account(activity : Activity, godotTdsPlugin: GodotTdsPlugin) : TapTdsInterface
{
    override var _activity : Activity = activity
    override var _godotTdsPlugin : GodotTdsPlugin = godotTdsPlugin

    init
    {
        _initCallbacks()
    }

    private lateinit var _logInCallback : TapTapCallback<TapTapAccount>

    fun init(clientId : String, clientToken : String)
    {
        _activity.runOnUiThread {
            val enableLog: Boolean = BuildConfig.DEBUG

            val tapSdkOptions: TapTapSdkOptions = TapTapSdkOptions(
                clientId,
                clientToken,
                TapTapRegion.CN
            )

            val options = arrayOf(
                TapTapComplianceOptions(
                    showSwitchAccount = true,
                    useAgeRange = false,
                ),
                TapTapAchievementOptions(
                    enableToast = true
                )
            )

            tapSdkOptions.enableLog = enableLog

            TapTapSdk.init(
                context = _activity,
                sdkOptions = tapSdkOptions,
                options = options
            )
        }
    }

    fun logIn()
    {
        val scopes = arrayOf<String>(SCOPE_PUBLIC_PROFILE)
        loginWithScopes(_activity, scopes, _logInCallback);
    }

    fun logOut()
    {
        TapTapLogin.logout()
    }

    fun isLoggedIn() : Boolean
    {
        return TapTapLogin.getCurrentTapAccount() != null;
    }

    private fun getCurrentTapAccount() : TapTapAccount?
    {
        return TapTapLogin.getCurrentTapAccount()
    }

    fun getCurrentTapAccountAsString() : String
    {
        return TapTapLogin.getCurrentTapAccount().toString()
    }

    fun getAccountOpenId() : String
    {
        val tapTapAccount = getCurrentTapAccount()

        if (tapTapAccount != null)
            return tapTapAccount.openId

        return "Error: Invalid OpenId"
    }

    fun getAccountName() : String
    {
        val tapTapAccount = getCurrentTapAccount()

        if (tapTapAccount?.name != null)
            return tapTapAccount.name!!

        return "Error: Invalid Name"
    }

    fun getAccountAvatarUrl() : String
    {
        val tapTapAccount = getCurrentTapAccount()

        if (tapTapAccount?.avatar != null)
            return tapTapAccount.avatar!!

        return "Error: Invalid Avatar Url"
    }

    fun getAccountEmail() : String
    {
        val tapTapAccount = getCurrentTapAccount()

        if (tapTapAccount?.email != null)
            return tapTapAccount.email!!

        return "Error: Invalid Avatar Url"
    }

    fun getAccountUnionId() : String
    {
        val tapTapAccount = getCurrentTapAccount()

        if (tapTapAccount?.unionId != null)
            return tapTapAccount.unionId

        return "Error: Invalid UnionId"
    }

    fun _initCallbacks()
    {
        _logInCallback = object : TapTapCallback<TapTapAccount>
        {
            override fun onSuccess(result: TapTapAccount)
            {
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", StateCode.LOG_IN_SUCCESS, result.name!!)
            }

            override fun onFail(exception: TapTapException)
            {
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", StateCode.LOG_IN_FAIL, exception.message.toString())
            }

            override fun onCancel()
            {
                _godotTdsPlugin.emitPluginSignal("onLogInReturn", StateCode.LOG_IN_CANCEL, "onCancel")
            }
        }
    }
}