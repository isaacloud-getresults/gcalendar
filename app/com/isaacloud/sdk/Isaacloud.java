package com.isaacloud.sdk;

import java.io.IOException;
import java.util.*;

import net.minidev.json.JSONObject;

/**
 * Class with convenience methods for connecting with the api.
 */
public class Isaacloud extends Connector {

    /**
     * Class for building the request.
     */
    public class Caller {

        /**
         * Path to resource.
         */
        protected String path;

        /**
         * Parameters.
         */
        protected Map<String, Object> parameters = new HashMap<>();

        /**
         * Simple contructor.
         * @param _path path to resource.
         */
        public Caller(String _path) {
            this.path = _path;
        }

        /**
         * narrows the result set to contain only json fields, which are in the list of the method
         * @param fields list of field names
         * @return updated Caller object
         */
        public Caller withFields(String... fields) {
            parameters.put("fields", Arrays.asList(fields));
            return this;
        }

        /**
         * Declares exactly which fields in custom fields should be shown.
         * @param customs list of field names
         * @return updated Caller object
         */
        public Caller withCustoms(String... customs) {
            parameters.put("customs", Arrays.asList(customs));
            return this;
        }

        /**
         * Returns only the the resources with groups' ids in the list.
         * @param groups list of groups' ids
         * @return updated Caller object
         */
        public Caller withGroups(Long... groups) {
            parameters.put("groups", Arrays.asList(groups));
            return this;
        }

        /**
         * Returns only the the resources with ids' ids in the list.
         * @param ids list of ids
         * @return updated Caller object
         */
        public Caller withIds(Long... ids) {
            parameters.put("ids", Arrays.asList(ids));
            return this;
        }

        /**
         * Returns only the the resources with groups' ids in the list.
         * @param segments list of segments' ids
         * @return updated Caller object
         */
        public Caller withSegments(Long... segments) {
            parameters.put("segments", Arrays.asList(segments));
            return this;
        }

        /**
         * Limits the number and defines the offset for the results, works only with list resources.
         * @param limit limit of returned results
         * @param offset starting point for the returned results
         * @return updated Caller object
         */
        public Caller withPaginator(Long limit, Long offset) {
            parameters.put("limit", limit);
            parameters.put("offset", offset);
            return this;
        }

        /**
         * Declares the order in which results in list resources should be returned
         * @param order list of tuples in form of (fieldName, ASC or DESC)
         * @return updated Caller object
         */
        public Caller withOrder(SortedMap<String, String> order) {
            parameters.put("order", order);
            return this;
        }

        /**
         * Performs a search with concrete field values.
         * @param query list of tuples in form of (fieldName,fieldValue)
         * @return updated Caller object
         */
        public Caller withQuery(SortedMap<String, String> query) {
            parameters.put("query", query);
            return this;
        }

        /**
         * Returns only the resources created between certain dates given as milliseconds. In case one of the parameters is None, the limit is not set.
         * @param from starting date as millis
         * @param to end date as millis
         * @return updated Caller object
         */
        public Caller withCreatedAt(Long from, Long to) {
            if (from != null)
                parameters.put("fromc", from);
            if (to != null)
                parameters.put("toc", to);
            return this;
        }

        /**
         * Returns only the resources last updated between certain dates given as milliseconds. In case one of the parameters is None, the limit is not set.
         * @param from starting date as millis
         * @param to end date as millis
         * @return updated Caller object
         */
        public Caller withUpdatedAt(Long from, Long to) {
            if (from != null)
                parameters.put("fromu", from);
            if (to != null)
                parameters.put("tou", to);
            return this;
        }

        /**
         * Declares whether custom fields should be returned
         * @return updated Caller object
         */
        public Caller withCustom(){
            parameters.put("custom", true);
            return this;
        }

        /**
         * Add custom query parameters to request.
         * @param params map with queries
         * @return updated Caller object
         */
        public Caller withQueryParameters(Map<String, Object> params) {
            parameters.putAll(params);
            return this;
        }

