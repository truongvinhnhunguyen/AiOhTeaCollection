package com.aiohtea.aiohteacollection;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // --------------------------------------------------------------------------------------------
    // A utility to post a Toast of information to screen
    // --------------------------------------------------------------------------------------------
    static void myToast(Context ctx, String msg){
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1: // Floating button returns for adding a new device to list
                if (resultCode == DeviceListItem.SWITCH_DEV_TYPE) {
                    String switchId = data.getStringExtra("switchid");
                    String switchDesc = data.getStringExtra("switchdesc");

                    DeviceListItem item = new SwitchListItem(this, switchId, switchDesc,
                                  "tcp://m10.cloudmqtt.com:14110", "nywjllog", "DXwwL_1Bye8x");

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

                    myToast(getApplicationContext(), switchId + "-" + switchDesc);
                }

                break;
        }
    }
    // --------------------------------------------------------------------------------------------
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

        DeviceListItem clickedDevice = m_devList.get(position);

        clickedDevice.onClick(getApplicationContext(), parent, view, position, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
