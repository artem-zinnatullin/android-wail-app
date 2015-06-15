package com.artemzin.android.wail.api.lastfm;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.wail.api.lastfm.model.request.LFTrackRequestModel;
import com.artemzin.android.wail.api.lastfm.model.response.LFLoveTrackResponseModel;
import com.artemzin.android.wail.api.lastfm.model.response.LFScrobbleResponseModel;
import com.artemzin.android.wail.api.lastfm.model.response.LFUpdateNowPlayingResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.api.network.NetworkRequest;

import java.util.List;

public class LFTrackApi {

    private LFTrackApi() {
    }

    public static LFScrobbleResponseModel scrobble(String sessionKey, String apiKey, String secret, List<LFTrackRequestModel> tracks)
            throws LFApiException, NetworkException {

        LFRequestParamContainer requestParams = new LFRequestParamContainer("track.scrobble", secret);

        for (int i = 0; i < tracks.size(); i++) {
            final LFTrackRequestModel track = tracks.get(i);
            requestParams.addParam("artist[" + i + "]", track.getArtist());
            requestParams.addParam("track[" + i + "]", track.getTrack());
            requestParams.addParam("timestamp[" + i + "]", track.getTimeStamp().toString());

            if (!StringUtil.isNullOrEmpty(track.getAlbum())) {
                requestParams.addParam("album[" + i + "]", track.getAlbum());
            }

            requestParams.addParam("chosenByUser[" + i + "]", track.getChosenByUser().toString());

            if (track.getTrackNumber() != null) {
                requestParams.addParam("trackNumber[" + i + "]", track.getTrackNumber().toString());
            }

            if (track.getDuration() != null) {
                requestParams.addParam("duration[" + i + "]", track.getDuration().toString());
            }
        }

        requestParams.addParam(LFApiCommon.PARAM_API_KEY, apiKey);
        requestParams.addParam(LFApiCommon.PARAM_SK, sessionKey);

        String response = NetworkRequest.newPostRequestInstance(
                LFApiCommon.API_ROOT_URL,
                requestParams.generateRequestParamsAsStringWithSign()
        ).getResponse();

        return LFScrobbleResponseModel.parseFromJSON(response);
    }

    public static LFUpdateNowPlayingResponseModel updateNowPlaying(String sessionKey, String apiKey, String secret, LFTrackRequestModel track)
            throws LFApiException, NetworkException {

        LFRequestParamContainer requestParams = new LFRequestParamContainer("track.updateNowPlaying", secret);

        requestParams.addParam("artist", track.getArtist());
        requestParams.addParam("track", track.getTrack());

        if (!StringUtil.isNullOrEmpty(track.getAlbum())) {
            requestParams.addParam("album", track.getAlbum());
        }

        if (track.getTrackNumber() != null) {
            requestParams.addParam("trackNumber", track.getTrackNumber().toString());
        }

        if (track.getDuration() != null) {
            requestParams.addParam("duration", track.getDuration().toString());
        }

        requestParams.addParam(LFApiCommon.PARAM_API_KEY, apiKey);
        requestParams.addParam(LFApiCommon.PARAM_SK, sessionKey);

        String response = NetworkRequest.newPostRequestInstance(
                LFApiCommon.API_ROOT_URL,
                requestParams.generateRequestParamsAsStringWithSign()
        ).getResponse();
        return LFUpdateNowPlayingResponseModel.parseFromJSON(response);
    }

    public static LFLoveTrackResponseModel love(String sessionKey, String apiKey, String secret, LFTrackRequestModel track)
            throws NetworkException, LFApiException {
        LFRequestParamContainer requestParams = new LFRequestParamContainer("track.love", secret);

        requestParams.addParam("artist", track.getArtist());
        requestParams.addParam("track", track.getTrack());

        requestParams.addParam(LFApiCommon.PARAM_API_KEY, apiKey);
        requestParams.addParam(LFApiCommon.PARAM_SK, sessionKey);

        String response = NetworkRequest.newPostRequestInstance(
                LFApiCommon.API_ROOT_URL,
                requestParams.generateRequestParamsAsStringWithSign()
        ).getResponse();
        return LFLoveTrackResponseModel.parseFromJSON(response);
    }
}
