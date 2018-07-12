package com.mobile.transpotid.transpot.Adapter;

import android.graphics.Bitmap;

/**
 * Created by andro on 12/02/2016.
 */
public class Mission {
    private String pk, campaign, agent, desc, from_date, to_date, price, photo;

    private int iconID;
//    private Bitmap bitmap;


    public Mission(String pk, String campaign, String desc, String agent, String from_date, String to_date, String price, String photo) {
        super();
        this.pk = pk;
        this.campaign = campaign;
        this.agent = agent;
        this.desc = desc;
        this.from_date = from_date;
        this.to_date = to_date;
        this.price = price;
        this.photo = photo;
        this.iconID = iconID;
    }

    public int getIconID() {return iconID;}

    public String getPk() {return pk; }

    public String getCampaign() {return campaign;}

    public String getAgent() {return agent;}

    public String getDesc() {return desc;}

    public String getFrom_date() {return from_date;}

    public String getTo_date() {return to_date;}

    public String getPrice() {return price;}


    public String getPhoto() {
        return photo;
    }
}
