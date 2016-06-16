# Creating a Custom Cordova Plugin

At Wanari, we usually prefer native Android & iOS development and my colleagues have recently written quite a few articles on these topics. However, as a web developer, cross platform mobile app development options stand closer to my heart. In this article, I provide an example for creating a custom Cordova plugin.

Cordova is a framework for cross-platform mobile development. It's based on HTML and Javascript, making it easy to include web developers on mobile projects. It's advantageous, but we definitely cannot achieve everything with these frontend tools. So, we have to use native code, this is where plugins come into play. I'll look at the android part of them.

If you don't care about the blah-blah and are just looking for a working example, check this out:
https://github.com/damas888/cordova-plugin-sample

To test it, just insert the following code into the onDeviceReady function of the PROJECT_HOME\www\js\index.js file
```
cordova.exec(
	function(message) {
		alert(message);
	}, 
	function(){
		alert("Error calling the plugin");
	}, 
	"Sample", 
	"hello", 
	["Wanari"]
);
```
Now, the pros are afar, let's investigate the anatomy of the plugins!
The linked example shows, the structure of it is quite clear. We can see, the heart of them is the plugin.xml.

###plugin.xml
It doesn't look complicated. However, if you mess something up, it will be quite hard to find the mistake:

1. you will receive a pure Class not found message,
2. without errors in the log,
3. while the CLI will just show the plugin is added.

Take a look at the important elements of it:

**`<plugin>`** - The root element of the file, has three attributes: namespace, identifier and version.

**`<engines>`** - Here, you can define which Cordova versions will be supported by this plugin. It's not a mandatory attribute, but do not ever try to skip it if you want to provide your stuff to the public.

**`<assets>`** - Choose files or folders to copy into the Cordova app.

**`<js-module>`** - This part enables you, using the CommonJS module pattern, to include js files in the plugin. The advantage is that you don't need to import these files to the application code. The clobbers sub-attribute specify that the module.exports will be inserted into the window object. So, you will be able to simply access through the value of it.

**`<platform>`** - Platform specific information goes here, the name attribute will define the supported platform.

**`<source-file>`** - Contains native functionality. Typically, you just include one or a few executables this way, but you might need to use this tag many times, for example if you want to run an activity from the plugin.

**`<config-file>`** - It will pass the configuration files to the destination platform. A required part is to add the plugin to the config.xml through the feature attribute. Permissions, activities and other settings can also be added to the AndroidManifest.

The following code snippet shows how to include an activity to the plugin, giving permission to use the phone camera:
```
<platform name="android">
	<config-file target="AndroidManifest.xml" parent="/manifest">
		<uses-permission android:name="android.permission.CAMERA" />
	</config-file>
	<config-file target="AndroidManifest.xml" parent="/manifest/application">
		<activity android:name="com.imense.imenseDemoContinuousEU.ImenseANPR"
				android:screenOrientation="portrait"
				android:configChanges="keyboardHidden"
				android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
				android:windowSoftInputMode="stateAlwaysHidden"
				android:launchMode="singleTop">
		</activity>
	</config-file>
	<config-file target="res/xml/config.xml" parent="/*">
		<feature name="ANPRScanner">
			<param name="android-package" value="hu.wanari.plugin.anprscanner.ANPRScanner"/>
		</feature>
	</config-file>
	<source-file 
		src="src/android/imense/imenseDemoContinuousEU/CameraManager.java" 
		target-dir="src/hu/wanari/plugin" />
	<source-file 
		src="src/android/imense/imenseDemoContinuousEU/CameraConfigurationManager.java" 
		target-dir="src/hu/wanari/plugin" />
	...
</platform>
```

###Javascript Interface
The script at the top, calling the plugin with the cordova.exec, works fine, but you can use another method instead.

You can wrap this exec method to a js function.
As I've explained above: if you declare that in the plugin.xml, you won't have to refer to this js file in the application code.

Based on my plugin example, it will look something like this:
```
var success = function(message) {
	alert(message);
}
var failure = function() {
	alert("Error calling Sample Plugin");
}
sample.hello(success, failure, "Wanari");
```
It's widely accepted as a best practice, although I still prefer to include the exec directly. 

In my point of view this function contains almost nothing. Practically, all that happens is that you pass through parameters from a js function to a Cordova function. Really awesome! :stuck_out_tongue: You obviously cannot include the success/fail callbacks to the js, as they are based on the application logic. Also, why would we store teeny js pieces in the plugin while we presumably keep all of our scripts in the same place? Confusing.

Let's make a deal! Both methods are right, it's just up to your taste... but I suggest removing this unnecessary junk :smile: delete that js file and clean the js-module from the plugin.xml.

###Native Java
Not too exciting.. We have an object that has to extend the CordovaPlugin. 

The entry point is the execute method, with the following signature:
```
@Override
public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException { 
	...
}
```
The first parameter decides what the plugin would do. We place it into an if or, rather, a switch statement. The data contains the parameters while the callbackStatement is responsible for transmitting the result.

###Possible mistakes related to the plugins
- Under the development phase, when you introduce a new plugin, it's not enough to add it in the cordova CLI! They will be installed when you specify the target platforms. So, in our case, use the cordova platform rm android / cordova platform add android commands!
- Always include the version attribute to the <plugin> tag! Although it's not mandatory, to avoid it is bad practice.
- You can easily test your Cordova app in a browser, but it's not enough for the plugins. You can use an emulator or, in my opinion, the best way is to grab a real device.
- Do not commit platforms and publicly accessible plugins to the version control! Platforms should be added locally, plugins will be fetched at compilation.

I hope this was useful for fellow web developers & generally for devs experimenting with cross platform solutions! Let me know if you have any questions & I will try to answer them to the best of my knowledge! :smile:
