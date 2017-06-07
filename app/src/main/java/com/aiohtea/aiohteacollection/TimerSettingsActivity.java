package com.aiohtea.aiohteacollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

;

/**
 * Created by Nguyen Truong on 4/14/2017.
 */

public class TimerSettingsActivity extends AppCompatActivity {

    HardwareSettings m_hw;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_settings);

        Intent intent = getIntent();

        String hwSettingString = intent.getStringExtra("HW_SETTINGS");
        m_hw = new HardwareSettings(hwSettingString);
        display();
    }

    /**
     * void display()
     */
    void display(){

        CheckBox cbx;
        EditText etx;
        TextView tvw;
        boolean isEnabled;

        // Set display interval
        cbx = (CheckBox) findViewById(R.id.interval_enable);
        isEnabled = m_hw.isEnabled(0);
        cbx.setChecked(isEnabled);

        etx = (EditText) findViewById(R.id.on_every_value);
        if(isEnabled) {
            etx.setText(m_hw.getStart(0));
        }else{
            etx.setHint("- - - - -");
        }

        etx = (EditText) findViewById(R.id.off_every_value);
        if(isEnabled) {
            etx.setText(m_hw.getStop(0));
        }else{
            etx.setHint("- - - - -");
        }

        // Set display timer 1
        cbx = (CheckBox) findViewById(R.id.timer_1_enable);
        isEnabled = m_hw.isEnabled(1);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getStart(1));
        }else{
            tvw.setText("- -:- -");
        }

        tvw = (TextView) findViewById(R.id.off_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getStop(1));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 2
        cbx = (CheckBox) findViewById(R.id.timer_2_enable);
        isEnabled = m_hw.isEnabled(2);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getStart(2));
        }else{
            tvw.setText("- -:- -");
        }

        tvw = (TextView) findViewById(R.id.off_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getStop(2));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 3
        cbx = (CheckBox) findViewById(R.id.timer_3_enable);
        isEnabled = m_hw.isEnabled(3);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getStart(3));
        }else{
            tvw.setText("- -:- -");
        }

        tvw = (TextView) findViewById(R.id.off_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getStop(3));
        }else{
            tvw.setText("- -:- -");
        }

    }
}
