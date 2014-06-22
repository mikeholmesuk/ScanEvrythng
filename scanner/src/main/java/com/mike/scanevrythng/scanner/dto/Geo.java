package com.mike.scanevrythng.scanner.dto;

/**
 * Created by mikeholmes
 */
public class Geo {
	private String type;
	private Double[] coordinates;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Double[] coordinates) {
		this.coordinates = coordinates;
	}
}
