module.exports = {
    hello: function (successCallback, errorCallback, name) {
        cordova.exec(successCallback, errorCallback, "Sample", "hello", [name]);
    }
};
