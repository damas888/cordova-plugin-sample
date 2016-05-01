package hu.wanari.plugin.sample;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Sample extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if ("hello".equals(action)) {
            String name = data.getString(0);
            String message = "Hello " + name;
            callbackContext.success(message);
            return true;
        } else {
            return false;
        }
    }
}
