package com.vuzix.sg.partner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.vuzix.sg.partner.sdk.ApiResponseCallbacks;
import com.vuzix.sg.partner.sdk.SGPartnerSDK;
import com.vuzix.sg.partner.sdk.requests.DeviceApplicationListResponse;
import com.vuzix.sg.partner.sdk.requests.MessageResponse;

/**
 * This is the main Activity that displays the current chat session.
 */
public class HomeActivity extends Activity
{

    // Local Bluetooth adapter
    private BluetoothAdapter bluetoothAdapter = null;

    // Member object for the chat services
    private SGPartnerSDK sdkService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        setContentView(R.layout.activity_home);

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, R.id.menu_discoverable);
            // Otherwise, setup the chat session
        }
        else
        {
            if (sdkService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume()
    {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (sdkService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (sdkService.getStatus())
            {
                sdkService.start();
            }
        }
    }

    private void setupChat()
    {
        // Initialize the BluetoothChatService to perform bluetooth connections
        sdkService = new SGPartnerSDK(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (sdkService != null)
            sdkService.close();
    }

    private void ensureDiscoverable()
    {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onListApplications(View view)
    {
        sdkService.getApplications(true, new ApiResponseCallbacks<DeviceApplicationListResponse>() {
            @Override
            public void onStart() {
                //To change body of implemented methods use File | Settings | File Templates.
                Toast.makeText(HomeActivity.this,"Started",Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(DeviceApplicationListResponse response) {
                //To change body of implemented methods use File | Settings | File Templates.
                Toast.makeText(HomeActivity.this,"Response Size - "+response.getItems().size(),Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(int errorCode) {
                //To change body of implemented methods use File | Settings | File Templates.
                Toast.makeText(HomeActivity.this,"Error - "+errorCode,Toast.LENGTH_SHORT);
            }
        });
    }

    public void onDeviceTime(View view)
    {
        sdkService.getDeviceTime(new ApiResponseCallbacks<MessageResponse>() {
            @Override
            public synchronized void onStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //To change body of implemented methods use File | Settings | File Templates.
                        Toast.makeText(HomeActivity.this,"Started",Toast.LENGTH_SHORT);
                    }
                });

            }

            @Override
            public synchronized void onSuccess(MessageResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //To change body of implemented methods use File | Settings | File Templates.
                        Toast.makeText(HomeActivity.this,"Success" + response.getResponseMessage(),Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public synchronized  void onFailure(int errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //To change body of implemented methods use File | Settings | File Templates.
                        Toast.makeText(HomeActivity.this,"Started",Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case R.id.menu_secure_connect_scan:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case R.id.menu_insecure_connect_scan:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case R.id.menu_discoverable:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(HomeActivity.class.getName(), "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure)
    {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        sdkService.connectDevice(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.menu_secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, R.id.menu_secure_connect_scan);
                return true;
            case R.id.menu_insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, R.id.menu_insecure_connect_scan);
                return true;
            case R.id.menu_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

}
