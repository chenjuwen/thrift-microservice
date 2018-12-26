package com.seasy.microservice.server;

import java.util.Set;

import org.reflections.Reflections;

import com.seasy.microservice.core.common.ServiceAnnotation;

public class ReflectionsTest {
	public static void main(String[] args) {
		Reflections reflections = new Reflections("com.seasy.microservice");
		Set<Class<?>> annotationSet = reflections.getTypesAnnotatedWith(ServiceAnnotation.class);
		for(Class<?> clazz : annotationSet){
			ServiceAnnotation annotation = clazz.getAnnotation(ServiceAnnotation.class);
			System.out.println(annotation.serviceClass().getName());
			System.out.println(clazz.getName());
		}
	}
}
