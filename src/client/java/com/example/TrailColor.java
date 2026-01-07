package com.example;

import net.minecraft.network.chat.Component;

public enum TrailColor {
	WHITE(255, 255, 255),
	RED(255, 0, 0),
	GREEN(0, 255, 0),
	BLUE(0, 128, 255),
	YELLOW(255, 255, 0),
	PURPLE(200, 0, 255);

	public final int r;
	public final int g;
	public final int b;

	TrailColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Component label() {
		return Component.literal(this.name());
	}

	public static TrailColor fromName(String name) {
		if (name == null) {
			return null;
		}
		for (TrailColor c : values()) {
			if (c.name().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
}
