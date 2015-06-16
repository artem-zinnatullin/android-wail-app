package com.artemzin.android.wail.api.lastfm;

public class LFApiCommon {

    public static final String API_ROOT_URL = "https://ws.audioscrobbler.com/2.0/";

    public static final String PARAM_API_KEY = "api_key";
    public static final String PARAM_API_SIG = "api_sig";
    public static final String PARAM_SK = "sk";

    public static final String AUTHORIZATION_URL = "http://www.last.fm/api/auth/?api_key=%s&token=%s";

    private LFApiCommon() {}
}
