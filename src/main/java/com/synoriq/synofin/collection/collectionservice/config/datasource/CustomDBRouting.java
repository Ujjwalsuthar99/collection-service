package com.synoriq.synofin.collection.collectionservice.config.datasource;

import com.synoriq.synofin.collection.collectionservice.service.CurrentUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class CustomDBRouting extends AbstractRoutingDataSource {

    @Autowired
    CurrentUserInfo currentUserInfo;

    @Nullable
    private Map<Object, Object> targetDataSources;

    @Nullable
    private Object defaultTargetDataSource;

    @Nullable
    private Map<Object, DataSource> resolvedDataSources;

    @Nullable
    private DataSource resolvedDefaultDataSource;

    private boolean lenientFallback = true;

    @Resource
    public Environment env;

    @Override
    protected Object determineCurrentLookupKey() {
        log.info("Current determineCurrentLookupKey Function Running: {}", currentUserInfo.getClientId());
        return currentUserInfo.getClientId() != null ? currentUserInfo.getClientId() : "finova";
    }


    @Override
    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    @Override
    public Map<Object, DataSource> getResolvedDataSources() {
        Assert.state(this.resolvedDataSources != null, "DataSources not resolved yet - call afterPropertiesSet");
        return Collections.unmodifiableMap(this.resolvedDataSources);
    }

    @Override
    public DataSource getResolvedDefaultDataSource() {
        return this.resolvedDefaultDataSource;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = this.determineCurrentLookupKey();
        DataSource dataSource = (DataSource) this.resolvedDataSources.get(lookupKey);
        if (lookupKey != null && !lookupKey.equals("null") && dataSource == null) {
            targetDataSources = new DBInitialization(env).getDataSourceHashMap(null); //fetching the data sources from DB
            setResolvedDataSources(); //Setting the resolved data sources
            dataSource = (DataSource) this.resolvedDataSources.get(lookupKey);
        }
        if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        } else {
            return dataSource;
        }
    }

    //Setting the Resolved DataSources at restart
    @Override
    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        } else {
            this.resolvedDataSources = CollectionUtils.newHashMap(this.targetDataSources.size());
            this.targetDataSources.forEach((key, value) -> {
                Object lookupKey = this.resolveSpecifiedLookupKey(key);
                DataSource dataSource = this.resolveSpecifiedDataSource(value);
                this.resolvedDataSources.put(lookupKey, dataSource);
            });
            if (this.defaultTargetDataSource != null) {
                this.resolvedDefaultDataSource = this.resolveSpecifiedDataSource(this.defaultTargetDataSource);
            }
        }
    }

    //Setting the DataSource At runtime
    private void setResolvedDataSources() {
        this.targetDataSources.forEach((key, value) -> {
            Object lookupKey = this.resolveSpecifiedLookupKey(key);
            DataSource dataSource = this.resolveSpecifiedDataSource(value);
            this.resolvedDataSources.put(lookupKey, dataSource);
        });
    }
}
