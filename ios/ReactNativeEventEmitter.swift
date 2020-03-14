//
//  ReactNativeEventEmitter.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/14/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

@objc(ReactNativeEventEmitter)
open class ReactNativeEventEmitter: RCTEventEmitter {
    public override init() {
        super.init()
        EventEmitter.sharedInstance.registerEmitter(eventEmitter: self)
    }
    
    open override class func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @objc open override func supportedEvents() -> [String]! {
        var allEventNames: [String] = []
        allEventNames.append(ConstantDefinitions.CONST_RN_LOCATION_EVENT)
        allEventNames.append(ConstantDefinitions.CONST_RN_LOCATION_EVENT_DENIED)
        return allEventNames
    }
}
