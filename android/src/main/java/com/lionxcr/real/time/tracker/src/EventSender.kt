package com.lionxcr.real.time.tracker.src

import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import javax.annotation.Nullable

interface EventSender {
    fun sendEvents(reactContext: ReactContext, eventName: String, @Nullable params: WritableMap)
}