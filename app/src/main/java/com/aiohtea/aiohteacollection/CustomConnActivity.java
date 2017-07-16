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

public class CustomConnActivity extends AppCompatActivity {

    private ArrayList<String> m_connNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_conn);

        findViewById(R.id.conn_uri).requestFocus();
    }

    /**
     *  Return message: mqttServerUri#mqttUser#mqttPassword#
     * @param newSwitchView
     */
    public void customConnAddingOnClick(View newSwitchView){

        EditText etx;

        Intent resultIntent = new Intent();

        etx = (EditText) findViewById(R.id.conn_uri);
        String uri = etx.getText().toString();

        if(uri.equals("")){
            MainActivity.myToast(this, getString(R.string.conn_url)
                    + " " + getString(R.string.can_not_be_empty));
            etx.requestFocus();
            return;
        }

        resultIntent.putExtra("CONN_URI", uri);

        etx = (EditText) findViewById(R.id.conn_user);
        resultIntent.putExtra("CONN_USER", etx.getText().toString());

        etx = (EditText) findViewById(R.id.conn_password);
        resultIntent.putExtra("CONN_PASSWORD", etx.getText().toString());


        setResult(1, resultIntent);

        finish();

    }
}
