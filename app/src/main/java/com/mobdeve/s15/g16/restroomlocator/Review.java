package com.mobdeve.s15.g16.restroomlocator;

import com.google.firebase.firestore.DocumentId;

public class Review {
    @DocumentId
    private String id;
    private String userId;
    private String restroomId;
    private String startTime;
    private String endTime;
    private String fee;
    private String imageUri1;
    private String imageUri2;
    private String imageUri3;
    private String remarks;

    public Review() {

    }

    public Review(String userId, String restroomId, String startTime, String endTime, String fee, String imageUri1, String imageUri2, String imageUri3, String remarks) {
        this.userId = userId;
        this.restroomId = restroomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.imageUri1 = imageUri1;
        this.imageUri2 = imageUri2;
        this.imageUri3 = imageUri3;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestroomId() {
        return restroomId;
    }

    public void setRestroomId(String restroomId) {
        this.restroomId = restroomId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getImageUri1() {
        return imageUri1;
    }

    public void setImageUri1(String imageUri1) {
        this.imageUri1 = imageUri1;
    }

    public String getImageUri2() {
        return imageUri2;
    }

    public void setImageUri2(String imageUri2) {
        this.imageUri2 = imageUri2;
    }

    public String getImageUri3() {
        return imageUri3;
    }

    public void setImageUri3(String imageUri3) {
        this.imageUri3 = imageUri3;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
