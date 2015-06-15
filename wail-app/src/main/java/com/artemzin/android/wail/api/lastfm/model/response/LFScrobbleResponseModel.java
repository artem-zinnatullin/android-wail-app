package com.artemzin.android.wail.api.lastfm.model.response;

import com.artemzin.android.wail.api.lastfm.LFApiException;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class LFScrobbleResponseModel extends LFBaseResponseModel {
    private String json;

    protected LFScrobbleResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFScrobbleResponseModel parseFromJSON(String json) throws LFApiException {
        final LFScrobbleResponseModel responseModel = new LFScrobbleResponseModel(json);
        responseModel.json = json;
        return responseModel;
    }

    @Override
    public String toString() {
        return json;
    }
}
