package com.vuzix.sg.partner.sdk.requests;

import com.vuzix.sg.partner.sdk.ApiResponse;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageResponse extends ApiResponse
{
    private String responseMessage;

    public MessageResponse(int statusCode)
    {
        super();
        this.responseCode = statusCode;
    }

    @Override
    public void parseResponse(String response) {
        this.responseMessage = response;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}

