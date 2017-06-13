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

        EditText etx;


        Intent resultIntent = new Intent();

        etx = (EditText) findViewById(R.id.switch_id_box);
        resultIntent.putExtra("SW_NAME", etx.getText().toString());

        etx = (EditText) findViewById(R.id.switch_password_box);
        resultIntent.putExtra("SW_PASSWORD", etx.getText().toString());

        etx = (EditText) findViewById(R.id.switch_desc_box);
        resultIntent.putExtra("SW_DESC", etx.getText().toString());


        setResult(DeviceListItem.SWITCH_DEV_TYPE, resultIntent);

        finish();

    }
}
