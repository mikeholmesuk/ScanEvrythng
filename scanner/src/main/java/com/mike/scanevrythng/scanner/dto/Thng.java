package com.mike.scanevrythng.scanner.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mikeholmes
 */
public class Thng {
	private String id;
	private String name;
	private String description;
	private ThngLocation location;
	private List<String> tags;
	private Map<String, String> properties;
	private Map<String, String> customFields;
	private Date createdAt;
	private Date updatedAt;

	public Thng() {}

	// Getters & Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}

	public ThngLocation getLocation() {
		return location;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void setLocation(ThngLocation location) {
		this.location = location;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String toString() {
		return this.name + " (" + this.description + ")";
	}
}
