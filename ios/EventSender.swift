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
    
    private init() {}
    
    func registerEmitter(eventEmitter: ReactNativeEventEmitter){
        EventEmitter.self.eventEmitter = eventEmitter
    }
    
    func dispatch(name: String, body: Any?) {
        EventEmitter.eventEmitter.sendEvent(withName: name, body: body)
    }
    
    lazy var allEvents: [String] = {
        var allEventNames: [String] = []
        allEventNames.append(Constants.JS_LOCATION_EVENT_DENIED_NAME)
        allEventNames.append(Constants.JS_LOCATION_EVENT_NAME)
        return allEventNames
    }()
}
