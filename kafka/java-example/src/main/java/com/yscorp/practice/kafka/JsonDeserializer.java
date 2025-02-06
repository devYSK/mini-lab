package com.yscorp.practice.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDeserializer<T> implements Deserializer<T> {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private Class<T> tClass;

	public JsonDeserializer() {
	}

	public JsonDeserializer(Class<T> tClass) {
		this.tClass = tClass;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		String targetClassName = (String) configs.get("value.deserializer.targetClass");
		try {
			System.out.println("targetClassName: " + targetClassName);
			tClass = (Class<T>) Class.forName(targetClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Target class not found: " + targetClassName, e);
		}
	}

	@Override
	public T deserialize(String topic, byte[] data) {
		try {
			return objectMapper.readValue(data, tClass);
		} catch (Exception e) {
			System.err.println("Failed to deserialize data: " + new String(data));
			throw new RuntimeException("Error deserializing JSON object", e);
		}
	}

	@Override
	public void close() {}
}