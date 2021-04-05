package com.limetac.scanner.data.model;

import java.util.List;

public class PkgDetails {

    long Id;
    String code;
    int status;
    List<String> tags;
    long packingItemId;
    long bindId;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getPackingItemId() {
        return packingItemId;
    }

    public void setPackingItemId(long packingItemId) {
        this.packingItemId = packingItemId;
    }

    public long getBindId() {
        return bindId;
    }

    public void setBindId(long bindId) {
        this.bindId = bindId;
    }
}
