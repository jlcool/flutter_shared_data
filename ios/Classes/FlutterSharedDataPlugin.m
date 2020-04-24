#import "FlutterSharedDataPlugin.h"

@implementation FlutterSharedDataPlugin
{
    NSString *sharedText;
}
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_shared_data"
            binaryMessenger:[registrar messenger]];
  FlutterSharedDataPlugin* instance = [[FlutterSharedDataPlugin alloc] init];
    [registrar addApplicationDelegate:instance];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getSharedPath" isEqualToString:call.method]) {
    result(sharedText);
      sharedText=nil;
  } else {
    result(FlutterMethodNotImplemented);
  }
}
# pragma mark - AppDelegate
-(BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url {

    if ([url.absoluteString hasPrefix:@"file://"])
    {
       sharedText=url.path;
    }
    
    return NO;
}

-(BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    if ([url.absoluteString hasPrefix:@"file://"])
    {
        sharedText=url.path;
    }
    return NO;
}

-(BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    if ([url.absoluteString hasPrefix:@"file://"])
    {
        sharedText=url.path;
    }
    return NO;
}
@end
