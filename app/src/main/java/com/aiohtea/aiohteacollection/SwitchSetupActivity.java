package com.aiohtea.aiohteacollection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nguyen Truong on 4/12/2017.
 */

public class SwitchSetupActivity extends AppCompatActivity {

    private String m_swName;
    private String m_swPassword;
    private String m_swSelectedConnName;
    private Boolean m_addToList;
    private ArrayList<String> m_connNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_setup);

        Intent intent = getIntent();

        String connNameListString = intent.getStringExtra("SW_CONN_NAME_LIST");

        m_connNameList = MainActivity.parseNameListString(connNameListString);

        // Display default conn name
        ((TextView)findViewById(R.id.sw_conn_name)).setText(m_connNameList.get(0));

        // Double click on logo
        ((ImageView)findViewById(R.id.sw_logo_icon)).setOnClickListener(new MyDoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
            }

            @Override
            public void onDoubleClick(View v) {
                PopupMenu popupMenu = new PopupMenu(SwitchSetupActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.dev_gen_popup_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.clear();

                int size = m_connNameList.size();

                for(int i=0; i<size; i++) {
                    menu.add(m_connNameList.get(i));
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        m_swSelectedConnName = item.getTitle().toString();

                        ((TextView)findViewById(R.id.sw_conn_name)).setText(m_swSelectedConnName);

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void setupSwitchOnClick(View newSwitchView) throws ExecutionException, InterruptedException {

        // AP_ssid&AP_wiFiPass&MQTT_server&MQTT_port&MQTT_ClientID&MQTT_user&MQTT_pass&
        String setupMsg = new String();
        String str = new String();

        EditText editBox = (EditText) findViewById(R.id.switch_id_box);
        m_swName = editBox.getText().toString();


        if(m_swName.matches("")) {
            MainActivity.myToast(this, getString(R.string.switch_id_box)
                    + " " + getString(R.string.can_not_be_empty));
            editBox.requestFocus();
            return;
        }

        editBox = (EditText) findViewById(R.id.switch_password_box);
        m_swPassword = editBox.getText().toString();

        editBox = (EditText) findViewById(R.id.wifi_ssid_box);
        str = editBox.getText().toString();

        if(!str.matches("")) {
            setupMsg = str + "&";
        }else {
            MainActivity.myToast(this, getString(R.string.wifi_ssid_box)
                    + " " + getString(R.string.can_not_be_empty));
            editBox.requestFocus();
            return;
        }

        editBox = (EditText) findViewById(R.id.wifi_password_box);
        str = editBox.getText().toString();

        MyMqttConnection conn = new MyMqttConnection(((TextView)findViewById(R.id.sw_conn_name)).getText().toString());
        conn.connLoad(this);

        if(!str.matches("")) {
            // setupMsg += str + "&m10.cloudmqtt.com&14110&";
            setupMsg += str + "&"+conn.getServerAddress()+"&"+conn.getSeverPort()+"&";
        }else {
            MainActivity.myToast(this, getString(R.string.wifi_password_box)
                    + " " + getString(R.string.can_not_be_empty));
            editBox.requestFocus();
            return;
        }

        //setupMsg += m_swName + "." + m_swPassword + "&nywjllog&DXwwL_1Bye8x&";

        setupMsg += m_swName + "." + m_swPassword + "&"+conn.getMqttUser()+"&"+conn.getMqttPassword()+"&";

        Log.d("SWITCH_SETUP", setupMsg);

        m_addToList = ((CheckBox) findViewById(R.id.checkBox)).isChecked();

        SwitchSetupThread swst = new SwitchSetupThread ();
        swst.execute(setupMsg);

        // Waiting screen
        setContentView(R.layout.wait_view);
        WebView image = (WebView) findViewById(R.id.wait_image);
        image.loadDataWithBaseURL("file:///android_res/drawable/",
                "<img style=\"display:block; margin-left:auto; margin-right:auto;\" width=\"150\" height=\"150\" src='loader.gif' />", "text/html", "utf-8", null);
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

            //final String ip = "192.168.190.152";
            //final int port = 8080;

            //publishProgress();

            String setupMsg = params[0];

            Socket socket = null;
            try {
                // Make socket to server
                //socket = new Socket(ip, port);
                Log.d("SWITCH_SETUP", "Connecting!");
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 5000);
                Log.d("SWITCH_SETUP", "Connected!");

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

                Log.d("SWITCH_SETUP_RESULT", new String(buffer, 0, bytesRead));

            } catch (UnknownHostException e) {
                e.printStackTrace();
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 2;
            } finally {
                if (socket != null) {/*
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 3;
                    }*/
                }
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return 3;
            }

            return 0; // Successfully setup
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // Wait until communication with Switch completed
            //Integer result = swst.get();
            //Integer result = new Integer(1);

            Log.d("SWITCH_SETUP_THD_REST", result.toString());

            Intent resultIntent = new Intent();

            int resultCode = -1; // Error

            if(result == 0) { // Setup successfully
                if (m_addToList) {
                    resultCode = 2; // No error, add to list
                    resultIntent.putExtra("SW_NAME", m_swName);
                    resultIntent.putExtra("SW_PASSWORD", m_swPassword);
                    resultIntent.putExtra("SW_CONN_NAME", m_swSelectedConnName);

                }else{ // No error, don't add to list
                    resultCode = 1;
                }
            }

            setResult(resultCode, resultIntent);

            finish();
        }

        @Override
        protected void onProgressUpdate(Void...p){

        }
    }
}
