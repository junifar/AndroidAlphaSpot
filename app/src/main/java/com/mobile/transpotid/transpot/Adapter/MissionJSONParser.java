package com.mobile.transpotid.transpot.Adapter;

import com.mobile.transpotid.transpot.ChangeFormat.ChangeFormat;
import com.mobile.transpotid.transpot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by owner on 2/22/2016.
 */
public class MissionJSONParser {

    public List<HashMap<String,Object>> getMissions(JSONArray jMissions) {
        int missionCount = jMissions.length();
        List<HashMap<String, Object>> missionList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mission = null;

        for (int i=0; i<missionCount; i++){
            try{
                mission = getMission((JSONObject) jMissions.get(i));
                missionList.add(mission);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return missionList;
    }

    private HashMap<String, Object> getMission(JSONObject jMission) {
        HashMap<String, Object> mission = new HashMap<String, Object>();
        String id = "";
        String name = "";
        String description = "";
        String iteration = "";
        String photo_url = "";
        String to_date = "";
        String from_date = "";
        Double price = null;

        try{
            id = jMission.getString("id");
            name = jMission.getString("brand");
            description = jMission.getString("name");
            iteration = jMission.getString("iteration");
            photo_url = jMission.getString("photo_url");
            to_date = jMission.getString("to_date");
            from_date = jMission.getString("from_date");
            price = jMission.getDouble("price");


            String periods = ChangeFormat.formatDate(from_date) + " - " + ChangeFormat.formatDate(to_date);
            String harga = ChangeFormat.formatNumber(price);
            String agent = iteration + "/50";

            mission.put("pk",id);
            mission.put("name",name);
            mission.put("description",description);
            mission.put("photo_url", R.mipmap.ic_launcher);
            mission.put("photo_url_path",photo_url);
            mission.put("period",periods);
            mission.put("price",harga);
            mission.put("agent",agent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mission;
    }

    public List<HashMap<String,Object>> getMyMissions(JSONArray jMyMissions, String id_user) {
        int mymissionCount = jMyMissions.length();
        List<HashMap<String, Object>> myMissionList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mymission = null;

        for (int i=0; i<mymissionCount; i++){
            try{
                mymission = getMyMission((JSONObject) jMyMissions.get(i));
                if (id_user.equals(mymission.get("member"))){
                    myMissionList.add(mymission);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return myMissionList;
    }

    private HashMap<String, Object> getMyMission(JSONObject jMyMission) {
        HashMap<String, Object> mymission = new HashMap<String, Object>();
        String id = "";
        String name = "";
        String description = "";
        String iteration = "";
        String photo_url = "";
        String to_date = "";
        String from_date = "";
        Double price = null;
        String member = "";
        String member_name = "";
        String status = "";

        try{
            id = jMyMission.getString("id");
            name = jMyMission.getString("brand");
            description = jMyMission.getString("name");
            iteration = jMyMission.getString("iteration");
            photo_url = jMyMission.getString("photo_url");
            to_date = jMyMission.getString("to_date");
            from_date = jMyMission.getString("from_date");
            price = jMyMission.getDouble("price");
            member = jMyMission.getString("member");
            member_name = jMyMission.getString("member_name");
            status = jMyMission.getString("mission_status");

            String periods = ChangeFormat.formatDate(from_date) + " - " + ChangeFormat.formatDate(to_date);
            String harga = ChangeFormat.formatNumber(price);
            String agent = iteration + "/50";

            mymission.put("pk",id);
            mymission.put("name",name);
            mymission.put("description",description);
            mymission.put("photo_url", R.mipmap.ic_launcher);
            mymission.put("photo_url_path",photo_url);
            mymission.put("period",periods);
            mymission.put("price",harga);
            mymission.put("agent",agent);
            mymission.put("member",member);
            mymission.put("member_name",member_name);
            mymission.put("status",status);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mymission;
    }
}
