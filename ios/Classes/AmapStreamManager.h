//
//  AmapStreamManager.h
//  location_amap
//
//  Created by chenping on 2021/3/8.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@class AmapFlutterStreamHandler;

@interface AmapStreamManager : NSObject

+ (instancetype)sharedInstance ;

@property (nonatomic, strong) AmapFlutterStreamHandler* streamHandler;

@end

@interface AmapFlutterStreamHandler : NSObject<FlutterStreamHandler>

@property (nonatomic, strong) FlutterEventSink eventSink;

@end

NS_ASSUME_NONNULL_END
