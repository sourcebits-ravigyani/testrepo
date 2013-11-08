package com.vuzix.sg.partner.sdk.requests;

import com.vuzix.sg.partner.sdk.ApiResponse;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceApplicationListResponse extends ApiResponse
{
    private Hashtable<String, String> items = new Hashtable<String, String>();

    public DeviceApplicationListResponse(int statusCode)
    {
        super();
        this.responseCode = statusCode;
    }

    @Override
    public void parseResponse(String response)
    {

    }

    public Hashtable<String, String> getItems() {
        return items;
    }
}
