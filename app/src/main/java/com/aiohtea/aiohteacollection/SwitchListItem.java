package com.aiohtea.aiohteacollection;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class SwitchListItem extends DeviceListItem {
    public SwitchListItem(String deviceName, String devicceDesc, int deviceStatus) {
        super(deviceName, devicceDesc, deviceStatus);
    }

    public int getStatusImgRscId(){
        if(this.m_deviceStatus == 0){
            return R.mipmap.aiohtea_sw_off;
        }else {
            return R.mipmap.aiohtea_sw_on;
        }
    }
}
