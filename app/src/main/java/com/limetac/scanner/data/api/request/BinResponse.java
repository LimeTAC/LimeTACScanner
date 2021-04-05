package com.limetac.scanner.data.api.request;

import com.limetac.scanner.data.model.BinTag;

import java.io.Serializable;
import java.util.List;

public class BinResponse implements Serializable {

    String type;
    long id;
    String code;
    boolean isBin;
    long packingItemId;
    List<BinTag> tagDetails;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isBin() {
        return isBin;
    }

    public void setBin(boolean bin) {
        isBin = bin;
    }

    public long getPackingItemId() {
        return packingItemId;
    }

    public void setPackingItemId(long packingItemId) {
        this.packingItemId = packingItemId;
    }

    public List<BinTag> getTagDetails() {
        return tagDetails;
    }

    public void setTagDetails(List<BinTag> tagDetails) {
        this.tagDetails = tagDetails;
    }
}
