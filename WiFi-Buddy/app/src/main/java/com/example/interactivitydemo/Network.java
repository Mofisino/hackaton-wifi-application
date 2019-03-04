package com.example.interactivitydemo;

public class Network {
    public String ssid;
    public String pass;
    public String latitute;
    public String longitute;

    public Network() {

    }
    public Network(String ssid, String pass, String latitute, String longitute) {
        this.ssid = ssid;
        this.pass = pass;
        this.latitute = latitute;
        this.longitute = longitute;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitute() {
        return longitute;
    }

    public void setLongitute(String longitute) {
        this.longitute = longitute;
    }
}
