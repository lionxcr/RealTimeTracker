//
//  RNBridge.m
//  RnRealTimeTracker
//
//  Created by Pablo Segura on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RnRealTimeTracker, NSObject)
RCT_EXTERN_METHOD(startBackgroundLocation)
RCT_EXTERN_METHOD(stopBackgroundLocation)
RCT_EXTERN_METHOD(getCurrentLocationForUser:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
@end
