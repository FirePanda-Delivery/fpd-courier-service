package ru.diplom.fpd.courier.configuration.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "infinispan.active-couriers")
public class ActiveCourierCacheProperties {
    private String cacheName;
    private Resource configurationFile;
}
