package com.yifeplayte.wavecharge

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookMethod
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelperInit.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "com.android.systemui" -> supportWaveChargeAnimationHook(lpparam)
        }
    }

    private fun supportWaveChargeAnimationHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        findMethod("com.android.keyguard.charge.ChargeUtils") {
            name == "supportWaveChargeAnimation"
        }.hookMethod {
            after { param ->
                var ex = Throwable()
                var stackElement = ex.stackTrace
                var mResult = false
                val classTrue = setOf(
                    "com.android.keyguard.charge.ChargeUtils",
                    "com.android.keyguard.charge.container.MiuiChargeContainerView",
                    "com.android.keyguard.charge.view"
                )
                for (i in stackElement.indices) {
                    //XposedBridge.log("Dump Stack " + i + ": " + stackElement[i].className + "--" + stackElement[i].methodName)
                    when {
                        stackElement[i].className in classTrue -> {
                            mResult = true
                            break
                        }
                    }
                }
                param.result = mResult
            }
        }
        findMethod("com.android.keyguard.charge.wave.WaveView") {
            name == "updateWaveHeight"
        }.hookMethod {
            after { param ->
                XposedHelpers.setIntField(param.thisObject, "mWaveXOffset", 0)
            }
        }
    }
}