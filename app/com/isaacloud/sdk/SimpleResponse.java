package com.isaacloud.sdk;

import net.minidev.json.JSONObject;

/**
 * Represents single results.
 */
public class SimpleResponse extends Response {

    /**
     * Object body.
     */
	private JSONObject body;

    /**
     * Simple constructor
     * @param _body object returned for api.
     */
	public SimpleResponse(JSONObject _body) {
		body = _body;
	}

    /**
     * Checks whether the response contains an array.
     * @return false
     */
	@Override
	public boolean isArray() {
		return false;
	}

    /**
     * Checks whether the response contains an object.
     * @return true
     */
	@Override
	public boolean isObject() {
		return true;
	}

    /**
     * Gets json array or object depending on the class.
     * @return json object
     */
	@Override
	public Object getJson() {
		return body;
	}

    /**
     * String representation of the object.
     * @return object as String
     */
	@Override 
	public String toString(){
		return body.toJSONString();
	}
	
}
