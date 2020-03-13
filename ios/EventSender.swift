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
        print("EVENT LISTENER STATUS")
        print(EventEmitter.hasListeners as Any)
        if (EventEmitter.hasListeners == true) {
            EventEmitter.eventEmitter.sendEvent(withName: name, body: body)
        }
    }
    
    lazy var allEvents: [String] = {
        var allEventNames: [String] = []
        allEventNames.append(Constants.JS_LOCATION_EVENT_DENIED_NAME)
        allEventNames.append(Constants.JS_LOCATION_EVENT_NAME)
        return allEventNames
    }()
}
