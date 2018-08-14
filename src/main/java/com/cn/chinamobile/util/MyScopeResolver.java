package com.cn.chinamobile.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Created by zh on 2017/7/12.
 * 定义spring管理的实例作用域
 */
public class MyScopeResolver implements ScopeMetadataResolver {
    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition beanDefinition) {
        ScopeMetadata scopeMetadata = new ScopeMetadata();
        scopeMetadata.setScopedProxyMode(ScopedProxyMode.NO);
        scopeMetadata.setScopeName("prototype");
        return scopeMetadata;
    }
}
