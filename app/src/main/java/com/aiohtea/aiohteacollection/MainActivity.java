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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

//@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<MyMqttConnection> m_connList;
    private List<DeviceListItem> m_devList;
    private ListView m_listView;


    // Returning code
    final public static int ADD_DEVICE_ACTIVITY = 1;
    final public static int SETUP_DEVICE_ACTIVITY = 2;
    final public static int TIMER_SETTING_ACTIVITY = 3;
    final public static int CUSTOM_CONN_ACTIVITY = 4;


    // DEFAULT CONNECTION
    public final static String DEFAULT_CONN_NAME_1 = "Default";
    public final static String DEFAULT_CONN_URL_1 = "tcp://iot.eclipse.org:1883";
    public final static String DEFAULT_CONN_USER_1 = "";
    public final static String DEFAULT_CONN_PASS_1 = "";

    // DEFAULT CONNECTION (aiohtee@gmail.com)
    public final static String DEFAULT_CONN_NAME_2 = "CloudMQTT 1";
    public final static  String DEFAULT_CONN_URL_2 = "tcp://m10.cloudmqtt.com:15730";
    public final static  String DEFAULT_CONN_USER_2 = "doopmmgi";
    public final static  String DEFAULT_CONN_PASS_2 = "qKpKXAgW4N2X";

    // DEFAULT CONNECTION (truongvinhnhunguyen@gmail.com)
    public final static String DEFAULT_CONN_NAME_3 = "CloudMQTT 2";
    public final static  String DEFAULT_CONN_URL_3 = "tcp://m10.cloudmqtt.com:14110";
    public final static  String DEFAULT_CONN_USER_3 = "nywjllog";
    public final static  String DEFAULT_CONN_PASS_3 = "DXwwL_1Bye8x";

    /*
    // DEFAULT CONNECTION
    public final static String DEFAULT_CONN_NAME_4 = "HiveMQ";
    public final static String DEFAULT_CONN_URL_4 = "tcp://broker.hivemq.com:1883";
    public final static String DEFAULT_CONN_USER_4 = "";
    public final static String DEFAULT_CONN_PASS_4 = "";
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
            MyMqttConnection conn = new MyMqttConnection(DEFAULT_CONN_NAME_1, DEFAULT_CONN_URL_1,
                    DEFAULT_CONN_USER_1, DEFAULT_CONN_PASS_1);
            addConnectionToList(conn);

            conn = new MyMqttConnection(DEFAULT_CONN_NAME_2, DEFAULT_CONN_URL_2,
                    DEFAULT_CONN_USER_2, DEFAULT_CONN_PASS_2);
            addConnectionToList(conn);

            conn = new MyMqttConnection(DEFAULT_CONN_NAME_3, DEFAULT_CONN_URL_3,
                    DEFAULT_CONN_USER_3, DEFAULT_CONN_PASS_3);
            addConnectionToList(conn);

            addConnectionToList(conn);

            // END MAKE A DEFAULT CONNECTION
        }


        // Initiate displayed list
        m_listView = (ListView)findViewById(R.id.device_list);
        m_listView.setAdapter(new DeviceListViewAdapter(this, R.layout.switch_list_item, m_devList));


        m_listView.setLongClickable(true);
        m_listView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                myToast(MainActivity.this, m_devList.get(pos).getConnName());

                return true;
            }
        });

        m_listView.setOnItemClickListener(this);

        connEstablish();



        // ================== Class to listen Floating Action Button ===============================
        class Xxx implements View.OnClickListener{
            private MainActivity m_t;

            Xxx(MainActivity t){ m_t = t;}

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(m_t, SwitchAddingActivity.class);
                intent.putExtra("SW_CONN_NAME_LIST", connNameListToString());
                startActivityForResult(intent, ADD_DEVICE_ACTIVITY);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new Xxx(this));
     }

    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
     void connRefresh(){
         int size = m_connList.size();

         for(int i=0; i < size; i++) {
             m_connList.get(i).refresh(this);
         }

         refreshDeviceList();
     }
    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
     public void connEstablish(){
         int size = m_connList.size();

         for(int i=0; i < size; i++) {
             m_connList.get(i).connect(this);
             // myToast(this, "Establishing conn:" + m_connList.get(i).getconnName());
         }

         refreshDeviceList();
     }

    // --------------------------------------------------------------------------------------------
    // void onResume()
    // --------------------------------------------------------------------------------------------
    @Override
    public void onResume(){
        super.onResume();
        connRefresh();
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
                intent.putExtra("SW_CONN_NAME_LIST", connNameListToString());
                startActivityForResult(intent, SETUP_DEVICE_ACTIVITY);
                return true;

            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_custom_conn:
                intent = new Intent(this, CustomConnActivity.class);
                startActivityForResult(intent, CUSTOM_CONN_ACTIVITY);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    // --------------------------------------------------------------------------------------------
    // This function is called from returning of Activities/View called by MainActivity
    // such as Floating button to add new device; set up device item from overflow menu
    // --------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case ADD_DEVICE_ACTIVITY: // Floating button returns for adding a new device to list
                if (resultCode == DeviceListItem.SWITCH_DEV_TYPE) {
                    String swName = data.getStringExtra("SW_NAME");
                    String swPassword = data.getStringExtra("SW_PASSWORD");
                    String swDesc = data.getStringExtra("SW_DESC");
                    String swConnName = data.getStringExtra("SW_CONN_NAME");

                    String swId = Long.toString(System.currentTimeMillis());
                    DeviceListItem item = new SwitchListItem(swId, swName, swPassword, swDesc, swConnName);
                    addDeviceToList(item);

                    // Log.d("FIND_DEV_BY_ID", "ADD: "+ swId);

                }
                break;

            case SETUP_DEVICE_ACTIVITY: // "Setup device..." overflow menu
                if(resultCode == 2){ //Add to list
                    String swName = data.getStringExtra("SW_NAME");
                    String swPassword = data.getStringExtra("SW_PASSWORD");
                    String swConnName = data.getStringExtra("SW_CONN_NAME");

                    DeviceListItem item =
                            new SwitchListItem(Long.toString(System.currentTimeMillis()), swName,
                                    swPassword, "Automatically added", swConnName);
                    addDeviceToList(item);
                }

                if(resultCode == -1)
                    myToast(this, "Setup error!");
                else
                if(resultCode != 0)
                    myToast(this, "Setup successfully!!");
                break;

            case TIMER_SETTING_ACTIVITY: // Timer settings
                if (resultCode == 1)
                {
                    String cmd = data.getStringExtra("HW_SETTINGS");
                    String devId = data.getStringExtra("HW_SETTINGS_DEV_ID");

                    myToast(this, "Timer setting to device: " + devId);
                    Log.d("TIMER_CMD", cmd);

                    SwitchListItem dev = (SwitchListItem)getDeviveByDeviceId(devId);
                    dev.commandHardware(this, cmd.getBytes());
                }
                break;

            case CUSTOM_CONN_ACTIVITY:
                String uri = data.getStringExtra("CONN_URI");
                String username = data.getStringExtra("CONN_USER");
                String password = data.getStringExtra("CONN_PASSWORD");

                myToast(this, uri+"-"+username+"-"+password);



                break;
        }

        refreshDeviceList();

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
    // Put Connection names into a string that can be passed to other Activity via Intent Str Extra
    // --------------------------------------------------------------------------------------------
    public String connNameListToString(){
        String s = "";

        int size = m_connList.size();

        for (int i=0; i<size; i++){
            s += m_connList.get(i).getconnName();
            s += '#';
        }

        return s;
    }

    // --------------------------------------------------------------------------------------------
    // Put Device names into a string that can be passed to other Activity via Intent Str Extra
    // --------------------------------------------------------------------------------------------
    public String devIdListToString(){
        String s = "";

        int size = m_devList.size();

        for (int i=0; i<size; i++){
            s += m_devList.get(i).getDeviceId();
            s += '#';
        }

        return s;
    }

    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
    static ArrayList<String> parseNameListString(String nameListString){
        ArrayList <String> ret = new ArrayList();

        int len = nameListString.length();

        String s = "";
        for (int i=0; i<len; i++){
            char c = nameListString.charAt(i);

            if(c != '#'){
                s += c;
            } else {
                ret.add(s);
                s = "";
            }
        }

        return ret;
    }




    // --------------------------------------------------------------------------------------------
    // Load devices
    // --------------------------------------------------------------------------------------------
    void deviceLoad(){
        SharedPreferences settings = this.getSharedPreferences
                (getString(R.string.app_name), MODE_PRIVATE);

        ArrayList<String> devNameList = parseNameListString(settings.getString("SW_ID_LIST", ""));

        int numDev = devNameList.size();


        for(int i = 0; i < numDev; i++){
            DeviceListItem item = new SwitchListItem(devNameList.get(i));
            item.deviceLoad(this);
            m_devList.add(item);
        }

    }

    // --------------------------------------------------------------------------------------------
    // public void addDeviceToList(DeviceListItem item)
    // --------------------------------------------------------------------------------------------
    public void addDeviceToList(DeviceListItem item){

        m_devList.add(item);

        // Write to disk
        item.deviceStore(this);
        updateDeviceListStorage();

        refreshDeviceList();
    }

    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
    public void deleteDevice(String deviceId){
        int size = m_devList.size();

        for (int i=0; i<size; i++){
            DeviceListItem dev = m_devList.get(i);
            if(deviceId.equals(dev.getDeviceId())){
                dev = m_devList.remove(i);
                dev.commRelease(this);
                dev.deviceClear(this);
                updateDeviceListStorage();
                refreshDeviceList();
                return;
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Load connection
    // --------------------------------------------------------------------------------------------
    void connectionLoad(){
        SharedPreferences settings = this.getSharedPreferences
                (getString(R.string.app_name), MODE_PRIVATE);

        ArrayList<String> connNameList = parseNameListString(settings.getString("MQTT_CONN_NAME_LIST", ""));

        int numConn = connNameList.size();

        for(int i = 0; i < numConn; i++){
            MyMqttConnection conn = new MyMqttConnection (connNameList.get(i));
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
    // --------------------------------------------------------------------------------------------
    void updateDeviceListStorage(){
        SharedPreferences settings = getSharedPreferences
                (getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();


        String devListString = devIdListToString();
        editor.putString("SW_ID_LIST", devListString);

        editor.commit();
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

        String connListString = connNameListToString();
        editor.putString("MQTT_CONN_NAME_LIST", connListString);

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
    /**
     *
     * @param id
     * @return
     */
     // --------------------------------------------------------------------------------------------
    public DeviceListItem getDeviveByDeviceId(String id) {
        int size = m_devList.size();

        for(int i=0; i<size; i++){
            DeviceListItem dev = m_devList.get(i);
            String devId = dev.getDeviceId();

            // Log.d("FIND_DEV_BY_ID", "ID: "+devId);

            if(devId.equals(id)){
                return dev;
            }
        }

        // Log.d("FIND_DEV_BY_ID", id + " not found. Size:" + size);
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

            if(connName.equals(item.getConnName()) && topic.contains(item.getDeviceTopicID())){
                item.mqttMessageArrive(this, topic.substring(topic.lastIndexOf("/")+1), payload);
                //break;
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
