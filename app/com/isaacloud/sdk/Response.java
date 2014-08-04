package com.isaacloud.sdk;

/**
 * Base class for responses.
 */
public abstract class Response {

    /**
     * Checks whether the response contains an array.
     * @return true if is array.
     */
	public abstract boolean isArray();

    /**
     * Checks whether the response contains an object.
     * @return true if is an object.
     */
	public abstract boolean isObject();

    /**
     * Gets json array or object depending on the class.
     * @return json array if isArray, json object if isObject
     */
	public abstract Object getJson();
}
