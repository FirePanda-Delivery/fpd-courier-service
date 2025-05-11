package ru.diplom.fpd.courier.configuration;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.spring.starter.remote.InfinispanRemoteCacheCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.diplom.fpd.courier.configuration.property.ActiveCourierCacheProperties;
import ru.diplom.fpd.courier.configuration.property.CourierKafkaProperties;
import ru.diplom.fpd.courier.feign.UserApi;
import ru.diplom.fpd.courier.model.cache.ActiveCourierLocation;


@Configuration
@EnableConfigurationProperties({CourierKafkaProperties.class, ActiveCourierCacheProperties.class})
@EnableFeignClients(clients = {UserApi.class})
@RequiredArgsConstructor
@EnableScheduling
public class ApplicationConfig {

    public static final String ACTIVE_COURIER_LOCATION_CACHE_NAME = "ACTIVE_COURIER_LOCATION_CACHE_NAME";
    @Lazy
    private final ActiveCourierCacheProperties activeCouriersCacheProperties;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Bean(name = "yandexMapsRestTemplate")
    public RestTemplate createYMapsRestTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @SneakyThrows
    public InfinispanRemoteCacheCustomizer caches() {
        return builder -> {
            try {
                builder.remoteCache(activeCouriersCacheProperties.getCacheName())
                        .configurationURI(activeCouriersCacheProperties.getConfigurationFile().getURI());
                builder.remoteCache(activeCouriersCacheProperties.getCacheName()).marshaller(ProtoStreamMarshaller.class);
                builder.addContextInitializer(new ActiveCourierLocationSchemaBuilderImpl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean(ACTIVE_COURIER_LOCATION_CACHE_NAME)
    public RemoteCache<Long, ActiveCourierLocation> activeCourierLocationCache(RemoteCacheManager remoteCacheManager) {
        RemoteCache<String, String> metadataCache =
                remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
        GeneratedSchema schema = new ActiveCourierLocationSchemaBuilderImpl();
        metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

        return remoteCacheManager.getCache(activeCouriersCacheProperties.getCacheName());
    }

}