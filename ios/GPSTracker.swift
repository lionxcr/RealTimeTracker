//
//  BackgroundTracker.swift
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import CoreLocation

struct FAILURES {
    static let PERMISSIONS_DENIED = "persmissions_denied"
    static let SYSTEM_ERROR = "system_error"
}

class GPSTracker: NSObject, CLLocationManagerDelegate {
    private var locationManager: CLLocationManager = CLLocationManager()
    private let eventEmiter: EventEmiter = EventEmiter()
    var timeInterval: Double = 0.30
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.distanceFilter = kCLLocationAccuracyBest
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
    }
    
    func startLocationManager() {
        locationManager.requestAlwaysAuthorization()
    }
    
    func stopLocationManager() {
        locationManager.stopUpdatingLocation()
    }
    
    func getCurrentLocation() -> CLLocation? {
        startLocationManager()
        if (CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
        CLLocationManager.authorizationStatus() == .authorizedAlways) {
            return locationManager.location
        } else {
            return nil
        }
    }
    
    private func scheduleTimer(location: CLLocation) {
        DispatchQueue.global().asyncAfter(deadline: .now() + timeInterval, execute: {
            self.sendLocationEvent(location: location)
        })
    }
    
    private func sendLocationEvent(location: CLLocation){
        var eventData: [AnyHashable: Any] = [:]
        eventData.updateValue(location.coordinate.latitude, forKey: Constants.JS_LOCATION_LAT_KEY)
        eventData.updateValue(location.coordinate.longitude, forKey: Constants.JS_LOCATION_LON_KEY)
        eventData.updateValue(Date().timeIntervalSinceNow, forKey: Constants.JS_LOCATION_TIME_KEY)
        eventEmiter.sendEvent(withName: Constants.JS_LOCATION_EVENT_NAME, body: eventData)
    }
    
    private func sendFailureEvent(reason: String, message: String?){
        var eventData: [AnyHashable: Any] = [:]
        eventData.updateValue(reason, forKey: "error")
        eventData.updateValue(message ?? "", forKey: "description")
        eventEmiter.sendEvent(withName: Constants.JS_LOCATION_EVENT_DENIED_NAME, body: eventData)
    }
    
    internal func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .denied:
            sendFailureEvent(reason: FAILURES.PERMISSIONS_DENIED, message: nil)
        default:
            locationManager.startUpdatingLocation()
        }
    }
    
    internal func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        sendFailureEvent(reason: FAILURES.SYSTEM_ERROR, message: error.localizedDescription)
        locationManager.stopUpdatingLocation()
    }
    
    internal func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let locationObj = locations.last {
            if locationObj.horizontalAccuracy < kCLLocationAccuracyNearestTenMeters {
                scheduleTimer(location: locationObj)
            }
        }
    }
}
