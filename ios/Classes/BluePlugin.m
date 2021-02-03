#import "BluePlugin.h"
#if __has_include(<blue/blue-Swift.h>)
#import <blue/blue-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "blue-Swift.h"
#endif

@implementation BluePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBluePlugin registerWithRegistrar:registrar];
}
@end
