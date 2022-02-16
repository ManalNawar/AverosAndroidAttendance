package com.averos.als.positioningdemo;

public class AttendanceList {

    private String beacon , created_at, blockTitle;


    public AttendanceList(String beacon, String created_at){
        this.beacon = beacon;
        this.created_at = created_at;
        this.blockTitle = blockTitle;

    }

    public AttendanceList() {
    }

    public String getBlockTitle() {
        return blockTitle;
    }

    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

    public String getBeacon() {
        return beacon;
    }

    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
