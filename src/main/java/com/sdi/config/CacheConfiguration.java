package com.sdi.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.sdi.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.sdi.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.sdi.domain.User.class.getName());
            createCache(cm, com.sdi.domain.Authority.class.getName());
            createCache(cm, com.sdi.domain.User.class.getName() + ".authorities");
            createCache(cm, com.sdi.domain.DeployementType.class.getName());
            createCache(cm, com.sdi.domain.DeployementType.class.getName() + ".productDeployementDetails");
            createCache(cm, com.sdi.domain.CustomisationLevel.class.getName());
            createCache(cm, com.sdi.domain.CustomisationLevel.class.getName() + ".requestOfChanges");
            createCache(cm, com.sdi.domain.ProductVersion.class.getName());
            createCache(cm, com.sdi.domain.ProductVersion.class.getName() + ".productDeployementDetails");
            createCache(cm, com.sdi.domain.ProductVersion.class.getName() + ".productVersions");
            createCache(cm, com.sdi.domain.ProductVersion.class.getName() + ".moduleVersions");
            createCache(cm, com.sdi.domain.ProductVersion.class.getName() + ".infraComponentVersions");
            createCache(cm, com.sdi.domain.Product.class.getName());
            createCache(cm, com.sdi.domain.Product.class.getName() + ".productLines");
            createCache(cm, com.sdi.domain.ProductLine.class.getName());
            createCache(cm, com.sdi.domain.ProductLine.class.getName() + ".products");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName());
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".moduleDeployements");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".moduleVersions");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".features");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".productVersions");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".productDeployementDetails");
            createCache(cm, com.sdi.domain.ModuleVersion.class.getName() + ".requestOfChanges");
            createCache(cm, com.sdi.domain.Module.class.getName());
            createCache(cm, com.sdi.domain.Domaine.class.getName());
            createCache(cm, com.sdi.domain.Domaine.class.getName() + ".moduleVersions");
            createCache(cm, com.sdi.domain.Feature.class.getName());
            createCache(cm, com.sdi.domain.Feature.class.getName() + ".featureDeployements");
            createCache(cm, com.sdi.domain.Feature.class.getName() + ".moduleVersions");
            createCache(cm, com.sdi.domain.ProductDeployement.class.getName());
            createCache(cm, com.sdi.domain.ProductDeployementDetail.class.getName());
            createCache(cm, com.sdi.domain.ProductDeployementDetail.class.getName() + ".moduleDeployements");
            createCache(cm, com.sdi.domain.ProductDeployementDetail.class.getName() + ".infraComponentVersions");
            createCache(cm, com.sdi.domain.ProductDeployementDetail.class.getName() + ".allowedModuleVersions");
            createCache(cm, com.sdi.domain.ModuleDeployement.class.getName());
            createCache(cm, com.sdi.domain.ModuleDeployement.class.getName() + ".featureDeployements");
            createCache(cm, com.sdi.domain.FeatureDeployement.class.getName());
            createCache(cm, com.sdi.domain.Region.class.getName());
            createCache(cm, com.sdi.domain.Region.class.getName() + ".countries");
            createCache(cm, com.sdi.domain.ClientCertification.class.getName());
            createCache(cm, com.sdi.domain.ClientSize.class.getName());
            createCache(cm, com.sdi.domain.ClientSize.class.getName() + ".clients");
            createCache(cm, com.sdi.domain.Client.class.getName());
            createCache(cm, com.sdi.domain.Client.class.getName() + ".productDeployements");
            createCache(cm, com.sdi.domain.Client.class.getName() + ".certifs");
            createCache(cm, com.sdi.domain.ClientType.class.getName());
            createCache(cm, com.sdi.domain.ClientType.class.getName() + ".clients");
            createCache(cm, com.sdi.domain.Certification.class.getName());
            createCache(cm, com.sdi.domain.Certification.class.getName() + ".clientCertifications");
            createCache(cm, com.sdi.domain.ClientEvent.class.getName());
            createCache(cm, com.sdi.domain.ClientEventType.class.getName());
            createCache(cm, com.sdi.domain.ClientEventType.class.getName() + ".clientEvents");
            createCache(cm, com.sdi.domain.Country.class.getName());
            createCache(cm, com.sdi.domain.Country.class.getName() + ".clients");
            createCache(cm, com.sdi.domain.HA.class.getName());
            createCache(cm, com.sdi.domain.HA.class.getName() + ".productVersions");
            createCache(cm, com.sdi.domain.ComponentType.class.getName());
            createCache(cm, com.sdi.domain.ComponentType.class.getName() + ".infraComponents");
            createCache(cm, com.sdi.domain.InfraComponent.class.getName());
            createCache(cm, com.sdi.domain.InfraComponentVersion.class.getName());
            createCache(cm, com.sdi.domain.InfraComponentVersion.class.getName() + ".productVersions");
            createCache(cm, com.sdi.domain.InfraComponentVersion.class.getName() + ".productDeployementDetails");
            createCache(cm, com.sdi.domain.RequestOfChange.class.getName());
            createCache(cm, com.sdi.domain.RequestOfChange.class.getName() + ".moduleVersions");
            createCache(cm, com.sdi.domain.Product.class.getName() + ".modules");
            createCache(cm, com.sdi.domain.Product.class.getName() + ".infraComponentVersions");
            createCache(cm, com.sdi.domain.Module.class.getName() + ".products");
            createCache(cm, com.sdi.domain.InfraComponentVersion.class.getName() + ".products");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
