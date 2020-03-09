import {
  NativeModules,
  PermissionsAndroid
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
    console.log('PERMISSIONS CHECK')
    console.log(granted)
    return granted === PermissionsAndroid.RESULTS.GRANTED;
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
      console.log('PERMISSIONS REQUEST')
      console.log(granted)
      return granted === PermissionsAndroid.RESULTS.GRANTED
    } catch (error) {
      console.error(error);
      return false;
    }
  },
  startTracker: () => RnRealTimeTracker.startBackgroundLocation(),
  stopTracker: () => RnRealTimeTracker.stopBackgroundLocation(),
  getCurrentLocation: () => RnRealTimeTracker.getCurrentLocationForUser(),
  trackerServiceEvent: RnRealTimeTracker.JS_LOCATION_EVENT_NAME,
  trackerCurrentLocationEvent: RnRealTimeTracker.JS_CURRENT_LOCATION_EVENT_NAME
}

export default RNTracker;