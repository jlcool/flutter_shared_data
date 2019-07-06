# flutter_shared_data

这是一个使用flutter获取其他app分享过来的文件的路径

``` dart
await FlutterSharedData.getSharedPath;
```

java 添加以下配置可以打开csv和txt文件
、、、xml
<intent-filter android:scheme="http"
                tools:ignore="AppLinkUrlError">
                <action android:name="android.intent.action.VIEW"/>
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="text/csv" />
                <data android:mimeType="text/plain" />
            </intent-filter>
、、、
ios 添加以下配置

``` xml
<key>CFBundleDocumentTypes</key>
	<array>
		<dict>
			<key>CFBundleTypeName</key>
			<string>com.myapp.common-data</string>
			<key>LSItemContentTypes</key>
			<array>
				<string>com.microsoft.excel.xls</string>
				<string>public.text</string>
			</array>
		</dict>
	</array>
```

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter, view our 
[online documentation](https://flutter.dev/docs), which offers tutorials, 
samples, guidance on mobile development, and a full API reference.
