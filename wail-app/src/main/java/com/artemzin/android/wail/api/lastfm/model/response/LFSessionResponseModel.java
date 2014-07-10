package com.artemzin.android.wail.api.lastfm.model.response;

import com.artemzin.android.wail.api.lastfm.LFApiException;

import org.json.JSONException;
import org.json.JSONObject;

public class LFSessionResponseModel extends LFBaseResponseModel {

    private String name;
    private String key;
    private String subscriber;

    protected LFSessionResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFSessionResponseModel parseFromJson(String json) throws LFApiException {
        try {
            final LFSessionResponseModel sessionModel = new LFSessionResponseModel(json);
            final JSONObject sessionJson = sessionModel.getJsonObject().getJSONObject("session");

            sessionModel.name       = sessionJson.optString("name");
            sessionModel.key        = sessionJson.optString("key");
            sessionModel.subscriber = sessionJson.optString("subscriber");

            return sessionModel;
        } catch (JSONException e) {
            throw LFApiException.newDataFormatErrorInstance(null, e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getSubscriber() {
        return subscriber;
    }
}
