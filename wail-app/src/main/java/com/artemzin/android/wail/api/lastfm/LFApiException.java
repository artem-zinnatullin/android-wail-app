package com.artemzin.android.wail.api.lastfm;

public class LFApiException extends Exception {

    private boolean isDataFormatError;
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

        exception.isDataFormatError = true;
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
