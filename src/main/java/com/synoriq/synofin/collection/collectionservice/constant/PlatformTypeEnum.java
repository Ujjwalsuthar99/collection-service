package com.synoriq.synofin.collection.collectionservice.constant;

public enum PlatformTypeEnum {
    ANDROID("android"), IOS("ios");

    private String platformType;

    PlatformTypeEnum(String platformType){
        this.platformType = platformType;
    }

    public String getPlatformType(){
        return this.platformType;
    }
}
