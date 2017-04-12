package com.aiohtea.aiohteacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nguyen Truong on 4/12/2017.
 */

public class SwitchSetup extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_setup);
    }

    public void setupSwitchOnClick(View newSwitchView) throws ExecutionException, InterruptedException {

        // AP_ssid&AP_wiFiPass&MQTT_server&MQTT_port&MQTT_ClientID&MQTT_user&MQTT_pass&
        String setupMsg = new String();

        EditText editBox = (EditText) findViewById(R.id.wifi_ssid_box);
        setupMsg = editBox.getText().toString() + "&";

        editBox = (EditText) findViewById(R.id.wifi_password_box);
        setupMsg += editBox.getText().toString() + "&m10.cloudmqtt.com&14110&";

        editBox = (EditText) findViewById(R.id.switch_id_box);
        setupMsg += editBox.getText().toString() + "&nywjllog&DXwwL_1Bye8x&";

        Log.d("SWITCH_SETUP", setupMsg);

        SwitchSetupThread swst = new SwitchSetupThread ();
        swst.execute(setupMsg);

        Integer result = swst.get();

        Log.d("SWITCH_SETUP THD RESULT", result.toString());

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

    private class SwitchSetupThread extends AsyncTask<String, Void, Integer> {

        // --------------------------------------------------------------------------------------------
        // Upload setup data to Switch via ip: 192.168.4.1 / port:1109
        // --------------------------------------------------------------------------------------------
        //int uploadSetup(String setupMsg) {
        @Override
        protected Integer doInBackground(String ...params){
            final String ip = "192.168.4.1";
            final int port = 1109;

            //final String ip = "192.168.2.11";
            //final int port = 8080;

            String setupMsg = params[0];

            Socket socket = null;
            try {
                // Make socket to server
                socket = new Socket(ip, port);
                //serverSocket.connect(new InetSocketAddress(ip, port), 10000);
                //Log.d("SWITCH_SETUP", "Connected!");

                // Send data to server
                socket.getOutputStream().write(setupMsg.getBytes());

                Log.d("SWITCH_SETUP", "Sent: " + setupMsg);

                // Set timeout to wait for reading "OK" from Switch
                socket.setSoTimeout(5000);

                byte[] buffer = new byte[8];

                int bytesRead=0;
                InputStream inputStream = socket.getInputStream();

                Log.d("SWITCH_SETUP", "Start reading...");
                // Reading data from server
                while (((bytesRead = inputStream.read(buffer)) != -1) && (bytesRead < 2)) {
                }

                Log.d("SWITCH_SETUP RESULT", new String(buffer));

            } catch (UnknownHostException e) {
                e.printStackTrace();
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 2;
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 3;
                    }
                }
            }

            return 0; // Successfully setup
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }
}
