package com.artemzin.android.wail.api.lastfm.model.response;

import android.text.TextUtils;

import com.artemzin.android.wail.api.lastfm.LFApiException;

import org.json.JSONObject;

public abstract class LFBaseResponseModel {

    private JSONObject jsonObject;
    private Error error;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    protected LFBaseResponseModel(String json) throws LFApiException {
        try {
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            throw LFApiException.newDataFormatErrorInstance(null, e.getMessage());
        }

        error = Error.optFromJSONObject(jsonObject);

        if (error != null) {
            throw LFApiException.newIntance(error.error, error.message);
        }
    }

    private static class Error {

        private String error;
        private String message;

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public static Error optFromJSONObject(JSONObject jsonObject) {
            final String errorValue = jsonObject.optString("error");

            if (!TextUtils.isEmpty(errorValue)) {
                final Error error = new Error();
                error.error = errorValue;
                error.message = jsonObject.optString("message");
                return error;
            }

            return null;
        }
    }

}
