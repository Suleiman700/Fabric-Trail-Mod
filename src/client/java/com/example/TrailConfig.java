package com.example;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public final class TrailConfig {
	private static final Gson modid$gson = new GsonBuilder().setPrettyPrinting().create();
	private static final String modid$fileName = "TrailMod.json";

	public int ttlSeconds = 10;
	public String color = "WHITE";
	public String mode = "STATIC";
	public String gradientEndColor = "WHITE";
	public boolean enabled = true;
	public int keyCode = 86; // GLFW_KEY_V
	public int lineWidth = 2;
	public int lineOpacity = 100;
	public boolean arrowsEnabled = true;
	public int arrowSpacingBlocks = 2;
	public int arrowSize = 1;
	public boolean pauseOnSneak = true;
	public boolean eraseOnRetrace = false;
	public boolean otherPlayersEnabled = false;
	public int otherPlayersRangeBlocks = 524;
	public int otherPlayersKeyCode = 66; // GLFW_KEY_B
	public int otherPlayersPointCap = 30000;

	public static TrailConfig load() {
		TrailConfig config = new TrailConfig();
		Path path = modid$getPath();
		if (!Files.exists(path)) {
			return config;
		}
		try (Reader reader = Files.newBufferedReader(path)) {
			TrailConfig loaded = modid$gson.fromJson(reader, TrailConfig.class);
			if (loaded != null) {
				loaded.validate();
				return loaded;
			}
		} catch (Exception ignored) {
		}
		return config;
	}

	public void save() {
		Path path = modid$getPath();
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				modid$gson.toJson(this, writer);
			}
		} catch (Exception ignored) {
		}
	}

	public void validate() {
		if (this.ttlSeconds < 1) {
			this.ttlSeconds = 1;
		}
		if (this.ttlSeconds > 10800) {
			this.ttlSeconds = 10800;
		}
		if (this.color == null) {
			this.color = "WHITE";
		}
		if (TrailColor.fromName(this.color) == null) {
			this.color = "WHITE";
		}
		if (this.mode == null || TrailMode.fromName(this.mode) == null) {
			this.mode = "STATIC";
		}
		if (this.gradientEndColor == null) {
			this.gradientEndColor = "WHITE";
		}
		if (TrailColor.fromName(this.gradientEndColor) == null) {
			this.gradientEndColor = "WHITE";
		}
		if (this.lineWidth < 1) {
			this.lineWidth = 1;
		}
		if (this.lineWidth > 10) {
			this.lineWidth = 10;
		}
		if (this.lineOpacity < 0) {
			this.lineOpacity = 0;
		}
		if (this.lineOpacity > 100) {
			this.lineOpacity = 100;
		}
		if (this.arrowSpacingBlocks < 1) {
			this.arrowSpacingBlocks = 1;
		}
		if (this.arrowSpacingBlocks > 40) {
			this.arrowSpacingBlocks = 40;
		}
		if (this.arrowSize < 1) {
			this.arrowSize = 1;
		}
		if (this.arrowSize > 10) {
			this.arrowSize = 10;
		}
		if (this.otherPlayersRangeBlocks < 0) {
			this.otherPlayersRangeBlocks = 0;
		}
		if (this.otherPlayersRangeBlocks > 4096) {
			this.otherPlayersRangeBlocks = 4096;
		}
		if (this.otherPlayersPointCap < 1000) {
			this.otherPlayersPointCap = 1000;
		}
		if (this.otherPlayersPointCap > 200000) {
			this.otherPlayersPointCap = 200000;
		}
	}

	public long ttlMs() {
		return (long) this.ttlSeconds * 1000L;
	}

	public TrailColor trailColor() {
		TrailColor c = TrailColor.fromName(this.color);
		return c == null ? TrailColor.WHITE : c;
	}

	public TrailColor gradientEndTrailColor() {
		TrailColor c = TrailColor.fromName(this.gradientEndColor);
		return c == null ? TrailColor.WHITE : c;
	}

	public TrailMode trailMode() {
		TrailMode m = TrailMode.fromName(this.mode);
		return m == null ? TrailMode.STATIC : m;
	}

	private static Path modid$getPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(modid$fileName);
	}
}
