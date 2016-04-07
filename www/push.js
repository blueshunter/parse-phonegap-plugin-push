

var exec = cordova.require('cordova/exec');

var PushNotification = {
    
    register: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'PushNotification',
            'register'
        );
    },

    unregister: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'PushNotification',
            'unregister'
        );
    }
};
module.exports = PushNotification;
