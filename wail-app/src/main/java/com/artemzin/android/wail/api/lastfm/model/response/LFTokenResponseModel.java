package com.artemzin.android.wail.api.lastfm.model.response;

import com.artemzin.android.wail.api.lastfm.LFApiException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class LFTokenResponseModel extends LFBaseResponseModel {
    private String token;

    protected LFTokenResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFTokenResponseModel parseFromJson(String json) throws LFApiException {
        try {
            final LFTokenResponseModel tokenModel = new LFTokenResponseModel(json);
            final JSONObject tokenJson = tokenModel.getJsonObject();

            tokenModel.setToken(tokenJson.getString("token"));

            return tokenModel;
        } catch (JSONException e) {
            throw LFApiException.newDataFormatErrorInstance(null, e.getMessage());
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
