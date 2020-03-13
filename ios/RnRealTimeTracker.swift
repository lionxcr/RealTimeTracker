//
//  RnRealTimeTracker.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import CoreLocation

@objc(RnRealTimeTracker)
class RnRealtimeTracker: NSObject, RCTBridgeModule {
    
    private let gpsTracker: GPSTracker = GPSTracker()
    
    static func moduleName() -> String! {
        return "RnRealtimeTracker"
    }
    
    private static func constantsExport() -> Dictionary<AnyHashable, Any> {
        var constants: [AnyHashable: Any] = [:]
        constants.updateValue(Constants.JS_LOCATION_EVENT_NAME, forKey: ConstantDefinitions.CONST_RN_LOCATION_EVENT)
        constants.updateValue(Constants.JS_LOCATION_LAT_KEY, forKey: ConstantDefinitions.CONST_RN_LOCATION_LAT)
        constants.updateValue(Constants.JS_LOCATION_LON_KEY, forKey: ConstantDefinitions.CONST_RN_LOCATION_LON)
        constants.updateValue(Constants.JS_LOCATION_TIME_KEY, forKey: ConstantDefinitions.CONST_RN_LOCATION_TIME)
        return constants
    }
    
    static func requiresMainQueueSetup() -> Bool {
      return true
    }
    
    @objc func startBackgroundLocation() {
        gpsTracker.startLocationManager()
    }
    
    @objc func stopBackgroundLocation() {
        gpsTracker.stopLocationManager()
    }
    
    @objc(getCurrentLocationForUser:rejecter:) func getCurrentLocationForUser(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        let currentLocation: CLLocation? = gpsTracker.getCurrentLocation()
        if let location = currentLocation {
            var data: [AnyHashable: Any] = [:]
            data.updateValue(location.coordinate.latitude, forKey: Constants.JS_LOCATION_LAT_KEY)
            data.updateValue(location.coordinate.longitude, forKey: Constants.JS_LOCATION_LON_KEY)
            data.updateValue(Date().timeIntervalSinceNow, forKey: Constants.JS_LOCATION_TIME_KEY)
            resolve(data)
        } else {
            let error = NSError(domain: "Error Getting Location", code: 400, userInfo: nil)
            reject("error", error.domain, error)
        }
    }
    
}
