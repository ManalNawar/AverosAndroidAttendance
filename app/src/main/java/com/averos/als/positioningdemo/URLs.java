package com.averos.als.positioningdemo;

public class URLs {
    private static final String ROOT_URL = "https://attend.itjed.com/api/";

    //MSAL URLs
    public static final String CLIENT_ID = "cf3a6b35-96d8-40c4-956a-1e4ef077bbdf";
    public static final String SCOPES [] = {"https://graph.microsoft.com/User.Read"};
    public static final String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";
    // Attendance URLs
    public static final String URL_TOKEN = ROOT_URL + "token";
    public static final String URL_ATTENDANCE_SUBMIT = ROOT_URL + "attendance/";
    public static final String URL_BLOCKS = ROOT_URL + "blocks/user/";
    public static final String URL_ATTENDANCE = ROOT_URL + "attendance/user/";
    public static final String API_APP_KEY = "3D3679B75DF6F1987AB688C3B4493A97B908FB3F454B36D93FCDDC2CEC34DE84";
}
