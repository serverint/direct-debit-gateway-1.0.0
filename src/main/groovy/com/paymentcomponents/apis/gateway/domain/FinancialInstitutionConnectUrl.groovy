package com.paymentcomponents.apis.gateway.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

/**
 * Created by t.logothetis on 5/18/2017.
 */
@RedisHash("financialInstitution_connectUrl")
class FinancialInstitutionConnectUrl {
    private @Id String centralBankCode
    private String waspConnectUrl

    FinancialInstitutionConnectUrl() {
    }

    FinancialInstitutionConnectUrl(String centralBankCode, String waspConnectUrl) {
        this.centralBankCode = centralBankCode
        this.waspConnectUrl = waspConnectUrl
    }


    String getCentralBankCode() {
        return centralBankCode
    }

    void setCentralBankCode(String centralBankCode) {
        this.centralBankCode = centralBankCode
    }

    String getWaspConnectUrl() {
        return waspConnectUrl
    }

    void setWaspConnectUrl(String waspConnectUrl) {
        this.waspConnectUrl = waspConnectUrl
    }


    @Override
    public String toString() {
        return "FinancialInstitutionConnectUrl{" +
                "centralBankCode='" + centralBankCode +
                ", waspConnectUrl='" + waspConnectUrl + '\'' +
                '}';
    }
}
