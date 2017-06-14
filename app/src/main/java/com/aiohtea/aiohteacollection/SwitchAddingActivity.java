package com.aiohtea.aiohteacollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class SwitchAddingActivity extends AppCompatActivity {

    private ArrayList<String> m_connNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_adding);

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
                PopupMenu popupMenu = new PopupMenu(SwitchAddingActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.dev_gen_popup_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.clear();

                int size = m_connNameList.size();

                for(int i=0; i<size; i++) {
                    menu.add(m_connNameList.get(i));
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ((TextView)findViewById(R.id.sw_conn_name)).setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });


    }

    public void addSwitchOnClick(View newSwitchView){

        EditText etx;


        Intent resultIntent = new Intent();

        etx = (EditText) findViewById(R.id.switch_id_box);
        String swName = etx.getText().toString();
        if(swName.equals("")){
            MainActivity.myToast(this, getString(R.string.switch_id_box)
                    + " " + getString(R.string.can_not_be_empty));
            etx.requestFocus();
            return;
        }

        resultIntent.putExtra("SW_NAME", swName);

        etx = (EditText) findViewById(R.id.switch_password_box);
        resultIntent.putExtra("SW_PASSWORD", etx.getText().toString());

        etx = (EditText) findViewById(R.id.switch_desc_box);
        resultIntent.putExtra("SW_DESC", etx.getText().toString());

        resultIntent.putExtra("SW_CONN_NAME", ((TextView)findViewById(R.id.sw_conn_name)).getText().toString());


        setResult(DeviceListItem.SWITCH_DEV_TYPE, resultIntent);

        finish();

    }
}
