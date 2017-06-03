package com.aiohtea.aiohteacollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SwitchAddingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_adding);
    }

    public void addSwitchOnClick(View newSwitchView){

        EditText switchIdBox = (EditText) findViewById(R.id.switch_id_box);
        EditText switchDescBox = (EditText) findViewById(R.id.switch_desc_box);

        Intent resultIntent = new Intent();

        resultIntent.putExtra("SW_NAME", switchIdBox.getText().toString());
        resultIntent.putExtra("SW_DESC", switchDescBox.getText().toString());


        setResult(DeviceListItem.SWITCH_DEV_TYPE, resultIntent);

        finish();

    }
}
