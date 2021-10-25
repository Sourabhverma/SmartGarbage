package com.example.smartgarbage;

import java.util.Map;
import java.util.List;

public class BinModal {
    private String _id;
    private String bin_id;
    private String status;
    private List geolocation;
    private String region;
    private Map result;

    public BinModal(Map result) {
//        this.result = result;
//        _id = id;
//        this.bin_id = bin_id;
        getResult();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getBin_id() {
        return bin_id;
    }

    public void setBin_id(String bin_id) {
        this.bin_id = bin_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(List geolocation) {
        this.geolocation = geolocation;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Map getResult() {
        this._id = (String) result.get("_id");
        this.bin_id = String.valueOf(result.get("bin_id"));
        this.status = (String) result.get("status");
        this.geolocation = (List) result.get("geolocation");
        this.region = (String) result.get("region");
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
        this._id = (String) result.get("_id");
        this.bin_id = String.valueOf(result.get("bin_id"));
        this.status = (String) result.get("status");
        this.geolocation = (List) result.get("geolocation");
    }
}
