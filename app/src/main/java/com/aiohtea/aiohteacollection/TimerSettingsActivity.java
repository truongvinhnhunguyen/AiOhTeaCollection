package com.aiohtea.aiohteacollection;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

;import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Nguyen Truong on 4/14/2017.
 */


public class TimerSettingsActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener {

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
     * public void onFocusChange(View v, boolean hasFocus)
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus){

    }

    /**
     * public void onClick (View v)
     * @param v
     */
    @Override
    public void onClick (View v){
        int id = v.getId();

        switch(id){
            case R.id.interval_on_enable:
            case R.id.interval_off_enable:
                EditText etx;
                int editTextId;

                if(id == R.id.interval_on_enable)
                    editTextId = R.id.on_every_value;
                else
                    editTextId = R.id.off_every_value;

                etx = (EditText) findViewById(editTextId);

                if(((CheckBox)v).isChecked()){
                    etx.setText("60");
                }else{
                    etx.getText().clear();
                    etx.setHint("- - - - -");
                }
                etx.requestFocus();
                etx.setSelection(etx.getText().length());

                break;

            case R.id.timer_1_on_enable:
            case R.id.timer_2_on_enable:
            case R.id.timer_3_on_enable:
            case R.id.timer_1_off_enable:
            case R.id.timer_2_off_enable:
            case R.id.timer_3_off_enable:

                TextView tvw;
                int textViewId = R.id.timer_1_on_enable;
                int timerId = 1;
                boolean onOrOff = true;

                switch (id){
                    case R.id.timer_1_on_enable:
                        textViewId = R.id.on_at_value_1;
                        timerId = 1;
                        onOrOff = true;
                        break;

                    case R.id.timer_2_on_enable:
                        textViewId = R.id.on_at_value_2;
                        timerId = 2;
                        onOrOff = true;
                        break;

                    case R.id.timer_3_on_enable:
                        textViewId = R.id.on_at_value_3;
                        timerId = 3;
                        onOrOff = true;
                        break;

                    case R.id.timer_1_off_enable:
                        textViewId = R.id.off_at_value_1;
                        timerId = 1;
                        onOrOff = false;
                        break;

                    case R.id.timer_2_off_enable:
                        textViewId = R.id.off_at_value_2;
                        timerId = 2;
                        onOrOff = false;
                        break;

                    case R.id.timer_3_off_enable:
                        textViewId = R.id.off_at_value_3;
                        timerId = 3;
                        onOrOff = false;
                        break;

                }

                int h = -1, m=-1;

                tvw = (TextView)findViewById(textViewId);

                if(((CheckBox)v).isChecked()) {
                    if(m_hw.isEnabled(timerId, onOrOff)) {
                        String s = m_hw.getValueString(timerId, onOrOff);
                        h = Integer.valueOf(s.substring(0, 2)).intValue();
                        m = Integer.valueOf(s.substring(3, 5)).intValue();
                    }

                    MyTimePickerDialog tp;
                    tp = new MyTimePickerDialog(this, tvw, h, m);
                    tp.show();
                }

                break;
        }
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
        cbx = (CheckBox) findViewById(R.id.interval_on_enable);
        isEnabled = m_hw.isEnabled(0, true);
        cbx.setChecked(isEnabled);

        etx = (EditText) findViewById(R.id.on_every_value);
        if(isEnabled) {
            etx.setText(m_hw.getValueString(0, true));
        }else{
            etx.setHint("- - - - -");
        }

        cbx = (CheckBox) findViewById(R.id.interval_off_enable);
        isEnabled = m_hw.isEnabled(0, false);
        cbx.setChecked(isEnabled);

        etx = (EditText) findViewById(R.id.off_every_value);
        if(isEnabled) {
            etx.setText(m_hw.getValueString(0, false));
        }else{
            etx.setHint("- - - - -");
        }

        // Set display timer 1
        cbx = (CheckBox) findViewById(R.id.timer_1_on_enable);
        isEnabled = m_hw.isEnabled(1, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(1, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_1_off_enable);
        isEnabled = m_hw.isEnabled(1, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(1, false));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 2
        cbx = (CheckBox) findViewById(R.id.timer_2_on_enable);
        isEnabled = m_hw.isEnabled(2, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(2, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_2_off_enable);
        isEnabled = m_hw.isEnabled(1, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(2, false));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 3
        cbx = (CheckBox) findViewById(R.id.timer_3_on_enable);
        isEnabled = m_hw.isEnabled(3, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(3, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_3_off_enable);
        isEnabled = m_hw.isEnabled(1, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getValueString(3, false));
        }else{
            tvw.setText("- -:- -");
        }

    }


    /**
     *
     */
    class MyTimePickerDialog implements TimePickerDialog.OnTimeSetListener {

        TimerSettingsActivity m_ctx;
        TextView m_tvw;

        int m_hour;
        int m_min;

        MyTimePickerDialog (TimerSettingsActivity ctx, TextView tvw, int h, int m){
            m_ctx = ctx;
            m_tvw = tvw;

            if(h < 0){
                Calendar cal = Calendar.getInstance();
                m_hour = cal.get(Calendar.HOUR_OF_DAY);
                m_min = cal.get(Calendar.MINUTE);
            } else {
                m_hour = h;
                m_min = m;
            }
        }

        void show(){
            new TimePickerDialog(m_ctx, this, m_hour, m_min, true).show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String s = "";

            if(hourOfDay < 10)
                s += '0';
            s += hourOfDay;

            if (minute < 10)
                s += '0';
            s += minute;

            m_tvw.setText(s);
        }

    }
}
