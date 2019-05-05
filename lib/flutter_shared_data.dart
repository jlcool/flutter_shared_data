import 'dart:async';

import 'package:flutter/services.dart';

class FlutterSharedData {
  static const MethodChannel _channel =
      const MethodChannel('flutter_shared_data');

  static Future<String> get getSharedPath async {
    final String version = await _channel.invokeMethod('getSharedPath');
    return version;
  }
}
