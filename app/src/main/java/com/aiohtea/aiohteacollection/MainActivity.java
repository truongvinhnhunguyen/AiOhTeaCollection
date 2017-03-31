package com.aiohtea.aiohteacollection;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<DeviceListItem> m_swList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        m_swList = new ArrayList<DeviceListItem>();

        for(int i = 0; i<3; i++){
            m_swList.add(new SwitchListItem("Switch-" + i, "Description of: "+i, i%3));
        }

        ListView listView = (ListView)findViewById(R.id.device_list);

        listView.setAdapter(new DeviceListViewAdapter(this, R.layout.device_list_item, m_swList));

        listView.setOnItemClickListener(this);


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

    static void myToast(Context ctx, String msg){
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    // Take data from called child such as SwitchAdding
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String switchId = data.getStringExtra("switchid");
                    String switchDesc = data.getStringExtra("switchdesc");
                    myToast(getApplicationContext(), switchId + "-" + switchDesc);
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + m_swList.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
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
