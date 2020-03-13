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
    var timeInterval: TimeInterval = 30
    weak var timer: Timer?
    var timerDispatchSourceTimer : DispatchSourceTimer?
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.distanceFilter = kCLLocationAccuracyBest
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
    }
    
    func startLocationManager() {
        DispatchQueue.main.async {
            EventEmitter.sharedInstance.registerListener()
            print("STARTING SERVICE")
            self.locationManager.requestAlwaysAuthorization()
            self.scheduleTimer()
        }
    }
    
    func stopLocationManager() {
        DispatchQueue.main.async {
            EventEmitter.sharedInstance.unRegisterListener()
            self.stopTimer()
            self.locationManager.stopUpdatingLocation()
        }
      
    }
    
    private func stopTimer() {
        timer?.invalidate()
        timerDispatchSourceTimer?.cancel()
    }
    
    deinit {
        stopTimer()
    }
    
    func getCurrentLocation() -> CLLocation? {
        if (CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
        CLLocationManager.authorizationStatus() == .authorizedAlways) {
            return locationManager.location
        } else {
            startLocationManager()
            if (CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == .authorizedAlways) {
                locationManager.stopUpdatingLocation()
                return locationManager.location
            } else {
                return nil
            }
        }
    }
    
    private func scheduleTimer() {
        print("STARTED TIMER")
        if #available(iOS 10.0, *) {
            timer = Timer.scheduledTimer(withTimeInterval: 3, repeats: true) { [weak self] _ in
                self?.sendLocationUpdate()
            }

        } else {
            // Fallback on earlier versions
            timerDispatchSourceTimer = DispatchSource.makeTimerSource(flags: [], queue: DispatchQueue.main)
            timerDispatchSourceTimer?.schedule(deadline: .now(), repeating: .seconds(30))
            timerDispatchSourceTimer?.setEventHandler{
                self.sendLocationUpdate()

            }
            timerDispatchSourceTimer?.resume()
        }
    }
    
    @objc func sendLocationUpdate() {
        print("IN TIMER")
        if let location = locationManager.location {
            print("SENDING LOCATION")
            sendLocationEvent(location: location)
        }
    }
    
    private func sendLocationEvent(location: CLLocation){
        var eventData: [AnyHashable: Any] = [:]
        eventData.updateValue(location.coordinate.latitude, forKey: Constants.JS_LOCATION_LAT_KEY)
        eventData.updateValue(location.coordinate.longitude, forKey: Constants.JS_LOCATION_LON_KEY)
        eventData.updateValue(Date().timeIntervalSinceNow, forKey: Constants.JS_LOCATION_TIME_KEY)
        EventEmitter.sharedInstance.dispatch(name: Constants.JS_LOCATION_EVENT_NAME, body: eventData)
    }
    
    private func sendFailureEvent(reason: String, message: String?){
        var eventData: [AnyHashable: Any] = [:]
        eventData.updateValue(reason, forKey: "error")
        eventData.updateValue(message ?? "", forKey: "description")
        EventEmitter.sharedInstance.dispatch(name: Constants.JS_LOCATION_EVENT_DENIED_NAME, body: eventData)
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
        EventEmitter.sharedInstance.unRegisterListener()
        locationManager.stopUpdatingLocation()
    }
}
