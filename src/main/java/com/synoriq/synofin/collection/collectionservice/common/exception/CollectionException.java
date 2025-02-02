package com.synoriq.synofin.collection.collectionservice.common.exception;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;

public class CollectionException extends RuntimeException{

    public final ErrorCode errorCode;


    public CollectionException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public CollectionException(ErrorCode errorCode, Integer errCode){
        super(errCode.toString());
        this.errorCode = errorCode;
    }

    public CollectionException(ErrorCode errorCode, Exception ex) {
        super(ex.toString(), ex);
        this.errorCode = errorCode;
    }
}
