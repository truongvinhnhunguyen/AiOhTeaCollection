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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<DeviceListItem> m_devList;
    private ListView m_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        m_devList = new ArrayList<>();

        // Load added devices
        deviceLoad();

        m_listView = (ListView)findViewById(R.id.device_list);

        m_listView.setAdapter(new DeviceListViewAdapter(this, R.layout.device_list_item, m_devList));

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

        // Class to listen Floating Action Button
        class Xxx implements View.OnClickListener{
            MainActivity m_t;

            Xxx(MainActivity t){ m_t = t;}

            @Override
            public void onClick(View view) {
              Intent intent = new Intent(m_t, SwitchAdding.class);
              startActivityForResult(intent, 1);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new Xxx(this));
     }

    @Override
    // --------------------------------------------------------------------------------------------
    // Process Overflow menu (3 vetical dots menu) on App Bar
    // --------------------------------------------------------------------------------------------
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_setingup_devices:
                intent = new Intent(this, SwitchSetup.class);
                startActivityForResult(intent, 2);
                return true;

            case R.id.action_about:
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    // --------------------------------------------------------------------------------------------
    // Take data from called child such as SwitchAdding
    // --------------------------------------------------------------------------------------------
    void deviceLoad(){
        SharedPreferences settings = this.getSharedPreferences
                (getString(R.string.app_name), MODE_PRIVATE);

        // Load Switch
        HashSet<String> nameSet = (HashSet<String>) settings.getStringSet
                ("SW_NAME_LIST", new HashSet<String>());

        int numDev = nameSet.size();
        Log.d("MAIN_ACT: Numdev stored", Integer.toString(numDev));

        if (numDev == 0) return;

        String[] nameList = new String[numDev];
        nameSet.toArray(nameList);


        for(int i = 0; i < numDev; i++){
            DeviceListItem item = new SwitchListItem(this, nameList[i]);
            item.deviceLoad();
            m_devList.add(item);
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

                    DeviceListItem item = new SwitchListItem(this, switchId, switchDesc,
                                  "tcp://m10.cloudmqtt.com:14110", "nywjllog", "DXwwL_1Bye8x");
                    addDeviceToList(item);
                }
                break;

            case 2: // "Setup device..." overflow menu
                if(resultCode == 2){ //Add to list
                    String swName = data.getStringExtra("SW_NAME");

                    DeviceListItem item = new SwitchListItem(this, swName,
                            "Automatically added", "tcp://m10.cloudmqtt.com:14110", "nywjllog", "DXwwL_1Bye8x");
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
    // --------------------------------------------------------------------------------------------
    public void addDeviceToList(DeviceListItem item){

        m_devList.add(item);
        refreshDeviceList();

        // Write to disk
        item.deviceStore();

        SharedPreferences settings = getSharedPreferences
                (getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        HashSet<String> nameList = new HashSet<String>();

        for(int i=0; i < m_devList.size(); i++){
            nameList.add(m_devList.get(i).getDeviceName());

            Log.d("MAIN_ACT", m_devList.get(i).getDeviceName());
        }

        editor.putStringSet("SW_NAME_LIST", nameList);

        editor.commit();
    }

    // --------------------------------------------------------------------------------------------
    // Ensure change in data is displayed to the devices list
    // --------------------------------------------------------------------------------------------
    public void refreshDeviceList(){
        Log.d("MAIN", "List refreshed!");
        DeviceListViewAdapter a = (DeviceListViewAdapter)m_listView.getAdapter();
        a.notifyDataSetChanged();
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

    // --------------------------------------------------------------------------------------------
    // A utility to post a Toast of information to screen
    // --------------------------------------------------------------------------------------------
    static void myToast(Context ctx, String msg){
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

} // End class
