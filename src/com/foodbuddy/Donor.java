
package com.foodbuddy;

public class Donor {
    private int donorId;
    private String donorName;
    private String phone;

    public Donor(String donorName, String phone) {
        this.donorName = donorName;
        this.phone = phone;
    }

    public Donor(int donorId, String donorName, String phone) {
        this.donorId = donorId;
        this.donorName = donorName;
        this.phone = phone;
    }

    public int getDonorId() {
        return donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
