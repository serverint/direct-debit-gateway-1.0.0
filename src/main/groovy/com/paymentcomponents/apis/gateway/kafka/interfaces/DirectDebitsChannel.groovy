package com.paymentcomponents.apis.gateway.kafka.interfaces

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

/**
 * Created by t.logothetis on 5/18/2017.
 */
public interface DirectDebitsChannel {

    @Output(value = "direct-debit-gateway")
    MessageChannel output()

}
