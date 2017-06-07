package com.paymentcomponents.apis.gateway.controller

import com.paymentcomponents.apis.gateway.service.DirectDebitService
import com.paymentcomponents.common.request.DirectDebitRequest
import com.paymentcomponents.common.response.Error
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by aalexandrakis on 26/04/2017.
 */
@RestController
class DirectDebitController {

    private RestTemplate restTemplate
    private DirectDebitService directDebitService

    @Autowired
    DirectDebitController(RestTemplate restTemplate, DirectDebitService directDebitService) {
        this.restTemplate = restTemplate
        this.directDebitService = directDebitService
    }


    @RequestMapping(value = "/v1/direct/debit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "initiateDirectDebit", notes = "Credit transfer initiation")
    @ApiResponses([
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    ])
    @ResponseStatus(HttpStatus.CREATED)
    public void initiateDirectDebit(
            @RequestBody DirectDebitRequest directDebitRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        directDebitService.initiateDirectDebit(directDebitRequest, httpServletRequest.servletPath)
    }

    @RequestMapping(value = "/v1/direct/debit/by-request-id", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "getCreditTransferByRequestId", notes = "Get credit transfer by date")
    @ApiResponses([
            @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    ])
    public def getCreditTransferByRequestId(
            @RequestParam("requestId") String requestId, HttpServletRequest httpServletRequest) {
        directDebitService.getCreditTransferByRequestId(requestId, httpServletRequest.servletPath)
    }

    @RequestMapping(value = "/v1/direct/debit/by-date", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "getCreditTransfersByDate", notes = "Get credit transfer by date")
    @ApiResponses([
            @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    ])
    public def getCreditTransfersByDate(
            @RequestParam("date") String creationDateTime, HttpServletRequest httpServletRequest) {
        directDebitService.getCreditTransfersByDate(creationDateTime, httpServletRequest.servletPath)
    }

}
