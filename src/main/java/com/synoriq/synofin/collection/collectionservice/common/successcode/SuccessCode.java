package com.synoriq.synofin.collection.collectionservice.common.successcode;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum SuccessCode {
    DATA_SAVE_SUCCESS(1017000, "Record Saved Successfully"),
        DATA_UPDATED_SUCESS(1017001, "Record Updated Successfully");

    private Integer codeValue;
    private String responseMessage;


    SuccessCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.responseMessage = message;
    }

    public Integer getCodeValue() {
        return this.codeValue;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    @Override
    public String toString() {
        String var10000 = this.name();
        return var10000 + "(" + this.getCodeValue() + ")";
    }

    private static final Map<Integer, SuccessCode> integerSuccessCodeMap;

    static {
        integerSuccessCodeMap = Maps.uniqueIndex(Arrays.asList(SuccessCode.values()), SuccessCode::getSuccessCodeValue);
    }


    public static SuccessCode getSuccessCode(Integer codeValue) throws IllegalArgumentException {
        return integerSuccessCodeMap.get(codeValue);
    }

    public Integer getSuccessCodeValue() {
        return this.codeValue;
    }

}
