//
//  EventSender.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class EventEmiter: RCTEventEmitter {
    override func sendEvent(withName name: String!, body: Any!) {
        sendEvent(withName: name, body: body)
    }
    
    override func supportedEvents() -> [String]! {
        return [
            Constants.JS_LOCATION_EVENT_DENIED_NAME,
            Constants.JS_LOCATION_EVENT_NAME
        ]
    }
}
