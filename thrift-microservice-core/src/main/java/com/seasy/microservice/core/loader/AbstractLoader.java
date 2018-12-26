package com.seasy.microservice.core.loader;

public abstract class AbstractLoader<T> implements Loader<T> {
	/**
	 * 组件类所在的包路径，多个包路径用分号(;)分隔
	 */
    private String basePackages;

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

}
