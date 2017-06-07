package com.paymentcomponents.apis.gateway.exceptions
/**
 * Created by aalexandrakis on 25/04/2017.
 */
class WaspApiValidationException extends Exception {
    private String errorCode
    private String errorDescription

    WaspApiValidationException(String errorCode, String errorDescription) {
        super()
        this.errorCode = errorCode
        this.errorDescription = errorDescription
    }

    public String getErrorCode() {
        return errorCode
    }

    public WaspApiValidationException setErrorCode(String errorCode) {
        this.errorCode = errorCode
        return this
    }

    String getErrorDescription() {
        return errorDescription
    }

    void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription
    }
}