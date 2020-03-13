import {
  NativeModules,
  PermissionsAndroid,
  DeviceEventEmitter,
  NativeEventEmitter,
  Platform
} from "react-native";

const { RnRealTimeTracker, ReactNativeEventEmitter } = NativeModules;

const iOS = () => Platform.OS === "ios";
const emitter = iOS() ? new NativeEventEmitter(ReactNativeEventEmitter) : null;

const RNTracker = {
  checkAndroidPermissions: async () => {
    const granted = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION
    );
    return granted;
  },
  requestAndroidPermission: async (
    title,
    message,
    buttonNegative,
    buttonPositive
  ) => {
    try {
      const granted = await PermissionsAndroid.requestMultiple(
        [
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
          PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION
        ],
        {
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
        if (r !== "granted") {
          permissionsGranted = false;
        }
      });
      return permissionsGranted;
    } catch (error) {
      return false;
    }
  },
  startTracker: () => RnRealTimeTracker.startBackgroundLocation(),
  checkGPSAndroidStatus: () =>
    !iOS()
      ? RnRealTimeTracker.checkGPSStatus()
      : console.warn("Function only available for Android"),
  stopTracker: () => RnRealTimeTracker.stopBackgroundLocation(),
  getCurrentLocation: () => RnRealTimeTracker.getCurrentLocationForUser(),
  trackerServiceEvent: handler =>
    iOS()
      ? emitter.addListener(RnRealTimeTracker.RN_LOCATION_EVENT, location =>
          handler(location)
        )
      : DeviceEventEmitter.addListener(
          RnRealTimeTracker.RN_LOCATION_EVENT,
          location => handler(location)
        ),
  trackerServiceFailedEvent: handler =>
    iOS()
      ? DeviceEventEmitter.addListener(
          RnRealTimeTracker.RN_LOCATION_EVENT_DENIED,
          error => handler(error)
        )
      : console.warn("Function only available for iOS")
};

export default RNTracker;
