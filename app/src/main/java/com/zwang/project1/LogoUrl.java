package com.zwang.project1;

import com.google.gson.annotations.SerializedName;

public class LogoUrl {
    @SerializedName("name")
    private String name = "";
    @SerializedName("domain")
    private String domain = "";
    @SerializedName("logo")
    private String logoUrl = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
