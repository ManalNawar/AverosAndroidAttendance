package com.averos.als.positioningdemo;

public class Users {
    private String email,name,token;


//    public Users(String string, String sharedPreferencesString, String preferencesString) {
//    }

    public Users() {}
    public Users(String name, String email,String token) {
        this.name = name;
        this.email = email;
        this.token = token;
    }


    public Users(String token) {
        this.token = token;
    }


    public static Users bloktitle(String blockTitle) {
        return new Users(blockTitle);

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }
}
