package com.smart.smart.wordSegment;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//
// Created by dingying on 2019/3/8.
//
public class SegmentResult {
    private static final String TAG = "SegementResult";
    private String origalJson;
    private String[] resultRecognition;
    private String equipment;
    private String action;
    private String value;
    private String mode;


    public static SegmentResult parseJson(String jsonStr) {
        SegmentResult result = new SegmentResult();
        result.setOrigalJson(jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.optJSONArray("items");
            if (jsonArray != null) {
                int size = jsonArray.length();
                String[] res = new String[size];
                for (int i = 0; i < size; i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    res[i] = object.getString("item");
                    String ne = object.optString("ne");
                    if (object.optString("pos").equals("m")) {
                        result.value = res[i];
                        continue;
                    }
                    // Log.i("jsonArrayitem",res[i]);
                    switch (ne) {
                        case "EQUI":
                            // result.equipment = object.optString("item");
                            result.equipment = res[i];
                            break;
                        case "ACT":
                            // result.action = object.optString("item");
                            result.action = res[i];
                            break;
                        case "MODE":
                            // result.mode = object.optString("item");
                            result.mode = res[i];
                            break;
                    }

                }
                if(null==result.equipment){
                    result.equipment = "抱歉:)未能识别出设备";
                }
                result.setResultRecognition(res);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getOrigalJson() {
        return origalJson;
    }

    public String getValue() {
        return value;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getAction() {
        return action;
    }

    public String getMode() {
        return mode;
    }

    public String[] getResultRecognition() {
        return resultRecognition;
    }

    public void  setEquipment(String e){
        this.equipment=e;
    }

    public void  setAction (String a){
        this.action=a;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setOrigalJson(String origalJson) {
        this.origalJson = origalJson;
    }

    public void setResultRecognition(String[] resultRecognition) {
        this.resultRecognition = resultRecognition;
    }

}
