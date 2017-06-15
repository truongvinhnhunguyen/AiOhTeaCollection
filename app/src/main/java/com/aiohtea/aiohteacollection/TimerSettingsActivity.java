package com.aiohtea.aiohteacollection;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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


public class TimerSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    HardwareSettings m_hw;
    String m_deviceId;
    TimerSettingsActivity m_this;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_settings);

        m_this = this;

        Intent intent = getIntent();

        String hwSettingString = intent.getStringExtra("HW_SETTINGS");
        m_hw = new HardwareSettings(hwSettingString);
        m_deviceId = intent.getStringExtra("HW_SETTINGS_DEV_ID");

        display();


        ((EditText)findViewById(R.id.on_every_value)).addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() != 0)
                            ((CheckBox) m_this.findViewById(R.id.interval_on_enable)).setChecked(true);
                        else
                            ((CheckBox) m_this.findViewById(R.id.interval_on_enable)).setChecked(false);
                   }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

        ((EditText)findViewById(R.id.off_every_value)).addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() != 0)
                            ((CheckBox) m_this.findViewById(R.id.interval_off_enable)).setChecked(true);
                        else
                            ((CheckBox) m_this.findViewById(R.id.interval_off_enable)).setChecked(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
    }


    /**
     *
     * @param v
     */
    @Override
    public void onClick (View v){
        if(v.getId() != R.id.timer_setup_button) {
            reactOnClickTimerChange(v);
        }
        else{ // Setup button was pressed

            String s;

            s = ((EditText) findViewById(R.id.on_every_value)).getText().toString();
            m_hw.setIntervalValue(true, s);

            s = ((EditText) findViewById(R.id.off_every_value)).getText().toString();
            m_hw.setIntervalValue(false, s);

            s = ((TextView) findViewById(R.id.on_at_value_1)).getText().toString();
            m_hw.setTimerValue(1, true, s);

            s = ((TextView) findViewById(R.id.off_at_value_1)).getText().toString();
            m_hw.setTimerValue(1, false, s);

            s = ((TextView) findViewById(R.id.on_at_value_2)).getText().toString();
            m_hw.setTimerValue(2, true, s);

            s = ((TextView) findViewById(R.id.off_at_value_2)).getText().toString();
            m_hw.setTimerValue(2, false, s);

            s = ((TextView) findViewById(R.id.on_at_value_3)).getText().toString();
            m_hw.setTimerValue(3, true, s);

            s = ((TextView) findViewById(R.id.off_at_value_3)).getText().toString();
            m_hw.setTimerValue(3, false, s);

            Intent resultIntent = new Intent();

            resultIntent.putExtra("HW_SETTINGS", m_hw.constructTimerSettingPayload());
            resultIntent.putExtra("HW_SETTINGS_DEV_ID", m_deviceId);

            setResult(1, resultIntent);

            finish();

        }
    }

    /**
     *
     * @param v
     */
    void reactOnClickTimerChange (View v){

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
            case R.id.on_at_value_1:
            case R.id.on_at_value_2:
            case R.id.on_at_value_3:
            case R.id.off_at_value_1:
            case R.id.off_at_value_2:
            case R.id.off_at_value_3:

                TextView tvw;
                CheckBox cbx;

                int textViewId = R.id.timer_1_on_enable;
                int cbxId = R.id.timer_1_on_enable;
                int timerId = 1;
                boolean onOrOff = true;

                boolean isCbxClicked = false;

                switch (id){
                    case R.id.timer_1_on_enable:
                        isCbxClicked = true;
                    case R.id.on_at_value_1:
                        textViewId = R.id.on_at_value_1;
                        cbxId = R.id.timer_1_on_enable;
                        timerId = 1;
                        onOrOff = true;
                        break;

                    case R.id.timer_2_on_enable:
                        isCbxClicked = true;
                    case R.id.on_at_value_2:
                        textViewId = R.id.on_at_value_2;
                        cbxId = R.id.timer_2_on_enable;
                        timerId = 2;
                        onOrOff = true;
                        break;

                    case R.id.timer_3_on_enable:
                        isCbxClicked = true;
                    case R.id.on_at_value_3:
                        textViewId = R.id.on_at_value_3;
                        cbxId = R.id.timer_2_on_enable;
                        timerId = 3;
                        onOrOff = true;
                        break;

                    case R.id.timer_1_off_enable:
                        isCbxClicked = true;
                    case R.id.off_at_value_1:
                        textViewId = R.id.off_at_value_1;
                        cbxId = R.id.timer_1_off_enable;
                        timerId = 1;
                        onOrOff = false;
                        break;

                    case R.id.timer_2_off_enable:
                        isCbxClicked = true;
                    case R.id.off_at_value_2:
                        textViewId = R.id.off_at_value_2;
                        cbxId = R.id.timer_2_off_enable;
                        timerId = 2;
                        onOrOff = false;
                        break;

                    case R.id.timer_3_off_enable:
                        isCbxClicked = true;
                    case R.id.off_at_value_3:
                        textViewId = R.id.off_at_value_3;
                        cbxId = R.id.timer_3_off_enable;
                        timerId = 3;
                        onOrOff = false;
                        break;

                }

                int h = -1, m=-1;

                tvw = (TextView)findViewById(textViewId);
                cbx = (CheckBox) findViewById(cbxId);

                if (isCbxClicked && (!((CheckBox) v).isChecked())) {
                    tvw.setText("- -:- -"); // Length must be > 5 :-)
                } else {

                    if (m_hw.isEnabled(timerId, onOrOff)) {
                        String s = m_hw.getTimerString(timerId, onOrOff);
                        h = Integer.valueOf(s.substring(0, 2));
                        m = Integer.valueOf(s.substring(3, 5));
                    }

                    MyTimePickerDialog tp;
                    tp = new MyTimePickerDialog(this, tvw, cbx, isCbxClicked, h, m);
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
            etx.setText(m_hw.getIntervalString(true));
        }else{
            etx.setHint("- - - - -");
        }

        cbx = (CheckBox) findViewById(R.id.interval_off_enable);
        isEnabled = m_hw.isEnabled(0, false);
        cbx.setChecked(isEnabled);

        etx = (EditText) findViewById(R.id.off_every_value);
        if(isEnabled) {
            etx.setText(m_hw.getIntervalString(false));
        }else{
            etx.setHint("- - - - -");
        }

        // Set display timer 1
        cbx = (CheckBox) findViewById(R.id.timer_1_on_enable);
        isEnabled = m_hw.isEnabled(1, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(1, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_1_off_enable);
        isEnabled = m_hw.isEnabled(1, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_1);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(1, false));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 2
        cbx = (CheckBox) findViewById(R.id.timer_2_on_enable);
        isEnabled = m_hw.isEnabled(2, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(2, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_2_off_enable);
        isEnabled = m_hw.isEnabled(2, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_2);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(2, false));
        }else{
            tvw.setText("- -:- -");
        }

        // Set display timer 3
        cbx = (CheckBox) findViewById(R.id.timer_3_on_enable);
        isEnabled = m_hw.isEnabled(3, true);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.on_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(3, true));
        }else{
            tvw.setText("- -:- -");
        }

        cbx = (CheckBox) findViewById(R.id.timer_3_off_enable);
        isEnabled = m_hw.isEnabled(3, false);
        cbx.setChecked(isEnabled);

        tvw = (TextView) findViewById(R.id.off_at_value_3);
        if (isEnabled) {
            tvw.setText(m_hw.getTimerString(3, false));
        }else{
            tvw.setText("- -:- -");
        }

    }


    /**
     * class MyTimePickerDialog
     */
    class MyTimePickerDialog implements TimePickerDialog.OnTimeSetListener {

        TimerSettingsActivity m_ctx;
        TextView m_tvw;
        CheckBox m_cbx;

        int m_hour;
        int m_min;
        boolean m_isCbxClicked;

        TimePickerDialog m_tp;


        MyTimePickerDialog (TimerSettingsActivity ctx, TextView tvw, CheckBox cbx, final boolean isCbxClicked, int h, int m){
            m_ctx = ctx;
            m_tvw = tvw;
            m_cbx = cbx;
            m_isCbxClicked = isCbxClicked;

            if(h < 0){
                Calendar cal = Calendar.getInstance();
                m_hour = cal.get(Calendar.HOUR_OF_DAY);
                m_min = cal.get(Calendar.MINUTE);
            } else {
                m_hour = h;
                m_min = m;
            }

            m_tp = new TimePickerDialog(m_ctx, R.style.DialogTheme, this, m_hour, m_min, true);

            m_tp.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which)
                {
                    if (which == DialogInterface.BUTTON_NEGATIVE)
                    {
                       if(isCbxClicked)
                           m_cbx.setChecked(false);
                    }
                }
            });
        }

        /**
         * void show()
         */
        void show(){
            m_tp.show();
        }


        /**
         * public void onTimeSet(TimePicker view, int hourOfDay, int minute)
         * @param view
         * @param hourOfDay
         * @param minute
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String s = "";

            if(hourOfDay < 10)
                s += '0';
            s += hourOfDay;

            s += ':';

            if (minute < 10)
                s += '0';
            s += minute;

            m_tvw.setText(s);
            m_cbx.setChecked(true);
        }

    }
}
