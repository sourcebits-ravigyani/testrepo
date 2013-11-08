package com.vuzix.sg.partner.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ApiResponse
{
    public final static int SUCCESS = 0;
    public final static int TIMEOUT = -1;
    public final static int UNAUTHORIZED = -100;
    public final static int PARSING_ERROR = -101;

    protected int responseCode;

    public abstract void parseResponse(String response);

    public int getResponseCode()
    {
        return responseCode;
    }

    public boolean isSuccess()
    {
        return responseCode == SUCCESS;
    }

    public boolean isUnauthorized() {
        return responseCode == UNAUTHORIZED;
    }
}
