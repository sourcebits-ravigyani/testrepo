package com.vuzix.sg.partner.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ApiRequest<T extends ApiResponse>
{
    //These will be fleshed out later
    public String param0;
    public String param1;
    public String param2;
    public String param3;

    public abstract T createResponse(int statusCode);
}
