package com.aiohtea.aiohteacollection;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

abstract public class DeviceListItem {
    protected String m_deviceName;
    protected String m_deviceDesc;
    protected int m_deviceStatus;

    public DeviceListItem(String deviceName, String devicceDesc, int deviceStatus){
        this.m_deviceName = deviceName;
        this.m_deviceDesc = devicceDesc;
        this.m_deviceStatus = deviceStatus;
    }

    public String getDeviceName() {
        return m_deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.m_deviceName = deviceName;
    }

    public String getDeviceDesc() {
        return m_deviceDesc;
    }

    public void setDeviceDesc (String deviceDesc){
        this.m_deviceDesc = deviceDesc;
    }

    public int getDeviceStatus() {
        return m_deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.m_deviceStatus = deviceStatus;
    }

    abstract int getStatusImgRscId();
}
