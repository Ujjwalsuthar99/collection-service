//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.synoriq.synofin.collection.collectionservice.rest.request;

public class CollectionActivityLogDTO {
    Long userId;
    Boolean deleted;
    String activityName;
    Double distanceFromUserBranch;
    Object address;
    String remarks;
    Object images;
    Long loanId;
    Object geolocationData;
    Long batteryPercentage;


    public CollectionActivityLogDTO() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public Double getDistanceFromUserBranch() {
        return this.distanceFromUserBranch;
    }

    public Object getAddress() {
        return this.address;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Object getImages() {
        return this.images;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getBatteryPercentage() { return batteryPercentage; }

    public Object getGeolocationData() {
        return this.geolocationData;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    public void setActivityName(final String activityName) {
        this.activityName = activityName;
    }

    public void setDistanceFromUserBranch(final Double distanceFromUserBranch) {
        this.distanceFromUserBranch = distanceFromUserBranch;
    }

    public void setAddress(final Object address) {
        this.address = address;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    public void setImages(final Object images) {
        this.images = images;
    }

    public void setLoanId(final Long loanId) {
        this.loanId = loanId;
    }

    public void setBatteryPercentage(Long batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public void setGeolocationData(final Object geolocationData) {
        this.geolocationData = geolocationData;
    }
}
