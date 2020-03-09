/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TouchableHighlight, DeviceEventEmitter, SafeAreaView } from 'react-native';
import RnRealTimeTracker from 'RealTimeTracker';

export default class App extends Component {
  state = {
    currentLocation: '',
    foregroundLocation: ''
  };

  componentDidMount() {
    this.serviceSubscription = DeviceEventEmitter.addListener(
      RnRealTimeTracker.trackerServiceEvent,
      location => {
        console.log('HERE FOREGROUND', location)
        this.setState({
          foregroundLocation: location
        }, () => console.log(
          `Received Coordinates from service at ${new Date(
            location.timestamp,
          ).toTimeString()}: `,
          location.latitude,
          location.longitude,
        ))
      }
    );

    this.currentLocationSubscription = DeviceEventEmitter.addListener(
      RnRealTimeTracker.trackerCurrentLocationEvent,
      location => {
        console.log('HERE CURRENT', location)
        this.setState({
          currentLocation: location
        }, () => console.log(
          `Received Coordinates from current location at ${new Date(
            location.timestamp,
          ).toTimeString()}: `,
          location.latitude,
          location.longitude,
        ))
      }
    );
  }

  componentWillUnmount() {
    this.serviceSubscription.remove();
    this.currentLocationSubscription.remove();
  }

  async enableTracker() {
    if (Platform.OS === 'android') {
      const granted = await RnRealTimeTracker.checkAndroidPermissions()
      if (granted) {
        RnRealTimeTracker.startTracker();
      } else {
        const granted = await RnRealTimeTracker.requestAndroidPermission('Need Permissions', 'This cool example needs your GPS Permissions', 'Cancel', 'Grant');
        if (granted) {
          RnRealTimeTracker.startTracker();
        }
      }
    }
  }

  stopTracker() {
    RnRealTimeTracker.stopTracker();
  }

  async getCurrentLocation() {
    const granted = await RnRealTimeTracker.checkAndroidPermissions()
    if (granted) {
      RnRealTimeTracker.getCurrentLocation();
    } else {
      const granted = await RnRealTimeTracker.requestAndroidPermission('Need Permissions', 'This cool example needs your GPS Permissions', 'Cancel', 'Grant');
      if (granted) {
        RnRealTimeTracker.getCurrentLocation();
      }
    }
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <Text style={styles.welcome}>☆RnRealTimeTracker example☆</Text>
          <Text style={styles.welcome}>☆CURRENT LOCATION☆</Text>
          <Text style={styles.instructions}>{JSON.stringify(this.state.currentLocation)}</Text>
          <TouchableHighlight style={styles.button} onPress={this.getCurrentLocation}>
            <Text style={styles.text}>Get Current Location</Text>
          </TouchableHighlight>
          <Text style={styles.welcome}>☆FOREGROUND LOCATION☆</Text>
          <Text style={styles.instructions}>{JSON.stringify(this.state.foregroundLocation)}</Text>
          <TouchableHighlight style={styles.button} onPress={this.enableTracker}>
            <Text style={styles.text}>Enable Location</Text>
          </TouchableHighlight>
          <TouchableHighlight style={styles.button} onPress={this.stopTracker}>
            <Text style={styles.text}>Cancel Location</Text>
          </TouchableHighlight>
        </View>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    height: '100%',
    flexDirection: 'column',
    paddingHorizontal: 20,
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  button: {
    marginVertical: 40,
    backgroundColor: '#2b5082',
    padding: 20,
  },
  text: {
    color: '#fff',
    textAlign: 'center',
  }
});
