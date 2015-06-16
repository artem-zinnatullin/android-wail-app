package com.artemzin.android.wail.api.lastfm;

public class LFApiException extends Exception {

    public static final String ERROR_NOT_AUTHORIZED_TOKEN = "14";
    public static final String ERROR_TOKEN_EXPIRED = "15";
    public static final String ERROR_SERVICE_OFFLINE = "11";
    public static final String ERROR_INVALID_SESSION_KEY = "9";

    private String error, message;

    protected LFApiException() {}

    public static LFApiException newIntance(String error, String message) {
        final LFApiException exception = new LFApiException();

        exception.error   = error;
        exception.message = message;

        return exception;
    }

    public static LFApiException newDataFormatErrorInstance(String error, String message) {
        final LFApiException exception = new LFApiException();

        exception.error             = error;
        exception.message           = message;

        return exception;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }


}
