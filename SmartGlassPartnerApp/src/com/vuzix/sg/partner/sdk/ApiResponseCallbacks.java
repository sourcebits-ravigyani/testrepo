package com.vuzix.sg.partner.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: ravigyani
 * Date: 31/10/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ApiResponseCallbacks<T extends ApiResponse>
{
    public void onStart();

    public void onSuccess(T response);

    public void onFailure(int errorCode);
}