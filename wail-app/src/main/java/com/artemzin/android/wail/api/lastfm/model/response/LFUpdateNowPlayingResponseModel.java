package com.artemzin.android.wail.api.lastfm.model.response;

import com.artemzin.android.wail.api.lastfm.LFApiException;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class LFUpdateNowPlayingResponseModel extends LFBaseResponseModel {
    private String json;

    protected LFUpdateNowPlayingResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFUpdateNowPlayingResponseModel parseFromJSON(String json) throws LFApiException {
        final LFUpdateNowPlayingResponseModel responseModel = new LFUpdateNowPlayingResponseModel(json);
        responseModel.json = json;
        return responseModel;
    }

    @Override
    public String toString() {
        return json;
    }
}
