package com.vuzix.sg.partner.sdk.requests;

import com.vuzix.sg.partner.sdk.ApiRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceListRequest extends ApiRequest<DeviceListResponse>
{
    public DeviceListRequest()
    {
        this.param2 = "msg.get.devices";
    }

    @Override
    public DeviceListResponse createResponse(int statusCode)
    {
        return new DeviceListResponse(statusCode);
    }
}

