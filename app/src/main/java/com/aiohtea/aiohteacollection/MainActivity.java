package com.aiohtea.aiohteacollection;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<MyMqttConnection> m_connList;
    private List<DeviceListItem> m_devList;
    private ListView m_listView;


    // DEFAULT CONNECTION
    final String DEFAULT_CONN_NAME = "CloudMQTT";
    final String DEFAULT_CONN_URI = "tcp://m10.cloudmqtt.com:14110";
    final String DEFAULT_CONN_USER = "nywjllog";
    final String DEFAULT_CONN_PASS = "DXwwL_1Bye8x";
/*
    // DEFAULT CONNECTION
    final String DEFAULT_CONN_NAME = "HiveMQ";
    final String DEFAULT_CONN_URI = "tcp://broker.hivemq.com:1883";
    final String DEFAULT_CONN_USER = "";
    final String DEFAULT_CONN_PASS = "";
    */

    /*
     * =============================================================================================
     * AREA TO DEFINE OVERRIDE FUNCTIONS
     * =============================================================================================
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create and Load added devices
        m_devList = new ArrayList<>();
        deviceLoad();

        // Create and Load MQTT connections
        m_connList = new ArrayList<>();
        connectionLoad();


        if(m_connList.size() == 0) {
            // MAKE A DEFAULT CONNECTION
            MyMqttConnection conn = new MyMqttConnection(DEFAULT_CONN_NAME, DEFAULT_CONN_URI,
                    DEFAULT_CONN_USER, DEFAULT_CONN_PASS);
            addConnectionToList(conn);
            // END MAKE A DEFAULT CONNECTION
        }


        // Initiate displayed list
        m_listView = (ListView)findViewById(R.id.device_list);
        m_listView.setAdapter(new DeviceListViewAdapter(this, R.layout.switch_list_item, m_devList));


        m_listView.setLongClickable(true);
        m_listView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
            int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });

        m_listView.setOnItemClickListener(this);



        // ================== Class to listen Floating Action Button ===============================
        class Xxx implements View.OnClickListener{
            private MainActivity m_t;

            Xxx(MainActivity t){ m_t = t;}

            @Override
            public void onClick(View view) {
              Intent intent = new Intent(m_t, SwitchAddingActivity.class);
              startActivityForResult(intent, 1);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new Xxx(this));
     }

    // --------------------------------------------------------------------------------------------
    // void onResume()
    // --------------------------------------------------------------------------------------------
    @Override
    public void onResume(){
        super.onResume();

        int size = m_connList.size();

        for(int i=0; i < size; i++) {
            m_connList.get(i).connect(this);
        }
    }

    // --------------------------------------------------------------------------------------------
    // public void onStop()
    // --------------------------------------------------------------------------------------------
    @Override
    public void onStop(){
        super.onStop();


        int size = m_connList.size();

        for(int i=0; i < size; i++) {
            m_connList.get(i).disconnect();
        }

    }

    @Override
    // --------------------------------------------------------------------------------------------
    // Process Overflow menu (3 vetical dots menu) on App Bar
    // --------------------------------------------------------------------------------------------
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_setingup_devices:
                intent = new Intent(this, SwitchSetupActivity.class);
                startActivityForResult(intent, 2);
                return true;

            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // --------------------------------------------------------------------------------------------
    // This function is call from returning of Activities/View called by MainActivity
    // such as Floating button to add new device; set up device item from overflow menu
    // --------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1: // Floating button returns for adding a new device to list
                if (resultCode == DeviceListItem.SWITCH_DEV_TYPE) {
                    String switchId = data.getStringExtra("SW_NAME");
                    String switchDesc = data.getStringExtra("SW_DESC");

                    DeviceListItem item =
                            new SwitchListItem(switchId, switchDesc, DEFAULT_CONN_NAME);
                    addDeviceToList(item);
                }
                break;

            case 2: // "Setup device..." overflow menu
                if(resultCode == 2){ //Add to list
                    String swName = data.getStringExtra("SW_NAME");

                    DeviceListItem item = new SwitchListItem(swName, "Automatically added", DEFAULT_CONN_NAME);
                    addDeviceToList(item);
                }

                if(resultCode == -1)
                    myToast(this, "Setup error!");
                else
                if(resultCode != 0)
                    myToast(this, "Setup successfully!!");
                break;
        }
    }

    // --------------------------------------------------------------------------------------------
    // An item in device list is clicked.
    // It call DeviceListItem.onClick for corresponding activities
    // --------------------------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // DeviceListItem clickedDevice = m_devList.get(position);
        // clickedDevice.onClick(getApplicationContext(), parent, view, position, id);
    }

    // --------------------------------------------------------------------------------------------
    // Android Studio created
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /*
     * =============================================================================================
     * AREA TO DEFINE OTHER FUNCTIONS
     * =============================================================================================
     */

    // --------------------------------------------------------------------------------------------
    // Load devices
    // --------------------------------------------------------------------------------------------
    void deviceLoad(){
        SharedPreferences settings = this.getSharedPreferences
                (getString(R.string.app_name), MODE_PRIVATE);

        // Load Switch
        HashSet<String> nameSet = (HashSet<String>) settings.getStringSet
                ("SW_NAME_LIST", new HashSet<String>());

        int numDev = nameSet.size();
        Log.d("MA: Numdev stored", Integer.toString(numDev));

        if (numDev == 0) return;

        String[] nameList = new String[numDev];
        nameSet.toArray(nameList);


        for(int i = 0; i < numDev; i++){
            DeviceListItem item = new SwitchListItem(nameList[i]);
            item.deviceLoad(this);
            m_devList.add(item);
        }
    }

    // --------------------------------------------------------------------------------------------
    // public void addDeviceToList(DeviceListItem item)
    // --------------------------------------------------------------------------------------------
    public void addDeviceToList(DeviceListItem item){

        m_devList.add(item);
        refreshDeviceList();

        // Write to disk
        item.deviceStore(this);

        SharedPreferences settings = getSharedPreferences
                (getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        HashSet<String> nameList = new HashSet<String>();

        for(int i=0; i < m_devList.size(); i++){
            nameList.add(m_devList.get(i).getDeviceName());

            Log.d("MA.addDeviceToList: ", m_devList.get(i).getDeviceName());
        }

        editor.putStringSet("SW_NAME_LIST", nameList);

        editor.commit();
    }

    // --------------------------------------------------------------------------------------------
    // Load connection
    // --------------------------------------------------------------------------------------------
    void connectionLoad(){
        SharedPreferences settings = this.getSharedPreferences
                (getString(R.string.app_name), MODE_PRIVATE);

        // Load Switch
        HashSet<String> nameSet = (HashSet<String>) settings.getStringSet
                ("MQTT_CONN_NAME_LIST", new HashSet<String>());

        int numConn = nameSet.size();
        Log.d("MA: Numdev stored", Integer.toString(numConn));

        if (numConn == 0) return;

        String[] nameList = new String[numConn];
        nameSet.toArray(nameList);


        for(int i = 0; i < numConn; i++){
            MyMqttConnection conn = new MyMqttConnection (nameList[i]);
            conn.connLoad(this);
            m_connList.add(conn);
        }
    }

    // --------------------------------------------------------------------------------------------
    // void commInit()
    // For Switch, subscribe "Status" and "Settings" to initiate the switch
    // --------------------------------------------------------------------------------------------
    public void commInit(){
        // Communication initiating for all devices
        int size = m_devList.size();
        for(int i=0; i < size; i++) {
            m_devList.get(i).commInit(this);
        }
    }

    // --------------------------------------------------------------------------------------------
    // public void addConnectionToList(MyMqttConnection conn)
    // --------------------------------------------------------------------------------------------
    public void addConnectionToList(MyMqttConnection conn){

        m_connList.add(conn);

        // Write to disk
        conn.connStore(this);

        SharedPreferences settings = getSharedPreferences
                (getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        HashSet<String> nameList = new HashSet<String>();

        for(int i=0; i < m_connList.size(); i++){
            nameList.add(m_connList.get(i).getconnName());

            Log.d("MA.addConnToList:", m_connList.get(i).getconnName());
        }

        editor.putStringSet("MQTT_CONN_NAME_LIST", nameList);

        editor.commit();
    }


    // --------------------------------------------------------------------------------------------
    // Ensure change in data is displayed to the devices list
    // --------------------------------------------------------------------------------------------
    public void refreshDeviceList(){
        DeviceListViewAdapter a = (DeviceListViewAdapter)m_listView.getAdapter();
        a.notifyDataSetChanged();
    }


    // --------------------------------------------------------------------------------------------
    // Android Studio created
    // --------------------------------------------------------------------------------------------
    public MyMqttConnection getConnByName(String name) {
        int size = m_connList.size();

        for(int i=0; i < size; i++){
            String connName = m_connList.get(i).getconnName();
           if (connName.equals(name)) {
               Log.d("MA.getConnByName: ", connName);
               return m_connList.get(i);
           }
        }
        Log.d("MA.getConnByName: ", "NOT FOUND");
        return null;
    }

    // --------------------------------------------------------------------------------------------
    // Process MQTT message arrival
    // --------------------------------------------------------------------------------------------
    public void mqttMessageArrive(String connName, String topic, byte[] payload){
        Log.d("MA.mqttMsgArrive: ", "Topic: " + topic + ": "+ (char)payload[0]);

        int size = m_devList.size();

        for(int i=0; i < size; i++){
            DeviceListItem item = m_devList.get(i);

            if(connName.equals(item.getConnName()) && topic.contains("/"+item.getDeviceName()+"/")){
                item.mqttMessageArrive(this, topic.substring(topic.lastIndexOf("/")+1), payload);
                break;
            }
        }
    }



    // --------------------------------------------------------------------------------------------
    // A utility to post a Toast of information to screen
    // --------------------------------------------------------------------------------------------
    static void myToast(Context ctx, String msg){
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

} // End class
