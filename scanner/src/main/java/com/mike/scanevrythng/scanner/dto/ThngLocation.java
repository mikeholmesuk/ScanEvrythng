package com.mike.scanevrythng.scanner.dto;

/**
 * Created by mikeholmes
 */
public class ThngLocation {
	private Geo position;

	public ThngLocation(Geo geo) {
	 	this.position = geo;
	}

	public Geo getPosition() {
		return position;
	}

	public void setPosition(Geo position) {
		this.position = position;
	}
}
