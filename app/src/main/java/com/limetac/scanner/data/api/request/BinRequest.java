package com.limetac.scanner.data.api.request;

import com.limetac.scanner.data.model.BinTag;

import java.util.List;

public class BinRequest {
    String locationCode;
    List<BinTag> tagList;
    String username = "eventreviewer1";
    String password = "!5353ns";

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public List<BinTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<BinTag> tagList) {
        this.tagList = tagList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
