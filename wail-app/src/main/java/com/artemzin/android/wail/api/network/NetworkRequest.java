package com.artemzin.android.wail.api.network;

import com.artemzin.android.wail.api.Util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLException;

/**
 * Network request object is needed to send requests to our servers
 * It handles all http problems and work for retrying request if something goes wrong
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class NetworkRequest {

    /**
     * Default connection timeout in millis
     * 30000 millis == 30 seconds
     */
    public static final int CONNECTION_TIMEOUT_IN_MILLIS_DEFAULT = 30000;

    /**
     * By default gzip compression is enabled
     */
    public static final boolean IS_GZIP_COMPRESSION_ENABLED_DEFAULT = true;

    /**
     * By default we will retry query one time
     * NOTICE: this is RETRY count
     * so if you will set for example = 2, total queries count could be 3,
     * because first is basic, then we could repeat it 2 times
     */
    public static final int QUERY_RETRY_LIMIT_DEFAULT = 1;

    int connectionTimeoutInMillis = CONNECTION_TIMEOUT_IN_MILLIS_DEFAULT;
    boolean isGzipCompressionEnabled = IS_GZIP_COMPRESSION_ENABLED_DEFAULT;
    int queryRetryLimit = QUERY_RETRY_LIMIT_DEFAULT;

    private final URL url;
    private final Method method;
    private final String postBody;

    public enum Method {
        POST,
        GET
    }

    /**
     * Creating network request object and checks entered url
     * @param url where you want to send request
     * @param method request method
     * @throws java.net.MalformedURLException if url format is incorrect
     */
    private NetworkRequest(String url, Method method, String postBody) throws NetworkException {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new NetworkException("MalformedURLException: " + e.getMessage());
        }

        this.method = method;
        this.postBody = postBody;
    }

    public static NetworkRequest newGetRequestInstance(String url) throws NetworkException {
        return new NetworkRequest(url, Method.GET, null);
    }

    public static NetworkRequest newPostRequestInstance(String url, String postBody) throws NetworkException {
        return new NetworkRequest(url, Method.POST, postBody);
    }

    public int getConnectionTimeoutInMillis() {
        return connectionTimeoutInMillis;
    }

    public void setConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }

    public boolean isGzipCompressionEnabled() {
        return isGzipCompressionEnabled;
    }

    public void setGzipCompressionEnabled(boolean gzipCompressionEnabled) {
        isGzipCompressionEnabled = gzipCompressionEnabled;
    }

    public int getQueryRetryLimit() {
        return queryRetryLimit;
    }

    public void setQueryRetryLimit(int queryRetryLimit) {
        this.queryRetryLimit = queryRetryLimit;
    }

    public String getUrl() {
        return url.toExternalForm();
    }

    /**
     * Sends request to needed url and returns response as string
     * @return server response as string
     * @throws NetworkException if some of network problems caused
     */
    public String getResponse() throws NetworkException {
        String response = null;

        for (int i = 0; i < queryRetryLimit; i++) {
            try {
                response = getInternalResponse();
                break;
            } catch (SSLException e) {
                if (i < queryRetryLimit - 1)
                    continue;
                throw new NetworkException("SSL exception: " + e.getMessage());
            } catch (SocketException e) {
                if (i < queryRetryLimit - 1)
                    continue;
                throw new NetworkException("Socket exception: " + e.getMessage());
            } catch (Exception e) {
                if (i < queryRetryLimit - 1)
                    continue;
                throw new NetworkException("Network exception: " + e.getMessage());
            }
        }

        return response;
    }

    /**
     * Sends http request to needed url and returns result as string
     * @return response as string
     * @throws Exception if something going wrong
     */
    private String getInternalResponse() throws Exception {
        HttpURLConnection connection = null;

        try {
            // This is important, we could not use existing URL object because request could be repeated
            // So its state will be corrupted
            connection = (HttpURLConnection) new URL(url.toExternalForm()).openConnection();

            connection.setConnectTimeout(connectionTimeoutInMillis);
            connection.setReadTimeout(connectionTimeoutInMillis);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            if (isGzipCompressionEnabled)
                connection.setRequestProperty("Accept-Encoding", "gzip");

            if (method.equals(Method.GET)) {
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
            } else if (method.equals(Method.POST)) {
                connection.setRequestMethod("POST");

                if (postBody != null) {
                    connection.setDoOutput(true);

                    final OutputStream outputStream = connection.getOutputStream();
                    final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(postBody);
                    bufferedWriter.close();
                }
            }

            final int responseCode = connection.getResponseCode();
            if (responseCode == -1)
                throw new Exception("Got response code -1, may be http keep-alive problem");

            InputStream inputStream = new BufferedInputStream(connection.getInputStream(), 8192);

            final String contentEncoding = connection.getContentEncoding();

            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip"))
                inputStream = new GZIPInputStream(inputStream);

            return Util.convertStreamToString(inputStream);
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
