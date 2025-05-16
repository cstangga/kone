package com.kone.ucp.dto;

import lombok.Data;


public class MemberRequestDTO {
    private String name;
    private String phoneNumber;
    private String desiredArea;
    private String address;
    private String jumin1; // 주민등록번호 앞 6자리
    private String jumin2; // 주민등록번호 뒤 7자리


    public MemberRequestDTO() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDesiredArea() {
        return desiredArea;
    }
    public void setDesiredArea(String desiredArea) {
        this.desiredArea = desiredArea;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getJumin1() {
        return jumin1;
    }
    public void setJumin1(String jumin1) {
        this.jumin1 = jumin1;
    }

    public String getJumin2() {
        return jumin2;
    }
    public void setJumin2(String jumin2) {
        this.jumin2 = jumin2;
    }
}
