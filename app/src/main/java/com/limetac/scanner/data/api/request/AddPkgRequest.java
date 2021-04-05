package com.limetac.scanner.data.api.request;

import java.util.List;

public class AddPkgRequest {
    String packageCode;
    List<String> tagCodeList;
    String packingItemId;
    private String username="eventreviewer1";
    private String password="!5353ns";

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public List<String> getTagCodeList() {
        return tagCodeList;
    }

    public void setTagCodeList(List<String> tagCodeList) {
        this.tagCodeList = tagCodeList;
    }

    public String getPackingItemId() {
        return packingItemId;
    }

    public void setPackingItemId(String packingItemId) {
        this.packingItemId = packingItemId;
    }
}