        /**
         * Call to api get methods.
         *
         * @return api json response
         */
        public Response get() throws IOException, IsaacloudConnectionException {
            return callService(path, "get", parameters, null);
        }

        /**
         * Call to api put methods.
         *
         * @return api json response
         */
        public Response put(JSONObject body) throws IOException, IsaacloudConnectionException {
            return callService(path, "put", parameters, body);
        }

        /**
         * Call to api post methods.
         *
         * @return api json response
         */
        public Response post(JSONObject body) throws IOException, IsaacloudConnectionException {
            return callService(path, "post", parameters, body);
        }

        /**
         * Call to api delete methods.
         *
         * @return api json response
         */
        public Response delete() throws IOException, IsaacloudConnectionException {
            return callService(path, "delete", parameters, null);
        }

        /**
         * Call to api patch methods.
         *
         * @return api json response
         */
        public Response patch(JSONObject body) throws IOException, IsaacloudConnectionException {
            return callService(path, "patch", parameters, body);
        }
    }

    /**
     * Constructor for default com.isaacloud.
     *
     * @param config - map that contains clientID:clientSecret
     */
    public Isaacloud(Map<String, String> config) {
        super("https://api.isaacloud.com", "https://oauth.isaacloud.com", "v1",
                config);
    }

    /**
     * Constructor for default com.isaacloud.
     *
     * @param config - map that contains clientID:clientSecret
     */
    public Isaacloud(Map<String, String> config, String api, String oauth, String version) {
        super(api, oauth, version, config); }

    /**
     * Call to api methods.
     *
     * @param uri path to resource
     * @param method REST method type
     * @param parameters query parameters
     * @param body body json object
     * @return api json response
     * @throws IOException connection problem
     * @throws IsaacloudConnectionException response had an error code
     */
    @Deprecated
    public Response api(String uri, String method,
                        Map<String, Object> parameters, JSONObject body)
            throws IOException, IsaacloudConnectionException {
        return this.callService(uri, method, parameters, body);
    }

    /**
     * @param uri resource we want to access
     * @return object representing the resource
     */
    public Caller path(String uri) {
        return new Caller(uri);
    }

    /**
     * Push new event into Queue.
     *
     * @param subjectId subject Id, id of resource
     * @param subjectType  GROUP or USER
     * @param priority PRIORITY_LOW, PRIORITY_MEDIUM, PRIORITY_NORMAL, PRIORITY_HIGH, PRIORITY_CRITICAL, PRIORITY_BLOCKER
     * @param sourceId transaction source id
     * @param type NORMAL, GROUP, DEBUG, FORCED
     * @param body event body
     * @return response with the created event.
     * @throws IOException connection problem
     * @throws IsaacloudConnectionException response had an error code
     */
    public Response event(long subjectId, String subjectType, String priority,
                          long sourceId, String type, JSONObject body) throws IOException,
            IsaacloudConnectionException {

        // compose event
        JSONObject obj = new JSONObject();
        if (body != null)
            obj.put("body", body);
        if (priority != null)
            obj.put("priority", priority);

        obj.put("sourceId", sourceId);
        obj.put("subjectId", subjectId);

        if (subjectType != null)
            obj.put("subjectType", subjectType);

        if (type != null)
            obj.put("type", type);

        // Send event into queue
        return callService("/queues/events", "post",
                new HashMap<String, Object>(), obj);

    }

    /**
     * Get base api url.
     *
     * @return url to api
     */
    public String getApiUrl() {
        return this.baseUrl;
    }

    /**
     * Get base oauth url.
     *
     * @return oauth url
     */
    public String getOauthUrl() {
        return oauthUrl;
    }

    /**
     * Get base version.
     *
     * @return api version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get token.
     *
     * @return token as string
     * @throws IOException connection error
     * @throws IsaacloudConnectionException response had an error code
     */
    public String getToken() throws IOException, IsaacloudConnectionException {
        return getAuthentication(false);
    }

}
