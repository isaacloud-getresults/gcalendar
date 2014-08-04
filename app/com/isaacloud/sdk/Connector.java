package com.isaacloud.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Joiner;

/**
 * Abstract class for connecting to different api with oauth authentication.
 */
abstract class Connector {

    /**
     * Url to api.
     */
    protected String baseUrl;

    /**
     * Api version
     */
    protected String version;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * Url to oauth
     */
    protected String oauthUrl;

    /**
     * Client id for the api
     */
    protected String clientId = null;

    /**
     * Secret connected with the id.
     */
    protected String clientSecret = null;

    /**
     * Current expiry time for token
     */
    protected long currentTokenTime = new Date().getTime() - 1;

    /**
     * Current token
     */
    protected String currentToken = "";

    /**
     * Client variable used to execute http requests.
     */
    protected CloseableHttpClient client = null;

    /**
     * Getter for client.
     *
     * @return current client
     */
    public CloseableHttpClient getHttpClient() {
        return client;
    }

    /**
     * Get current expiry date.
     *
     * @return date as long
     */
    public long getCurrentTokenTime() {
        return currentTokenTime;
    }

    /**
     * Get current authentication token.
     *
     * @return token as String
     */
    public String getCurrentToken() {
        return currentToken;
    }

    /**
     * Get version of the api.
     *
     * @return version as String
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the base url to the api.
     *
     * @return url as String
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Get the oauth url.
     *
     * @return url as String
     */
    public String getOauthUrl() {
        return oauthUrl;
    }

    /**
     * Basic constructor, creates apache HTTPClient.
     *
     * @param baseUrl  base com.isaacloud api url
     * @param oauthUrl com.isaacloud oauth url
     * @param version  com.isaacloud api version
     * @param config   configuration with client id and version
     */
    public Connector(String baseUrl, String oauthUrl, String version,
                     Map<String, String> config) {

        this.baseUrl = baseUrl + "/" + version;

        this.oauthUrl = oauthUrl;

        this.version = version;

        if (config.containsKey("clientId"))
            this.clientId = config.get("clientId");

        if (config.containsKey("secret"))
            this.clientSecret = config.get("secret");

        try {
            setupSSL();
        } catch (KeyManagementException | NoSuchAlgorithmException
                | KeyStoreException | IOException e) {
            System.out.println("Cannot initialize SSL connection " + e.getMessage() + "\n");
        }

    }

    /**
     * Used to setup the ssl context. Does not throw exceptions.
     *
     * @throws IOException
     * @throws UnknownHostException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     */
    public CloseableHttpClient setupSSL()
            throws KeyManagementException, NoSuchAlgorithmException, IOException,
            KeyStoreException {

        // trust all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        // allow TLSv1 protocol only
        SSLConnectionSocketFactory sslConnection = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        client = HttpClients.custom().setSSLSocketFactory(sslConnection)
                .build();

        return client;

    }

    /**
     * Creates a HttpPost object to be sent to oauth.
     *
     * @return HTTPPost for getting Oauth token.
     */
    protected HttpPost getAuthenticationPost() {

        HttpPost method = new HttpPost(this.oauthUrl + "/token");

        method.addHeader(
                "Authorization",
                "Basic "
                        + new String(
                        Base64.encodeBase64((this.clientId + ":" + this.clientSecret)
                                .getBytes())
                )
        );

        List<NameValuePair> urlParameters = new ArrayList<>();

        urlParameters.add(new BasicNameValuePair("grant_type",
                "client_credentials"));
        try {
            method.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            // this will never throw any exceptions, because it has the same parameters each time
        }

        return method;
    }

