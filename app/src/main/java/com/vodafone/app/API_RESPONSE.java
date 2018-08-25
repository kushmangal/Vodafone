package com.vodafone.app;

import java.util.ArrayList;

public class API_RESPONSE {
    private int status;
    private String msg;
    private ArrayList<Offers_Response> offers;

    ProfileResponse user;
    private long time;
    private int show_title = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<Offers_Response> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offers_Response> offers) {
        this.offers = offers;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getShow_title() {
        return show_title;
    }

    public void setShow_title(int show_title) {
        this.show_title = show_title;
    }

    public ProfileResponse getUser() {
        return user;
    }

    public void setUser(ProfileResponse user) {
        this.user = user;
    }
}
