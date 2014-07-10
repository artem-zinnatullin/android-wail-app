package com.artemzin.android.wail.api.lastfm;

import com.artemzin.android.wail.api.MD5Hash;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LFRequestParamContainer {

    private final Map<String, String> paramsMap = new TreeMap<String, String>();

    private final String secret;

    public LFRequestParamContainer(String methodName, String secret) {
        this.secret = secret;
        addParam("method", methodName);
    }

    public LFRequestParamContainer addParam(String name, String value) {
        paramsMap.put(name.trim(), value);
        return this;
    }

    private String getParamsAsGETParamsString() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();

        final Set<Map.Entry<String, String>> entrySet = paramsMap.entrySet();

        int i = 0;
        final int paramsCount = entrySet.size();

        for (Map.Entry<String, String> param : entrySet) {
            i++;

            stringBuilder
                    .append(param.getKey())
                    .append("=")
                    .append(URLEncoder.encode(param.getValue(), "UTF-8"));

            if (i != paramsCount) {
                stringBuilder.append("&");
            }

            // Very very small chance, that UTF-8 is not supported by Android device
            // And that UnsupportedEncodingException will be thrown
        }

        return stringBuilder.toString();
    }

    private String calculateRequestSign() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> param : paramsMap.entrySet()) {
            stringBuilder
                    .append(param.getKey())
                    .append(param.getValue());
        }

        stringBuilder.append(secret);

        return MD5Hash.calculateMD5(stringBuilder.toString());
    }

    private String getParamsAsGETParamsStringWithSign() throws Exception {
        final String sig = calculateRequestSign();
        addParam("format", "json");

        final StringBuilder stringBuilder = new StringBuilder(getParamsAsGETParamsString());
        stringBuilder
                .append("&")
                .append(LFApiCommon.PARAM_API_SIG)
                .append("=")
                .append(sig);

        return stringBuilder.toString();
    }

    public String generateUrlForRequestWithSign() {
        return LFApiCommon.API_ROOT_URL + "?" + generateRequestParamsAsStringWithSign();
    }

    public String generateRequestParamsAsStringWithSign() {
        try {
            return getParamsAsGETParamsStringWithSign();
        } catch (Exception e) {
            return null;
        }
    }
}
