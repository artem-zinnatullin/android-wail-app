package com.artemzin.android.wail.api.lastfm;

import com.artemzin.android.wail.api.lastfm.model.request.LFArtistAndAlbumRequestModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.api.network.NetworkRequest;

import java.util.List;

public class LFLibraryApi {

    private LFLibraryApi() {}

    public static String addAlbum(String sessionKey, String apiKey, String secret, List<LFArtistAndAlbumRequestModel> artistAndAlbums)
            throws NetworkException, LFApiException {

        final LFRequestParamContainer requestParams = new LFRequestParamContainer("library.addAlbum", secret);

        for (int i = 0; i < artistAndAlbums.size(); i++) {
            final LFArtistAndAlbumRequestModel artistAndAlbum = artistAndAlbums.get(i);

            requestParams.addParam("artist[" + i + "]", artistAndAlbum.getArtist());
            requestParams.addParam("album[" + i + "]", artistAndAlbum.getAlbum());
        }

        requestParams.addParam(LFApiCommon.PARAM_API_KEY, apiKey);
        requestParams.addParam(LFApiCommon.PARAM_SK, sessionKey);

        return NetworkRequest.newPostRequestInstance(
                LFApiCommon.API_ROOT_URL,
                requestParams.generateRequestParamsAsStringWithSign()
        ).getResponse();
    }
}
