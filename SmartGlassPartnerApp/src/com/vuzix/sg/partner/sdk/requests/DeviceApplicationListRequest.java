package com.vuzix.sg.partner.sdk.requests;

import com.vuzix.sg.common.Communication;
import com.vuzix.sg.partner.sdk.ApiRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceApplicationListRequest extends ApiRequest<DeviceApplicationListResponse>
{
    public DeviceApplicationListRequest()
    {
        this.param2 = Communication.MSG_GET_APPLICATIONS;
    }

    @Override
    public DeviceApplicationListResponse createResponse(int statusCode) {
        return new DeviceApplicationListResponse(statusCode);
    }
}