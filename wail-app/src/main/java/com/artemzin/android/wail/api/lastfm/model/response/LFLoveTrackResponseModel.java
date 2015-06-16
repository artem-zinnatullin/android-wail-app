package com.artemzin.android.wail.api.lastfm.model.response;

import com.artemzin.android.wail.api.lastfm.LFApiException;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class LFLoveTrackResponseModel extends LFBaseResponseModel {
    private String json;

    protected LFLoveTrackResponseModel(String json) throws LFApiException {
        super(json);
    }

    public static LFLoveTrackResponseModel parseFromJSON(String json) throws LFApiException {
        final LFLoveTrackResponseModel responseModel = new LFLoveTrackResponseModel(json);
        responseModel.json = json;
        return responseModel;
    }

    @Override
    public String toString() {
        return json;
    }
}
