import {
  NativeModules,
  PermissionsAndroid,
  DeviceEventEmitter
} from 'react-native';


const {
  RnRealTimeTracker
} = NativeModules;

const RNTracker = {
  checkAndroidPermissions: async () => {
    const granted = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION
    );
    return granted;
  },
  requestAndroidPermission: async (title, message, buttonNegative, buttonPositive) => {
    try {
      const granted = await PermissionsAndroid.requestMultiple(
        [
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
          PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION
        ], {
          title,
          message,
          buttonNegative,
          buttonPositive
        }
      );
      const keys = Object.keys(granted);
      const result = keys.map(k => granted[k]);
      let permissionsGranted = true;
      result.forEach(r => {
        if (r !== 'granted'){
          permissionsGranted = false;
        }
      });
      return permissionsGranted;
    } catch (error) {
      return false;
    }
  },
  startTracker: () => RnRealTimeTracker.startBackgroundLocation(),
  checkGPSAndroidStatus: () => RnRealTimeTracker.checkGPSStatus(),
  stopTracker: () => RnRealTimeTracker.stopBackgroundLocation(),
  getCurrentLocation: () => RnRealTimeTracker.getCurrentLocationForUser(),
  trackerServiceEvent: (handler) => DeviceEventEmitter.addListener(
    RnRealTimeTracker.RN_LOCATION_EVENT,
    location => handler(location)
  ),
  trackerServiceFailedEvent: (handler) => DeviceEventEmitter.addListener(
    RnRealTimeTracker.RN_LOCATION_EVENT_DENIED,
    error => handler(error)
  )
}

export default RNTracker;