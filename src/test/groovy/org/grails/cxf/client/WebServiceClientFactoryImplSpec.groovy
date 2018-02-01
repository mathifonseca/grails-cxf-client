package org.grails.cxf.client

import mock.SimpleServicePortType
import org.apache.cxf.configuration.security.AuthorizationPolicy
import org.apache.cxf.interceptor.LoggingInInterceptor
import org.apache.cxf.interceptor.LoggingOutInterceptor
import org.apache.cxf.transports.http.configuration.ConnectionType
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy
import org.grails.cxf.client.exception.UpdateServiceEndpointException
import spock.lang.Specification

class WebServiceClientFactoryImplSpec extends Specification {

	def "create web service client using factory method"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when:
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()],
				[new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()],
				null,
				null,
				"http://schemas.xmlsoap.org/wsdl/soap12/",
				false,
				CxfClientConstants.SSL_PROTOCOL_SSLV3,
				null,
				[disableCNCheck: false, sslCacheTimeout: 60])

		then:
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		factory.interfaceMap.get("testService").clientPolicyMap.connection == ConnectionType.CLOSE
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		factory.interfaceMap.get("testService").handler != null
		!factory.interfaceMap.get("testService").httpClientPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		factory.interfaceMap.get("testService").tlsClientParameters.disableCNCheck == false
		factory.interfaceMap.get("testService").tlsClientParameters.sslCacheTimeout == 60

	}

	def "create web service client using factory method with http client policy"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when:
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8', connection: ConnectionType.KEEP_ALIVE],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], new HTTPClientPolicy(connectionTimeout: 10, receiveTimeout: 20), new AuthorizationPolicy(userName: 'user', password: 'pass'), "", false, "",
				[:],
				[:])

		then:
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").inFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").httpClientPolicy != null
		factory.interfaceMap.get("testService").httpClientPolicy.connectionTimeout == 10
		factory.interfaceMap.get("testService").httpClientPolicy.receiveTimeout == 20
		factory.interfaceMap.get("testService").httpClientPolicy.allowChunking
		factory.interfaceMap.get("testService").authorizationPolicy.userName == 'user'
		factory.interfaceMap.get("testService").authorizationPolicy.password == 'pass'
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		factory.interfaceMap.get("testService").clientPolicyMap.connection == ConnectionType.KEEP_ALIVE
		!factory.interfaceMap.get("testService").proxyFactoryBindingId
		!factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").requestContext
		!factory.interfaceMap.get("testService").tlsClientParameters
	}

	def "create web service client using factory method with http client policy no chunking"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when:
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType, "testService", "http://localhost:8080/cxf-client", false, [receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'], [new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()], [new CxfClientFaultConverter()], new HTTPClientPolicy(connectionTimeout: 10, receiveTimeout: 20, allowChunking: false),
				new AuthorizationPolicy(userName: 'user', password: 'pass'),
				null, null, null, null, null)

		then:
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").httpClientPolicy != null
		factory.interfaceMap.get("testService").httpClientPolicy.connectionTimeout == 10
		factory.interfaceMap.get("testService").httpClientPolicy.receiveTimeout == 20
		factory.interfaceMap.get("testService").authorizationPolicy.userName == 'user'
		factory.interfaceMap.get("testService").authorizationPolicy.password == 'pass'
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		factory.interfaceMap.get("testService").clientPolicyMap.connection == ConnectionType.CLOSE
		!factory.interfaceMap.get("testService").httpClientPolicy.allowChunking
		!factory.interfaceMap.get("testService").proxyFactoryBindingId
		!factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").requestContext
		!factory.interfaceMap.get("testService").tlsClientParameters
	}

	def "create web service client using factory method and timeouts"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when:
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client", false,
				[receiveTimeout: 1, connectionTimeout: 2, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()],
				[new CxfClientFaultConverter()], [new CxfClientFaultConverter()], null, null, "", null, "", [:], [:])

		then:
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").inFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 2
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 1
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		factory.interfaceMap.get("testService").handler != null
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").authorizationPolicy
		!factory.interfaceMap.get("testService").proxyFactoryBindingId
		!factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").requestContext
		!factory.interfaceMap.get("testService").tlsClientParameters
	}

	def "create web service client using factory method with different content type"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when:
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType, "testService", "http://localhost:8080/cxf-client",
				false, [receiveTimeout: 1, connectionTimeout: 2, allowChunking: false, contentType: 'application/soap+xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], null, null, "", null, "", [:], [:])

		then:
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").inFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 2
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 1
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'application/soap+xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		factory.interfaceMap.get("testService").handler != null
		!factory.interfaceMap.get("testService").authorizationPolicy
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").proxyFactoryBindingId
		!factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").requestContext
		!factory.interfaceMap.get("testService").tlsClientParameters
	}

	def "create web service client using factory method and change url"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when: "we create an initial service"
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client/old", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], null, null, "", null, "",
				["one": "one"], [:])

		then: "we should have some stuff hooked up here"
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("one") == "one"
		factory.interfaceMap.get("testService").requestContext.get("one") == "one"
		!factory.interfaceMap.get("testService").tlsClientParameters
		!factory.interfaceMap.get("testService").authorizationPolicy
		!factory.interfaceMap.get("testService").mtomEnabled

		when: "change the url to something new"
		factory.updateServiceEndpointAddress("testService", "http://localhost:8080/cxf-client/new")

		then: "all things should still remain in cache, but the url should have changed"
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").proxyFactoryBindingId
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/new"
		!factory.interfaceMap.get("testService").tlsClientParameters
		!factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").authorizationPolicy
	}

	def "create web service client using factory method and change url on invalid name"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when: "we create an initial service"
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client/old", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], null, null, "http://schemas.xmlsoap.org/wsdl/soap12/", true, "",
				[:], [:])

		then: "we should have some stuff hooked up here"
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		!factory.interfaceMap.get("testService").authorizationPolicy
		!factory.interfaceMap.get("testService").httpClientPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").tlsClientParameters
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"

		when: "change the url to something new using invalid name"
		factory.updateServiceEndpointAddress("unknownService", "http://localhost:8080/cxf-client/new")

		then: "all things should still remain in cache and the url should not have changed and exception should be thrown"
		UpdateServiceEndpointException exception = thrown()
		exception.message.contains("Must provide a service name")
		!factory.interfaceMap.containsKey("unknownService")
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").authorizationPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").tlsClientParameters
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"
	}

	def "create web service client using factory method and change url on empty name"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when: "we create an initial service"
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client/old", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], null, null, "http://schemas.xmlsoap.org/wsdl/soap12/", true, "", [:], [:])

		then: "we should have some stuff hooked up here"
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").authorizationPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").tlsClientParameters
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"

		when: "change the url to something new using invalid name"
		factory.updateServiceEndpointAddress('', "http://localhost:8080/cxf-client/new")

		then: "all things should still remain in cache and the url should not have changed and exception should be thrown"
		UpdateServiceEndpointException exception = thrown()
		exception.message.contains("Must provide a service name")
		!factory.interfaceMap.containsKey("unknownService")
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").authorizationPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		factory.interfaceMap.get("testService").mtomEnabled
		!factory.interfaceMap.get("testService").tlsClientParameters
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"
	}

	def "create web service client using factory method and retrieve url"() {
		given:
		WebServiceClientFactory webServiceClientFactory = new WebServiceClientFactoryImpl()
		String serviceName = 'testService'
		String serviceEndpointAddress = 'http://localhost:8080/cxf-client'

		when: "create an initial service"
		webServiceClientFactory.getWebServiceClient(null, null, null, SimpleServicePortType, serviceName, serviceEndpointAddress, false, [receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'], [new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()], [new CxfClientFaultConverter()], null, null, "http://schemas.xmlsoap.org/wsdl/soap12/", false, "", [:], [:])

		then: "can retrieve the service endpoint address"
		webServiceClientFactory.getServiceEndpointAddress(serviceName) == serviceEndpointAddress
	}

	def "test the settings map when speificying the protocol via tlsClientParameters"() {
		given:
		WebServiceClientFactoryImpl factory = new WebServiceClientFactoryImpl()

		when: "we create an initial service"
		Object webServiceClient = factory.getWebServiceClient(null, null, null, SimpleServicePortType,
				"testService", "http://localhost:8080/cxf-client/old", false,
				[receiveTimeout: 0, connectionTimeout: 0, allowChunking: false, contentType: 'text/xml; charset=UTF-8'],
				[new LoggingOutInterceptor()], [new LoggingInInterceptor()], [new CxfClientFaultConverter()],
				[new CxfClientFaultConverter()], null, null, "http://schemas.xmlsoap.org/wsdl/soap12/", false, null, [:],
				[disableCNCheck      : true,
				 sslCacheTimeout     : 100,
				 cipherSuitesFilter  : [include: ['.*_EXPORT_.*', '.*_EXPORT1024_.*'], exclude: ['.*_DH_anon_.*']]
				])

		then: "we should have some stuff hooked up here"
		webServiceClient != null
		factory.interfaceMap.containsKey("testService")
		factory.interfaceMap.get("testService").clientInterface == SimpleServicePortType
		factory.interfaceMap.get("testService").inInterceptors instanceof List
		factory.interfaceMap.get("testService").outInterceptors instanceof List
		factory.interfaceMap.get("testService").outFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inFaultInterceptors instanceof List
		factory.interfaceMap.get("testService").inInterceptors.size() > 0
		factory.interfaceMap.get("testService").outInterceptors.size() > 0
		factory.interfaceMap.get("testService").outFaultInterceptors.size() > 0
		factory.interfaceMap.get("testService").clientPolicyMap.connectionTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.receiveTimeout == 0
		factory.interfaceMap.get("testService").clientPolicyMap.contentType == 'text/xml; charset=UTF-8'
		!factory.interfaceMap.get("testService").clientPolicyMap.allowChunking
		!factory.interfaceMap.get("testService").httpClientPolicy
		!factory.interfaceMap.get("testService").authorizationPolicy
		factory.interfaceMap.get("testService").proxyFactoryBindingId == "http://schemas.xmlsoap.org/wsdl/soap12/"
		!factory.interfaceMap.get("testService").mtomEnabled
		factory.interfaceMap.get("testService").tlsClientParameters.disableCNCheck == true
		factory.interfaceMap.get("testService").tlsClientParameters.sslCacheTimeout == 100
		factory.interfaceMap.get("testService").tlsClientParameters.secureSocketProtocol == CxfClientConstants.SSL_PROTOCOL_SSLV3
		factory.interfaceMap.get("testService").tlsClientParameters.cipherSuitesFilter.include == ['.*_EXPORT_.*', '.*_EXPORT1024_.*']
		factory.interfaceMap.get("testService").tlsClientParameters.cipherSuitesFilter.exclude == ['.*_DH_anon_.*']
		factory.interfaceMap.get("testService").handler != null
		factory.interfaceMap.get("testService").handler.cxfProxy.h.client.currentRequestContext.get("org.apache.cxf.message.Message.ENDPOINT_ADDRESS") == "http://localhost:8080/cxf-client/old"
	}
}
