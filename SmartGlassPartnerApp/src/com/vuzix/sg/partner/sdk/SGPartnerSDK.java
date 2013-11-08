package com.vuzix.sg.partner.sdk;


import android.content.Context;
import android.util.Log;
import com.vuzix.sg.common.*;
import com.vuzix.sg.partner.sdk.requests.*;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: splisson
 * Date: 10/30/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SGPartnerSDK
{
    private static final int ALIVE_STATUS_TIMEOUT = 2000;
    private Communication communication;
    private ExecutorService executor;
    private ScheduledExecutorService scheduleExecuter;
    private static Integer foo = new Integer(1);

    private boolean isSGDeviceAlive;

    /**
     * Constructor for SGPartnerSDK, this takes a Context and intiailizes a Communication layer
     *
     * @return true if the smart glasses has returned a keep alive message recently enough
     */
    public SGPartnerSDK(Context context)
    {
        this.communication = new BluetoothCommunication(context);

        executor = Executors.newFixedThreadPool(10);
        isSGDeviceAlive = false;
    }

    /**
     * Change the communication type using this using this method,
     * Will be exposed in later stages
     *
     * param : will be an enum of type of Communication, Bluetooth, USB etc
     */
    private void setCommunicationType()
    {
        close();

        //Reinitialize?
        executor = Executors.newFixedThreadPool(10);
        scheduleExecuter = Executors.newScheduledThreadPool(10);

        //Set Communication instance, the IMessageHandler will be assigned in each RemoteCallTask, trying to make it a Reqeust-Response Unit
        //this.communication = communication;
    }

    private void dispatchCallbacks(ApiResponse apiResponse, ApiResponseCallbacks responseCallbacks)
    {
        if (apiResponse.isSuccess())
        {
            try
            {
                responseCallbacks.onSuccess(apiResponse);
            }
            catch (Exception e)
            {
                Log.e(SGPartnerSDK.class.getName(), "Error proccessing response", e);
                responseCallbacks.onFailure(apiResponse.getResponseCode());
            }
        }
        else
        {
            responseCallbacks.onFailure(apiResponse.getResponseCode());
        }

    }


    private void executeRemoteCall(ApiRequest request, ApiResponseCallbacks callbacks)
    {
        try
        {
            new Thread(new RemoteCallTask(request, callbacks)).start();

//            ExecutorService executor = Executors.newFixedThreadPool(10);
//            Future<ApiResponse> future = executor.submit(new RemoteCallTask(request, callbacks));
//            dispatchCallbacks(future.get(), callbacks);
//            executor.shutdown();
        }
        catch (Exception ex)
        {
            callbacks.onFailure(ApiResponse.UNAUTHORIZED);
            //dispatchCallbacks( request.createResponse(ApiResponse.TIMEOUT), callbacks);
        }
    }

    /**
     * Seb, why is this public?
     *
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Returns the status of the smart glasses connected, if any
     *
     * Seb, is this not better to name it as isActive, as it returns a boolean ( or is this an enum)
     * Also, dont understand why we can not depend on the bluetooth/usb communication layer's status to determine the status of the device
     *
     * Also see restartStatusRunnable
     * @return true if the smart glasses has returned a keep alive message recently enough
     */
    public boolean getStatus()
    {
        if(communication == null)
            return false;

       return isSGDeviceAlive;
    }

    /**
     * Connects to a device given its address.
     * @param deviceAddress
     */
    public void connectDevice(String deviceAddress)
    {
        communication.connectDevice(deviceAddress);

    }

    /**
     * Starts the communication service
     */
    public void start()
    {
        communication.start();

        //Keep polling the service for status
        isSGDeviceAlive = true;
        restartStatusRunnable();
    }

    /**
     * Disconnect the current connection
     */
    public void close()
    {
        if(this.communication != null)
            this.communication.stop();

        //Attempts to stop all actively executing tasks, halts the processing of waiting tasks
        executor.shutdownNow();
        scheduleExecuter.shutdownNow();
    }

    /**
     * Send  a message to the current connected device
     * @param message
     * @param responseCallbacks
     */
    public void sendMessage(String applicationIdentifier, String message, final ApiResponseCallbacks<MessageResponse> responseCallbacks)
    {
        executeRemoteCall(new MessageRequest(), responseCallbacks);
    }

    /**
     * Fetch Device time from the connected device
     * @param responseCallbacks
     */
    public void getDeviceTime(final ApiResponseCallbacks<MessageResponse> responseCallbacks)
    {
        //Can use generic messages like this also for sending
        //Would prefer to use Encapsulated Message responses to parse the response however
        executeRemoteCall(new MessageRequest(Communication.MSG_GET_TIME), responseCallbacks);
    }


    /**
     * Fetch the list of application from the connected SG device
     *
     * Seb: This is public on SGPartner not here       Ravi:WHY?
     *
     * @param allApplications
     * @param responseCallbacks
     */
    public void getApplications( boolean allApplications, final ApiResponseCallbacks<DeviceApplicationListResponse> responseCallbacks)
    {
        executeRemoteCall(new DeviceApplicationListRequest(), responseCallbacks);
    }


    /**
     * This will schedule a task within the SDK to periodically ping the device for status
     *
     * Two questions:
     * 1. Why can we not use the bluetooth/USB/wireless Communication layers directly.
     * 2. Is this not the responsibility of the Running Activity to check the status using a scheduled runnable, should the API not be exposing a method, which checks for Active and returns fresh response
     */
    private void restartStatusRunnable()
    {
        scheduleExecuter.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                executeRemoteCall(new MessageRequest(Communication.MSG_SG_DEVICE_ALIVE), new ApiResponseCallbacks<MessageResponse>() {
                    @Override
                    public void onStart() {}

                    @Override
                    public void onSuccess(MessageResponse response)
                    {
                        isSGDeviceAlive = true;
                    }

                    @Override
                    public void onFailure(int errorCode)
                    {
                        isSGDeviceAlive = false;
                    }
                });
            }
        }, ALIVE_STATUS_TIMEOUT, ALIVE_STATUS_TIMEOUT, TimeUnit.SECONDS);
    }


    public void resetStopwatch(ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_RESET_STOPWATCH), callbacks);
    }


    public void startStopwatch(ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_START_STOPWATCH), callbacks);
    }

    public void pauseStopwatch(ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_PAUSE_STOPWATCH), callbacks);
    }

    public void setTimer(int duration, ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_SET_TIMER, String.valueOf(duration)), callbacks);
    }

    public void startTimer(ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_START_TIMER), callbacks);
    }

    public void pauseTimer(ApiResponseCallbacks<MessageResponse> callbacks)
    {
        executeRemoteCall(new MessageRequest(Communication.MSG_PAUSE_TIMER), callbacks);
    }

    /**
     * Get the list of smart glasses devices.
     *
     * This I think is wrong, there is no phone-glass communication needed for this, This list is to be returned from the Bluetooth/USB/Wireless layer
     * @param callbacks these handlers witll be called back with the list of devices detected
     */
    public void getDevices(ApiResponseCallbacks<MessageResponse> callbacks) {
        // TODO: implement
    }

    /**
     * This class encapsulates the Request - Response mechanism of communcation.
     * In case of bluetooth, this is implemented as a Chat, the client (this) sends a request, the server receives it, and replies back with the data
     * The IMessageHandler is implemented by this class so as to encapsulate parsing of the response
     */


    private class RemoteCallTask implements Runnable, IMessageHandler
    {
        private String responseString;
        final CountDownLatch latch = new CountDownLatch(1);

        private final ApiRequest request;
        private final ApiResponseCallbacks responseCallbacks;


        public RemoteCallTask(ApiRequest request, ApiResponseCallbacks responseCallbacks)
        {
            this.request = request;
            this.responseCallbacks = responseCallbacks;

            this.responseCallbacks.onStart();

            communication.setMessageHandler(this);
        }

        @Override
        public void run()
        {
            if(!communication.sendMessage(request.param0, request.param1, request.param2))
            {
                this.responseCallbacks.onFailure(ApiResponse.UNAUTHORIZED);
                return;
            }

            try
            {

                latch.await();

                ApiResponse response = this.request.createResponse(ApiResponse.SUCCESS);
                response.parseResponse(responseString);

                if(response.isSuccess())
                    this.responseCallbacks.onSuccess(response);
                else
                    this.responseCallbacks.onFailure(ApiResponse.PARSING_ERROR);
            }
            catch (InterruptedException e)
            {
                this.responseCallbacks.onFailure(ApiResponse.TIMEOUT);
            }
        }

        @Override
        public void onReceivedMessage(String destinationClientId, String destinationApplicationId, String message)
        {
            this.responseString  = message;

            latch.countDown();

            communication.setMessageHandler(null);
        }

    }


}
