package com.aiohtea.aiohteacollection;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Nguyen Truong on 6/10/2017.
 */

public class MyConfirmDialog implements DialogInterface.OnClickListener {

    static public final int ACTION_CODE_COMMAND_HW = 1;
    static public final int ACTION_CODE_DELETE_DEVICE = 2;

    int m_actionCode = 0;

    MainActivity m_mainActivity;
    DeviceListItem m_device;

    // For ACTION_CODE_COMMAND_HW
    byte[] m_payload;

    // for ACTION_DELETE_DEVICE

    AlertDialog.Builder m_builder;

    /**
     *
     * @param mainActivity
     */
    MyConfirmDialog(MainActivity mainActivity, String msg) {
        m_mainActivity = mainActivity;

        m_builder = new AlertDialog.Builder(m_mainActivity);
        m_builder.setTitle(m_mainActivity.getString(R.string.confirm_dialog_title));
        m_builder.setMessage(msg);
        m_builder.setIcon(R.mipmap.timer_on_button);
        m_builder.setPositiveButton("YES", this);
        m_builder.setNegativeButton("NO", null);
    }

    /**
     *  MyConfirmDialog(MainActivity mainActivity, DeviceListItem device, byte[] payload)
     *  Use this constructor for confirmation of sending command to hardware
     * @param mainActivity
     * @param device
     * @param payload
     */
    MyConfirmDialog(MainActivity mainActivity, String msg, DeviceListItem device, byte[] payload){
        this(mainActivity, msg);

        m_actionCode = this.ACTION_CODE_COMMAND_HW;
        m_device = device;
        m_payload = payload;
    }

    /**
     *
     * @param mainActivity
     * @param msg
     * @param device
     */
    MyConfirmDialog(MainActivity mainActivity, String msg, DeviceListItem device){
        this(mainActivity, msg);

        m_actionCode = this.ACTION_CODE_DELETE_DEVICE;
        m_device = device;
    }



    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (m_actionCode){
            case ACTION_CODE_COMMAND_HW:
                m_device.commandHardware(m_mainActivity, m_payload);
                break;

            case ACTION_CODE_DELETE_DEVICE:
                m_mainActivity.deleteDevice(m_device.getDeviceName());
                break;
        }

        dialog.dismiss();
    }

    /**
     *
     */
    public void show(){
        AlertDialog alert = m_builder.create();
        alert.show();
    }
}
