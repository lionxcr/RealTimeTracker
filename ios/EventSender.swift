//
//  EventSender.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class EventEmitter {
    
    public static var sharedInstance = EventEmitter()
    
    private static var eventEmitter: ReactNativeEventEmitter!
    
    private static var hasListeners: Bool!
    
    private init() {}
    
    func registerEmitter(eventEmitter: ReactNativeEventEmitter){
        EventEmitter.eventEmitter = eventEmitter
    }
    
    func registerListener() {
        EventEmitter.hasListeners = true
    }
    
    func unRegisterListener() {
        EventEmitter.hasListeners = false
    }
    
    func dispatch(name: String, body: Any?) {
        if (EventEmitter.hasListeners == true) {
            if (name == Constants.JS_LOCATION_EVENT_NAME || name == Constants.JS_LOCATION_EVENT_DENIED_NAME) {
                EventEmitter.eventEmitter.sendEvent(withName: name, body: body)
            }
        }
    }
}
