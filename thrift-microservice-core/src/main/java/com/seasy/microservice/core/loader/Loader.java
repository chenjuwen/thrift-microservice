package com.seasy.microservice.core.loader;

/**
 * 加载器
 */
public interface Loader<T> {
    T load();
}
