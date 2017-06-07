package com.paymentcomponents.apis.gateway.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.paymentcomponents.apis.gateway.domain.FinancialInstitutionConnectUrl
import com.paymentcomponents.apis.gateway.domain.InMemoryBank
import com.paymentcomponents.apis.gateway.exceptions.WaspApiValidationException
import com.paymentcomponents.apis.gateway.kafka.interfaces.DirectDebitsChannel
import com.paymentcomponents.apis.gateway.repository.FinancialInstitutionConnectUrlRepository
import com.paymentcomponents.apis.gateway.repository.InMemoryBankRepository
import com.paymentcomponents.common.Constants
import com.paymentcomponents.common.models.Bank
import com.paymentcomponents.common.request.DirectDebitRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

/**
 * Created by aalexandrakis on 26/04/2017.
 */
@Service
class DirectDebitService {

    ParameterizedTypeReference<List<DirectDebitRequest>> directListResponse = new ParameterizedTypeReference<List<DirectDebitRequest>>() {
    }

    private RestTemplate restTemplate
    private DirectDebitsChannel directDebitsChannel
    private FinancialInstitutionConnectUrlRepository financialInstitutionConnectUrlRepository

    @Autowired
    DirectDebitService(RestTemplate restTemplate, DirectDebitsChannel directDebitsChannel, FinancialInstitutionConnectUrlRepository financialInstitutionConnectUrlRepository) {
        this.restTemplate = restTemplate
        this.directDebitsChannel = directDebitsChannel
        this.financialInstitutionConnectUrlRepository = financialInstitutionConnectUrlRepository
    }

    public def getDirectDebitByRequestId(String requestId, String context) {
        this.restTemplate.exchange("http://direct-debit-service$context?requestId={requestId}", HttpMethod.GET, null, DirectDebitRequest.class, requestId)?.body
    }

    public def getDirectDebitByDate(String date, String context) {
        this.restTemplate.exchange("http://direct-debit-service$context?date={date}", HttpMethod.GET, null, directListResponse, date)?.body
    }

    public void initiateDirectDebit(DirectDebitRequest directDebitRequest, String context) {

        FinancialInstitutionConnectUrl financialInstitutionConnectUrl = financialInstitutionConnectUrlRepository.findOne(creditTransferRequest.instructedInstitutionCode)

        if (!financialInstitutionConnectUrl) {
            Bank financialInstitution = this.restTemplate.exchange("http://application-service/banks/search/by-central-bank-code?centralBankCode={centralBankCode}", HttpMethod.GET, null, new ParameterizedTypeReference<Bank>() {
            }, directDebitRequest.instructedInstitutionCode)?.body

            if(financialInstitution) {
                financialInstitutionConnectUrl = new FinancialInstitutionConnectUrl(directDebitRequest.instructedInstitutionCode, financialInstitution.waspConnectUrl)
                financialInstitutionConnectUrlRepository.save(financialInstitutionConnectUrl)
            } else {
                throw new WaspApiValidationException(Constants.ERROR_CODES.instructed_bank_not_found.toString(), "Instructed bank $directDebitRequest.instructedInstitutionCode not found")
            }

        }

        restTemplate.exchange("http://direct-debit-service" + context, HttpMethod.POST, new HttpEntity<DirectDebitRequest>(directDebitRequest), new ParameterizedTypeReference<ResponseEntity>() {
        })

        try {
            //TODO standarize expected bank's response body
            ResponseEntity response = restTemplate.exchange(financialInstitutionConnectUrl.waspConnectUrl + context, HttpMethod.POST, new HttpEntity<DirectDebitRequest>(directDebitRequest), new ParameterizedTypeReference<ResponseEntity>() {
            })
            directDebitRequest.bankHttpResponseCode = response.statusCode.toString()
            directDebitsChannel.output().send(MessageBuilder.withPayload(directDebitRequest).build())
        } catch (HttpClientErrorException ex) {
            directDebitRequest.bankHttpResponseCode = ex.statusCode.toString()
            directDebitRequest.bankResponseBody = ex.responseBodyAsString
            directDebitsChannel.output().send(MessageBuilder.withPayload(directDebitRequest).build())
            throw ex
        } catch (Exception ex) {
            directDebitRequest.bankHttpResponseCode = "500"
            directDebitRequest.bankResponseBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString([error: "An error occured trying to forward the request"])
            directDebitsChannel.output().send(MessageBuilder.withPayload(directDebitRequest).build())
            throw new WaspApiValidationException(Constants.ERROR_CODES.request_forward_error.toString(), "An error occured trying to forward the request")
        }
    }


}
