package com.vuzix.sg.partner.sdk.requests;

import com.vuzix.sg.partner.sdk.ApiRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageRequest extends ApiRequest<MessageResponse>
{
    public MessageRequest()
    {
        this.param0 = "com.msg.generic";
    }

    public MessageRequest(String param0)
    {
        this.param2 = param0;
    }

    public MessageRequest(String param0, String param1)
    {
        this.param0 = param0;
        this.param1 = param1;
    }

    @Override
    public MessageResponse createResponse(int statusCode) {
        return new MessageResponse(statusCode);
    }
}