    /**
     * Analyze response and get the token.
     *
     * @param response from call execution.
     * @return token as String
     * @throws IOException                  connection problems
     * @throws IsaacloudConnectionException response had an error code
     */
    protected String consumeResponse(CloseableHttpResponse response)
            throws IOException, IsaacloudConnectionException {

        StringBuilder result = new StringBuilder();

        HttpEntity entity1 = response.getEntity();

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                entity1.getContent()));

        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        EntityUtils.consume(entity1);
        response.close();

        int status = response.getStatusLine().getStatusCode();

        if (status != 200) {
            JSONObject error = (JSONObject) JSONValue.parse(result.toString());

            if (status == 401)
                throw new UnauthorizedException(error);
            else if (status == 403)
                throw new ForbiddenException(error);
            else
                throw new IsaacloudConnectionException(error);
        }

        JSONObject obj = (JSONObject) JSONValue.parse(result.toString());

        return obj.get("access_token").toString();
    }

    /**
     * Get the token, if it's outdated then retrieve it from the server.
     *
     * @return token value
     * @throws IOException                  connection problems
     * @throws IsaacloudConnectionException response had an error code
     */
    protected String getAuthentication(boolean refresh) throws IOException,
            IsaacloudConnectionException {

        // check current time
        long currentTime = System.currentTimeMillis();

        Lock lock;

        if (currentTime > this.currentTokenTime || refresh) {
            lock = readWriteLock.writeLock();
        } else {
            lock = readWriteLock.readLock();
        }

        lock.lock();

        if (currentTime > this.currentTokenTime) {

            if (this.clientSecret == null)
                throw new IllegalArgumentException("Client secret not set.");
            else if (this.clientId == null)
                throw new IllegalArgumentException("Client id not set.");

            CloseableHttpResponse response = client
                    .execute(getAuthenticationPost());

            this.currentToken = consumeResponse(response);

            this.currentTokenTime = currentTime + 3600 * 1000;
        }
        String ret = "Bearer " + currentToken;
        lock.unlock();
        return ret;
    }

    /**
     * Make a request and write to the string. *
     *
     * @param method method with parameters
     * @return response object as String
     * @throws IOException                  connection problems
     * @throws IsaacloudConnectionException response had an error code
     */
    protected Response makeRequest(HttpUriRequest method, int ttl) throws IOException,
            IsaacloudConnectionException {

        StringBuilder result = new StringBuilder();

        CloseableHttpResponse response = client.execute(method);

        HttpEntity entity1 = response.getEntity();

        int status = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                entity1.getContent()));

        String line;

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        EntityUtils.consume(entity1);

        Header[] paginator = response.getHeaders("Collection-Paginator");

        response.close();

        if (status != 200 && status != 201) {

            JSONObject error;

            try {
                error = (JSONObject) JSONValue.parse(result.toString());
            } catch(Exception e){
                error = new JSONObject();
                error.put("message",result.toString());
            }

            if (status == 404)
                throw new NotFoundException(error);
            else if (status == 500)
                throw new InternalServerException(error);
            else if (status == 400)
                throw new BadRequestException(error);
            else if (status == 401)
                if (error.containsKey("message") && error.get("message") == "expired_token" && ttl < 1) {
                    method.setHeader("Authorization", getAuthentication(true));
                    makeRequest(method, 1);
                } else
                    throw new UnauthorizedException(error);
            else if (status == 403) {
                error.put("remark",
                        "Maybe your certificate is expired, in that case use setupSSL method.");
                throw new ForbiddenException(error);
            } else if (status == 402)
                throw new PaymentRequiredException(error);
            else
                throw new IsaacloudConnectionException(
                        (JSONObject) JSONValue.parse(result.toString()));
        }

        if (paginator.length > 0) {

            JSONObject paginatorObject = (JSONObject) JSONValue
                    .parse(paginator[0].getValue());
            JSONArray obj = (JSONArray) JSONValue.parse(result.toString());
            return new ListResponse(obj,
                    Integer.parseInt(paginatorObject.get("total").toString()),
                    Integer.parseInt(paginatorObject.get("limit").toString()),
                    Integer.parseInt(paginatorObject.get("offset").toString()),
                    Integer.parseInt(paginatorObject.get("page").toString()),
                    Integer.parseInt(paginatorObject.get("pages").toString()));
        } else {

            JSONObject obj;

            if (result.toString().equals(""))
                obj = new JSONObject();
            else
                obj = (JSONObject) JSONValue.parse(result.toString());

            return new SimpleResponse(obj);
        }

    }

    /**
     * Prepares the url with parameters.
     *
     * @param wholeUri   path to be enhanced
     * @param parameters parameters to be added
     * @return new path
     */
    protected String prepareUrl(String wholeUri, Map<String, Object> parameters) {
        String regex = "\\{[a-zA-Z0-9,]+\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(wholeUri);

        while (matcher.find()) {

            String id = matcher.group();
            String tmp = id.replace("{", "");
            tmp = tmp.replace("}", "");

            if (parameters.containsKey(tmp)) {
                String rep = parameters.get(tmp).toString();
                wholeUri = wholeUri.replace(id, rep);
                parameters.remove(tmp);
            }

        }

        String and = "?";

        for (Entry<String, Object> entry : parameters.entrySet()) {

            if (entry.getValue() != null) {

                wholeUri = wholeUri + and + entry.getKey() + "=";
                Object vals = entry.getValue();

                if (List.class.isAssignableFrom(vals.getClass())) {
                    wholeUri = wholeUri + Joiner.on(",").join((List) vals);
                } else if (Map.class.isAssignableFrom(vals.getClass())) {
                    wholeUri = wholeUri
                            + Joiner.on(",").withKeyValueSeparator(":")
                            .join((Map) vals);
                } else {
                    wholeUri = wholeUri + vals;
                }
                and = "&";
            }

        }

        return wholeUri;
    }

    /**
     * Constructs a request and send it.
     *
     * @param methodName method type as lowercase (get,post,patch,put,delete)
     * @param uri        path of the resource
     * @param parameters additional parameters
     * @return response as String
     * @throws IOException                  connection problem
     * @throws IsaacloudConnectionException response had an error code
     */
    public Response callService(String uri, String methodName,
                                Map<String, Object> parameters, JSONObject body)
            throws IsaacloudConnectionException, IOException {

        String wholeUri = prepareUrl(this.baseUrl + uri, parameters);

        HttpUriRequest method;

        switch (methodName) {
            case "get":
                method = new HttpGet(wholeUri);
                break;
            case "delete":
                method = new HttpDelete(wholeUri);
                break;
            case "put":
                method = new HttpPut(wholeUri);
                break;
            case "post":
                method = new HttpPost(wholeUri);
                break;
            case "patch":
                method = new HttpPatch(wholeUri);
                break;
            default:
                throw new UnsupportedOperationException(methodName
                        + " is not supported.");
        }

        method.addHeader("Authorization", this.getAuthentication(false));


        if (body != null) {
            method.addHeader("Content-Type", "application/json charset=utf-8");

            ((HttpEntityEnclosingRequestBase) method)
                    .setEntity(new StringEntity(body.toJSONString(),
                            ContentType.APPLICATION_JSON));
        }

        return this.makeRequest(method, 0);
    }

}
