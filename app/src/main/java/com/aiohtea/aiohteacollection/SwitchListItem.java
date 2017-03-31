package com.aiohtea.aiohteacollection;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class SwitchListItem extends DeviceListItem {
    public SwitchListItem(String deviceName, String devicceDesc, int deviceStatus) {
        super(deviceName, devicceDesc, deviceStatus);
    }

    public int getStatusImgRscId(){
        switch (this.m_deviceStatus) {
            case 0:
                return R.mipmap.aiohtea_sw_off;
            case 1:
                return R.mipmap.aiohtea_sw_on;
            default:
                return R.mipmap.aiohtea_sw_offline;
        }
    }

    public String getDeviceStatusText() {
        switch (this.m_deviceStatus) {
            case 0:
                return "STATUS: OFF";
            case 1:
                return "STATUS: ON";
            default:
                return "STATUS: OFFLINE";
        }
    }
}
