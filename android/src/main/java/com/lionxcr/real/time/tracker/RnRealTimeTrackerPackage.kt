package com.lionxcr.real.time.tracker

import java.util.Arrays

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JavaScriptModule
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class RnRealTimeTrackerPackage : ReactPackage {

    fun createJSModules(): MutableList<Class<out JavaScriptModule>> {
        return emptyList<Class<out JavaScriptModule>>().toMutableList()
    }

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return Arrays.asList<NativeModule>(RnRealTimeTrackerModule(reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
