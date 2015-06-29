package com.appdynamics.cloud.connectors.verizon.rest;

import com.appdynamics.cloud.connectors.verizon.types.HttpError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.singularity.ee.connectors.api.ConnectorException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class RestClient {

    private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

    private final Client client;
    private final String accessKey;
    private final String secretKey;
    private String cloudSpace;
    private Gson gson = new Gson();
    private JsonParser parser = new JsonParser();


    public RestClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        client.addFilter(new AuthFilter());
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setCloudSpace(String cloudSpace) {
        this.cloudSpace = cloudSpace;
    }

    private <T> T castResponse(ClientResponse response, Class<T> type) {

        String responseJson = response.getEntity(String.class);
        T respObject = gson.fromJson(responseJson, type);
        return respObject;
    }

    private <T> T castResponse(ClientResponse response, Type type) {

        String responseJson = response.getEntity(String.class);
        JsonObject jsonObject = parser.parse(responseJson).getAsJsonObject();
        T respObject = gson.fromJson(jsonObject, type);
        return respObject;
    }

    public <T> T get(String uri, Class<T> type) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());
        ClientResponse clientResponse = service.get(ClientResponse.class);
        throwIfHTTPError(clientResponse);
        return castResponse(clientResponse, type);
    }

    public <T> T get(String uri, Type type) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());
        ClientResponse clientResponse = service.get(ClientResponse.class);
        throwIfHTTPError(clientResponse);
        return castResponse(clientResponse, type);
    }

    public <T> T post(String uri, String contentType, String acceptType, String jsonPostData, Class<T> type) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());

        WebResource.Builder requestBuilder = service.getRequestBuilder();
        if (contentType != null && contentType.length() > 0) {
            requestBuilder.type(contentType);
        }

        if (acceptType != null && acceptType.length() > 0) {
            requestBuilder.accept(acceptType);
        }

        ClientResponse clientResponse = requestBuilder.post(ClientResponse.class, jsonPostData);
        throwIfHTTPError(clientResponse);
        return castResponse(clientResponse, type);
    }

    public <T> T options(String uri, Class<T> type) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());
        ClientResponse clientResponse = service.options(ClientResponse.class);
        throwIfHTTPError(clientResponse);
        return castResponse(clientResponse, type);
    }

    public ClientResponse options(String uri) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());
        ClientResponse clientResponse = service.options(ClientResponse.class);
        throwIfHTTPError(clientResponse);
        return clientResponse;
    }

    public <T> T delete(String uri, Class<T> type) throws ConnectorException {

        WebResource service = client.resource(UriBuilder.fromUri(uri).build());
        ClientResponse clientResponse = service.delete(ClientResponse.class);
        throwIfHTTPError(clientResponse);
        return castResponse(clientResponse, type);
    }

    private void throwIfHTTPError(ClientResponse clientResponse) throws ConnectorException {
        int status = clientResponse.getStatus();
        if (status >= 200 && status < 300) {
            return;
        }
        HttpError httpError = castResponse(clientResponse, HttpError.class);
        throw new ConnectorException(httpError.getMessage());
    }

    private class AuthFilter extends ClientFilter {

        @Override
        public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
            MultivaluedMap headers = request.getHeaders();

            try {
                if(cloudSpace != null) {
                    headers.add("x-tmrk-cloudspace", cloudSpace);
                }

                String signature = sign(request, headers);
                headers.add("x-tmrk-authorization", new StringBuilder().append("CloudApi AccessKey=").append(getAccessKey()).append(" SignatureType=").append("HmacSHA256").append(" Signature=").append(signature).toString());

            } catch (Exception ex) {
                throw new ClientHandlerException("Error signing request headers", ex);
            }
            return getNext().handle(request);
        }
    }

    private String sign(ClientRequest request, MultivaluedMap<String, Object> headers) throws IllegalStateException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder stringToSign = new StringBuilder().append(getVerb(request)).append(getContentType(headers)).append(getDate(headers)).append(getCanonicalizedHeaders(headers)).append(getCanonicalizedResource(request));

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(getSecretKey().getBytes("UTF-8"), "HmacSHA256"));
        byte[] digest = mac.doFinal(stringToSign.toString().getBytes("UTF-8"));

        return new String(Base64.encode(digest)).trim();
    }

    private String getContentType(MultivaluedMap<String, Object> headers) {
        return getHeaderValue(headers, "Content-Type");
    }

    private String getVerb(ClientRequest request) {
        return new StringBuilder().append(request.getMethod().toUpperCase()).append('\n').toString();
    }

    private String getDate(MultivaluedMap<String, Object> headers) {
        if (!headers.containsKey("Date")) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());

            sdf.setTimeZone(this.GMT_TIME_ZONE);
            headers.putSingle("Date", sdf.format(new Date()));
        }

        return getHeaderValue(headers, "Date");
    }

    private static String getHeaderValue(MultivaluedMap<String, Object> headers, String headerName) {
        Object value = headers.getFirst(headerName);

        return value == null ? "\n" : new StringBuilder().append(value.toString()).append('\n').toString();
    }

    private static String getCanonicalizedHeaders(MultivaluedMap<String, Object> headers) {
        Map<String, Object> collected = new HashMap();
        for (Map.Entry header : headers.entrySet()) {
            String headerName = (String) header.getKey();

            if ((headerName.startsWith("x-tmrk-")) && (!"x-tmrk-authorization".equals(headerName))) {
                collected.put(headerName, ((List) header.getValue()).get(0));
            }
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry entry : collected.entrySet()) {
            builder.append((String) entry.getKey()).append(':').append(entry.getValue().toString()).append('\n');
        }

        if (builder.length() < 1) {
            builder.append('\n');
        }

        return builder.toString();
    }

    private static String getCanonicalizedResource(ClientRequest request) {
        Map<String, Object> queryMap = new TreeMap();
        String query = request.getURI().getQuery();

        if ((query != null) && (query.length() > 0)) {
            String[] parts = query.split("&");

            for (String part : parts) {
                String[] nameValue = part.split("=");
                if (nameValue.length == 2) {
                    queryMap.put(nameValue[0].toLowerCase(), nameValue[1]);
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(request.getURI().getPath().toLowerCase()).append('\n');
        for (Map.Entry entry : queryMap.entrySet()) {
            builder.append((String) entry.getKey()).append(':').append((String) entry.getValue()).append('\n');
        }
        return builder.toString();
    }
}