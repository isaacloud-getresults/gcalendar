package com.isaacloud.sdk;

import net.minidev.json.JSONObject;

/**
 * Base class for exceptions.
 */
public class IsaacloudConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

    /**
     * Object containg the error.
     */
    protected JSONObject error;

    /**
     * Default constructor with empty object.
     */
	IsaacloudConnectionException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _error string error message
     */
	IsaacloudConnectionException(String _error) {
		error = new JSONObject();		
		error.put("message", _error);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	IsaacloudConnectionException(JSONObject _error) {
		error = _error;
	}

    /**
     * Get the error message.
     * @return error message
     */
	public String getMessage() {
		if (error != null)
			return error.get("message").toString();
		else
			return "";
	}

    /**
     * Get the entire error object.
     * @return error object
     */
	public JSONObject getJSON() {
		return error;
	}

    /**
     * Get internal code for the error.
     * @return integer code
     */
	public int getInternalCode(){
		if (error != null)
			return Integer.parseInt(error.get("code").toString());
		else
			return 0;
	}

    /**
     * Other error data.
     * @return object with additional error data
     */
	public JSONObject getData(){
		if (error != null && error.containsKey("data"))
			return (JSONObject) error.get("data");
		else
			return new JSONObject();
	}
}

/**
 * Represents the 400 http error.
 */
class BadRequestException extends IsaacloudConnectionException {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	BadRequestException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	BadRequestException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	BadRequestException(JSONObject _error) {
		error = _error;
	}
}

/**
 * Represents the 401 http error.
 */
class UnauthorizedException extends IsaacloudConnectionException {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	UnauthorizedException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	UnauthorizedException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	UnauthorizedException(JSONObject _error) {
		error = _error;
	}
}

/**
 * Represents the 500 http error.
 */
class InternalServerException extends IsaacloudConnectionException {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	InternalServerException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	InternalServerException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	InternalServerException(JSONObject _error) {
		error = _error;
	}
}

/**
 * Represents the 402 http error.
 */
class PaymentRequiredException extends IsaacloudConnectionException {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	PaymentRequiredException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	PaymentRequiredException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	PaymentRequiredException(JSONObject _error) {
		error = _error;
	}
}

/**
 * Represents the 403 http error.
 */
class ForbiddenException extends IsaacloudConnectionException {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	ForbiddenException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	ForbiddenException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	ForbiddenException(JSONObject _error) {
		error = _error;
	}
}

/**
 * Represents the 404 http error.
 */
class NotFoundException extends IsaacloudConnectionException {

	private static final long serialVersionUID = 1L;

    /**
     * Default constructor with empty object.
     */
	NotFoundException() {
		error = null;
	}

    /**
     * Constructor that puts a message into an object.
     * @param _message string error message
     */
	NotFoundException(String _message) {
		error = new JSONObject();
		error.put("message", _message);
	}

    /**
     * Takes an error object.
     * @param _error object representing the error
     */
	NotFoundException(JSONObject _error) {
		error = _error;
	}
}