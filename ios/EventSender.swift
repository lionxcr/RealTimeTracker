//
//  EventSender.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

@objc(EventEmiter)
class EventEmiter: RCTEventEmitter {
    
    override class func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    override func supportedEvents() -> [String]! {
        return [
            Constants.JS_LOCATION_EVENT_DENIED_NAME,
            Constants.JS_LOCATION_EVENT_NAME
        ]
    }
}
