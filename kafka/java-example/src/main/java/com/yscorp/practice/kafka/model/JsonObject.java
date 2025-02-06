package com.yscorp.practice.kafka.model;

public class JsonObject {
	private String key;
	private Integer id;
	private String value;

	public JsonObject() {
	}

	public JsonObject(String key, Integer id, String value) {
		this.key = key;
		this.id = id;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "JsonObject{" +
			"key='" + key + '\'' +
			", id=" + id +
			", value='" + value + '\'' +
			'}';
	}
}
