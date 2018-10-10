package com.example.hahahaha.qrcodeadmin;

public class XYValue {

    private double x;
    private double y;
    private String qr_code;
    private String times;

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public XYValue(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public XYValue(String qr_code,String times){
        this.qr_code=qr_code;
        this.times=times;

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}