package com.example;

enum TrailMode {
	STATIC("Static"),
	GRADIENT("Gradient"),
	MOTION("Motion");

	private final String label;

	TrailMode(String label) {
		this.label = label;
	}

	String label() {
		return this.label;
	}

	static TrailMode fromName(String name) {
		if (name == null) {
			return null;
		}
		for (TrailMode m : values()) {
			if (m.name().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
}
