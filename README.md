#phonegap-plugin-push [![Build Status](https://travis-ci.org/phonegap/phonegap-plugin-push.svg)](https://travis-ci.org/phonegap/phonegap-plugin-push)

> Register and receive push notifications with Parse Integration

## Installation

This requires phonegap/cordova CLI 5.0+ ( current stable v1.2.2 )

```
cordova plugin add https://github.com/blueshunter/parse-phonegap-plugin-push --variable APP_ID=YOUR_PARSE_APP_ID  --variable CLIENT_KEY=YOUR_PARSE_CLIENT_KEY
```



## Supported Platforms

- Android
- iOS


## Quick Example

```javascript
    PushNotification.register( function() {

    	//alert('Parse successfully register device');

	}, function(e) {

    	//alert('Parse failed to register device');
	});
	
	PushNotification.unregister( function() {
                  
           //alert('Parse successfully unregister device');

        }, function(e) {

            //alert('Parse failed to unregister device');
        });
```

