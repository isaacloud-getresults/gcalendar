package com.isaacloud.sdk;

import net.minidev.json.JSONArray;

/**
 * Represent list responses from the api.
 */
public class ListResponse extends Response {

    /**
     * Total amount of elements that matched the query.
     */
	private long total;

    /**
     * Limit set in the paginator object.
     */
	private long limit;

    /**
     * Offset set in the paginator object.
     */
	private long offset;

    /**
     * Current page based on offset and limit.
     */
    private long page;

    /**
     * Total number of pages.
     */
    private long pages;

    /**
     * List of objects as an array.
     */
	private JSONArray body;

    /**
     * Simple contructor.
     * @param _body list of objects as an array
     * @param _total total amount of elements that matched the query
     * @param _limit limit set in the paginator object
     * @param _offset offset set in the paginator object
     * @param _page current page based on offset and limit
     * @param _pages total number of pages
     */
	public ListResponse(JSONArray _body,long _total, long _limit, long _offset, long _page, long _pages) {
		body = _body;
		total = _total;
		limit = _limit;
		offset = _offset;
        page = _page;
        pages = _pages;
	}

    /**
     * Getter for total.
     * @return total as long
     */
	public long getTotal() {
		return total;
	}

    /**
     * Getter for limit.
     * @return limit as long
     */
	public long getLimit() {
		return limit;
	}

    /**
     * Getter for offset.
     * @return offset as long
     */
	public long getOffset() {
		return offset;
	}

    /**
     * Getter for page.
     * @return page as long
     */
    public long getPage() {
        return page;
    }

    /**
     * Getter for pages.
     * @return pages as long
     */
    public long getPages() {
        return pages;
    }

    /**
     * Checks whether the response contains an array.
     * @return true
     */
	@Override
	public boolean isArray() {
		return true;
	}

    /**
     * Checks whether the response contains an object.
     * @return false
     */
	@Override
	public boolean isObject() {
		return false;
	}

    /**
     * Gets json array or object depending on the class.
     * @return json array
     */
	@Override
	public Object getJson() {
		return body;
	}

    /**
     * String representation of the array.
     * @return array as string
     */
	@Override 
	public String toString(){
		return body.toJSONString();
	}


}
