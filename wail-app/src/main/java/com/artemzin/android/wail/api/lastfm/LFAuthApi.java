package com.artemzin.android.wail.api.lastfm;

import com.artemzin.android.wail.api.lastfm.model.response.LFSessionResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.api.network.NetworkRequest;

public class LFAuthApi {

    private LFAuthApi() {}

    public static LFSessionResponseModel getMobileSession(String apiKey, String secret, String userName, String password)
                                throws LFApiException, NetworkException {

        final String requestParams = new LFRequestParamContainer("auth.getMobileSession", secret)
                .addParam("username", userName)
                .addParam("password", password)
                .addParam(LFApiCommon.PARAM_API_KEY, apiKey)
                .generateRequestParamsAsStringWithSign();

        return LFSessionResponseModel.parseFromJson(NetworkRequest.newPostRequestInstance(LFApiCommon.API_ROOT_URL, requestParams)
                .getResponse());
    }
}
