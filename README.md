# WASP Switch APIs GATEWAY
## Installation instructions
Build a docker image from [here](./Dockerfile)
```
docker build -t wasp/direct-debit-gateway .
```
Start a docker container from the image
```
docker run -it -d -p 9026:8080 \
-v /etc/timezone:/etc/timezone \
-v /home/teamcity/apps_properties/wasp_direct_debit_gateway:/properties/ \
-v /home/teamcity/apps_logs/wasp_direct_debit_gateway:/var/log/ \
--name wasp-direct-debit-gateway wasp/direct-debit-gateway
```
Default properties are the following. If you want to override some or all default properties create a property file with the parameters you want to override and run the java jar with this environment property ```--spring.config.location=/properties/application.properties```.
```
spring.application.name=direct-debit-gateway
server.port=9026

#Kafka
spring.cloud.stream.bindings.logs.destination=logs
spring.cloud.stream.bindings.logs.contentType=application/json
spring.cloud.stream.bindings.direct-debits.destination=direct-debits
spring.cloud.stream.bindings.direct-debits.contentType=application/json
spring.cloud.stream.kafka.binder.brokers=192.168.101.6
spring.cloud.stream.kafka.binder.zkNodes=192.168.101.6
spring.cloud.stream.kafka.binder.autoAddPartitions=true

#Registry
eureka.client.service-url.defaultZone= http://192.168.101.6:9010/eureka/
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=192.168.101.6 //docker's ip
eureka.instance.non-secure-port=9022 // docker's mapped port
eureka.instance.metadataMap.instanceId=${spring.application.name}:9022

#Zipkin
spring.zipkin.enabled=true
spring.zipkin.baseUrl=http://192.168.101.6:9012

#Logging
logging.level.root=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=INFO

#Filtering
ignore.jat.filter.paths=/v2/api-docs,/swagger.*,/.*.css,/.*.ico,/webjars/.*

#Redis connection properties
spring.redis.host=192.168.101.6
spring.redis.port=6379
spring.redis.password=redispassword
```
## API docs
[API docs in Swagger](http://192.168.101.6:9022/swagger-ui.html)