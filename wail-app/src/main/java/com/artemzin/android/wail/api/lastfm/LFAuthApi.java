package com.artemzin.android.wail.api.lastfm;

import com.artemzin.android.wail.api.lastfm.model.response.LFSessionResponseModel;
import com.artemzin.android.wail.api.lastfm.model.response.LFTokenResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.api.network.NetworkRequest;

public class LFAuthApi {

    private LFAuthApi() {
    }

    public static LFTokenResponseModel getToken(String apiKey, String secret)
            throws LFApiException, NetworkException {
        final String requestParams = new LFRequestParamContainer("auth.getToken", secret)
                .addParam(LFApiCommon.PARAM_API_KEY, apiKey)
                .generateRequestParamsAsStringWithSign();

        return LFTokenResponseModel.parseFromJson(NetworkRequest
                .newPostRequestInstance(LFApiCommon.API_ROOT_URL, requestParams)
                .getResponse());
    }

    public static LFSessionResponseModel getSession(String apiKey, String secret, String token)
            throws LFApiException, NetworkException {

        final String requestParams = new LFRequestParamContainer("auth.getSession", secret)
                .addParam("token", token)
                .addParam(LFApiCommon.PARAM_API_KEY, apiKey)
                .generateRequestParamsAsStringWithSign();

        return LFSessionResponseModel.parseFromJson(NetworkRequest
                .newPostRequestInstance(LFApiCommon.API_ROOT_URL, requestParams)
                .getResponse());
    }
}
