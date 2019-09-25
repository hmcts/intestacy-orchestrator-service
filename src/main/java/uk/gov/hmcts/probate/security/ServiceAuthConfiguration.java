package uk.gov.hmcts.probate.security;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Configuration
public class ServiceAuthConfiguration {

    @Bean    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(@Value("${auth.provider.service.client.baseUrl}") String s2sUrl,
                                                            //    @Value("${s2s.auth.totp.secret}") String secret,
                                                               @Value("${auth.provider.service.client.key}") String secret,
                                                               @Value("${auth.provider.service.client.microservice}") String microservice) {
        log.info("${auth.provider.service.client.key}) String secret", secret);
        log.info("${auth.provider.service.client.baseUrl}) String s2sUrl", s2sUrl);
        log.info("${auth.provider.service.client.microservice}) String microservice", microservice);                                                        
        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
                .encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, s2sUrl);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }
}
