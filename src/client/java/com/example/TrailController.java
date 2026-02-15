package com.example;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

final class TrailController {
	private static final double modid$trailMinPointDistance = 0.15D;
	private static final long modid$doublePressThresholdMs = 250L;
	private static final double modid$eraseOnRetraceRadiusSqr = 0.75D * 0.75D;
	private static final long modid$eraseOnRetraceMinAgeMs = 500L;
	private static final int modid$maxPruneRemovalsPerTick = 200;
	private static final int modid$maxEraseRemovalsPerTick = 400;

	private final ArrayDeque<TrailPoint> modid$trailPoints = new ArrayDeque<>();
	private Vec3 modid$lastTrailPos;
	private double modid$cumulativeDistance;

	private boolean modid$wasTogglePressed;
	private boolean modid$paused;
	private long modid$lastHotkeyPressMs;
	private boolean modid$pendingSinglePress;

	private long modid$trailPointTtlMs = 10_000L;
	private boolean modid$enabled = true;
	private TrailColor modid$color = TrailColor.WHITE;
	private TrailMode modid$mode = TrailMode.STATIC;
	private TrailColor modid$gradientEndColor = TrailColor.WHITE;
	private int modid$lineWidth = 2;
	private int modid$lineOpacity = 100;
	private boolean modid$arrowsEnabled = true;
	private int modid$arrowSpacingBlocks = 3;
	private int modid$arrowSize = 4;
	private boolean modid$pauseOnSneak = true;
	private boolean modid$eraseOnRetrace = false;
	private int modid$pendingCutTargetSize = -1;
	private boolean modid$pendingCutInsertBreak;

	private boolean modid$otherPlayersEnabled;
	private int modid$otherPlayersRangeBlocks;
	private int modid$otherPlayersKeyCode;
	private int modid$otherPlayersPointCap;
	private boolean modid$wasOtherPlayersTogglePressed;
	private long modid$lastOtherPlayersHotkeyPressMs;
	private boolean modid$pendingOtherPlayersSinglePress;
	private long modid$lastOtherPlayersWarnMs;
	private final Map<UUID, modid$OtherPlayerTrail> modid$otherPlayerTrails = new HashMap<>();

	static final class modid$OtherPlayerTrail {
		final UUID uuid;
		final ArrayDeque<TrailPoint> points = new ArrayDeque<>();
		Vec3 lastPos;
		double cumulativeDistance;
		int r = 255;
		int g = 255;
		int b = 255;

		modid$OtherPlayerTrail(UUID uuid) {
			this.uuid = uuid;
		}
	}

	static final class TrailPoint {
		final Vec3 pos;
		final long timeMs;
		final double dist;
		final boolean breakPoint;

		TrailPoint(Vec3 pos, long timeMs, double dist) {
			this.pos = pos;
			this.timeMs = timeMs;
			this.dist = dist;
			this.breakPoint = false;
		}

		TrailPoint(Vec3 pos, long timeMs, double dist, boolean breakPoint) {
			this.pos = pos;
			this.timeMs = timeMs;
			this.dist = dist;
			this.breakPoint = breakPoint;
		}
	}

	void applyConfig(TrailConfig config) {
		config.validate();
		this.modid$trailPointTtlMs = config.ttlMs();
		this.modid$color = config.trailColor();
		this.modid$mode = config.trailMode();
		this.modid$gradientEndColor = config.gradientEndTrailColor();
		this.modid$enabled = config.enabled;
		this.modid$lineWidth = config.lineWidth;
		this.modid$lineOpacity = config.lineOpacity;
		this.modid$arrowsEnabled = config.arrowsEnabled;
		this.modid$arrowSpacingBlocks = config.arrowSpacingBlocks;
		this.modid$arrowSize = config.arrowSize;
		this.modid$pauseOnSneak = config.pauseOnSneak;
		this.modid$eraseOnRetrace = config.eraseOnRetrace;
		this.modid$otherPlayersEnabled = config.otherPlayersEnabled;
		this.modid$otherPlayersRangeBlocks = config.otherPlayersRangeBlocks;
		this.modid$otherPlayersKeyCode = config.otherPlayersKeyCode;
		this.modid$otherPlayersPointCap = config.otherPlayersPointCap;
	}

	void tick(Minecraft client, TrailConfig config) {
		if (client.player == null) {
			this.modid$wasTogglePressed = false;
			this.modid$lastHotkeyPressMs = 0L;
			this.modid$pendingSinglePress = false;
			this.modid$trailPoints.clear();
			this.modid$lastTrailPos = null;
			this.modid$pendingCutTargetSize = -1;
			this.modid$pendingCutInsertBreak = false;
			this.modid$wasOtherPlayersTogglePressed = false;
			this.modid$lastOtherPlayersHotkeyPressMs = 0L;
			this.modid$pendingOtherPlayersSinglePress = false;
			this.modid$otherPlayerTrails.clear();
			return;
		}

		com.mojang.blaze3d.platform.Window window = client.getWindow();
		long nowMs = System.currentTimeMillis();
		boolean togglePressed = InputConstants.isKeyDown(window, config.keyCode);
		if (togglePressed && !this.modid$wasTogglePressed) {
			if (this.modid$lastHotkeyPressMs > 0L && nowMs - this.modid$lastHotkeyPressMs <= modid$doublePressThresholdMs) {
				this.modid$pendingSinglePress = false;
				this.clearTrail();
				this.modid$paused = false;
				this.modid$lastHotkeyPressMs = 0L;
				client.gui.getChat().addMessage(Component.literal("Trail cleared"));
			} else {
				this.modid$lastHotkeyPressMs = nowMs;
				this.modid$pendingSinglePress = true;
			}
		}
		if (this.modid$pendingSinglePress && this.modid$lastHotkeyPressMs > 0L && nowMs - this.modid$lastHotkeyPressMs > modid$doublePressThresholdMs) {
			this.modid$pendingSinglePress = false;
			this.modid$paused = !this.modid$paused;
			this.modid$lastHotkeyPressMs = 0L;
			client.gui.getChat().addMessage(Component.literal(this.modid$paused ? "Trail paused" : "Trail resumed"));
		}
		this.modid$wasTogglePressed = togglePressed;

		boolean otherPressed = InputConstants.isKeyDown(window, this.modid$otherPlayersKeyCode);
		if (otherPressed && !this.modid$wasOtherPlayersTogglePressed) {
			if (this.modid$lastOtherPlayersHotkeyPressMs > 0L && nowMs - this.modid$lastOtherPlayersHotkeyPressMs <= modid$doublePressThresholdMs) {
				this.modid$pendingOtherPlayersSinglePress = false;
				this.modid$otherPlayerTrails.clear();
				this.modid$lastOtherPlayersHotkeyPressMs = 0L;
				client.gui.getChat().addMessage(Component.literal("Other-player trails cleared"));
			} else {
				this.modid$lastOtherPlayersHotkeyPressMs = nowMs;
				this.modid$pendingOtherPlayersSinglePress = true;
			}
		}
		if (this.modid$pendingOtherPlayersSinglePress && this.modid$lastOtherPlayersHotkeyPressMs > 0L && nowMs - this.modid$lastOtherPlayersHotkeyPressMs > modid$doublePressThresholdMs) {
			this.modid$pendingOtherPlayersSinglePress = false;
			this.modid$otherPlayersEnabled = !this.modid$otherPlayersEnabled;
			this.modid$lastOtherPlayersHotkeyPressMs = 0L;
			client.gui.getChat().addMessage(Component.literal(this.modid$otherPlayersEnabled ? "Other-player trails enabled" : "Other-player trails disabled"));
		}
		this.modid$wasOtherPlayersTogglePressed = otherPressed;
		if (!this.modid$enabled) {
			return;
		}

		this.pruneTrail(nowMs);
		if (this.modid$paused) {
			return;
		}

		Vec3 current = client.player.position();
		if (this.modid$pendingCutTargetSize >= 0) {
			int removed = 0;
			while (this.modid$trailPoints.size() > this.modid$pendingCutTargetSize && removed < modid$maxEraseRemovalsPerTick) {
				this.modid$trailPoints.removeLast();
				removed++;
			}
			if (this.modid$trailPoints.size() <= this.modid$pendingCutTargetSize) {
				this.modid$pendingCutTargetSize = -1;
				if (this.modid$pendingCutInsertBreak) {
					TrailPoint last = this.modid$trailPoints.peekLast();
					if (last == null || !last.breakPoint) {
						this.modid$trailPoints.addLast(new TrailPoint(current, nowMs, 0.0D, true));
					}
					this.modid$pendingCutInsertBreak = false;
				}
				this.modid$lastTrailPos = null;
				this.modid$cumulativeDistance = 0.0D;
			}
		}
		if (this.modid$eraseOnRetrace && !this.modid$trailPoints.isEmpty()) {
			int idx = 0;
			int bestIndex = -1;
			double bestDistSqr = Double.MAX_VALUE;
			for (TrailPoint p : this.modid$trailPoints) {
				if (p.breakPoint) {
					idx++;
					continue;
				}
				if (nowMs - p.timeMs < modid$eraseOnRetraceMinAgeMs) {
					idx++;
					continue;
				}
				double d = p.pos.distanceToSqr(current);
				if (d <= modid$eraseOnRetraceRadiusSqr && d < bestDistSqr) {
					bestDistSqr = d;
					bestIndex = idx;
				}
				idx++;
			}

			if (bestIndex >= 0) {
				int targetSize = bestIndex + 1;
				if (this.modid$trailPoints.size() > targetSize) {
					this.modid$pendingCutTargetSize = targetSize;
					this.modid$pendingCutInsertBreak = true;
				}
			}
		}
		if (this.modid$pauseOnSneak && client.player.isShiftKeyDown()) {
			return;
		}

		if (this.modid$lastTrailPos == null || current.distanceTo(this.modid$lastTrailPos) >= modid$trailMinPointDistance) {
			if (this.modid$lastTrailPos != null) {
				this.modid$cumulativeDistance += current.distanceTo(this.modid$lastTrailPos);
			}
			this.modid$trailPoints.addLast(new TrailPoint(current.add(0.0D, 0.05D, 0.0D), nowMs, this.modid$cumulativeDistance));
			this.modid$lastTrailPos = current;
		}

		this.modid$tickOtherPlayers(client, nowMs);
	}

	private void modid$tickOtherPlayers(Minecraft client, long nowMs) {
		if (!this.modid$otherPlayersEnabled) {
			return;
		}
		if (client.level == null || client.player == null) {
			return;
		}
		int range = this.modid$otherPlayersRangeBlocks;
		double rangeSqr = (double) range * (double) range;
		Vec3 self = client.player.position();

		int totalPoints = 0;
		for (modid$OtherPlayerTrail t : this.modid$otherPlayerTrails.values()) {
			totalPoints += t.points.size();
		}
		int warnThreshold = (int) ((double) this.modid$otherPlayersPointCap * 0.66D);
		if (totalPoints > warnThreshold && nowMs - this.modid$lastOtherPlayersWarnMs > 10_000L) {
			this.modid$lastOtherPlayersWarnMs = nowMs;
			client.gui.getChat().addMessage(Component.literal("Other-player trails are getting large and may cause lag. Consider clearing them (double press hotkey) or lowering TTL/range."));
		}
		if (totalPoints > this.modid$otherPlayersPointCap) {
			this.modid$otherPlayerTrails.clear();
			client.gui.getChat().addMessage(Component.literal("Other-player trails cleared to prevent lag (point cap reached)"));
			return;
		}

		Map<UUID, Boolean> seen = new HashMap<>();
		for (Player p : client.level.players()) {
			if (p == client.player) {
				continue;
			}
			Vec3 pos = p.position();
			if (range > 0 && pos.distanceToSqr(self) > rangeSqr) {
				continue;
			}
			UUID uuid = p.getUUID();
			seen.put(uuid, Boolean.TRUE);
			modid$OtherPlayerTrail trail = this.modid$otherPlayerTrails.computeIfAbsent(uuid, modid$OtherPlayerTrail::new);
			int[] rgb = modid$getPlayerColorRgb(p);
			trail.r = rgb[0];
			trail.g = rgb[1];
			trail.b = rgb[2];

			this.modid$pruneOtherTrail(trail, nowMs);
			if (trail.lastPos == null || pos.distanceTo(trail.lastPos) >= modid$trailMinPointDistance) {
				if (trail.lastPos != null) {
					trail.cumulativeDistance += pos.distanceTo(trail.lastPos);
				}
				trail.points.addLast(new TrailPoint(pos.add(0.0D, 0.05D, 0.0D), nowMs, trail.cumulativeDistance));
				trail.lastPos = pos;
			}
		}

		this.modid$otherPlayerTrails.entrySet().removeIf(e -> !seen.containsKey(e.getKey()));
	}

	private void modid$pruneOtherTrail(modid$OtherPlayerTrail trail, long nowMs) {
		int removed = 0;
		while (!trail.points.isEmpty() && removed < modid$maxPruneRemovalsPerTick) {
			TrailPoint first = trail.points.peekFirst();
			if (first == null || nowMs - first.timeMs <= this.modid$trailPointTtlMs) {
				break;
			}
			trail.points.removeFirst();
			removed++;
		}
		if (trail.points.isEmpty()) {
			trail.lastPos = null;
			trail.cumulativeDistance = 0.0D;
		}
	}

	private static int[] modid$getPlayerColorRgb(Player p) {
		if (p.getTeam() != null && p.getTeam().getColor() != null) {
			ChatFormatting fmt = p.getTeam().getColor();
			switch (fmt) {
				case BLACK: return new int[]{0, 0, 0};
				case DARK_BLUE: return new int[]{0, 0, 170};
				case DARK_GREEN: return new int[]{0, 170, 0};
				case DARK_AQUA: return new int[]{0, 170, 170};
				case DARK_RED: return new int[]{170, 0, 0};
				case DARK_PURPLE: return new int[]{170, 0, 170};
				case GOLD: return new int[]{255, 170, 0};
				case GRAY: return new int[]{170, 170, 170};
				case DARK_GRAY: return new int[]{85, 85, 85};
				case BLUE: return new int[]{85, 85, 255};
				case GREEN: return new int[]{85, 255, 85};
				case AQUA: return new int[]{85, 255, 255};
				case RED: return new int[]{255, 85, 85};
				case LIGHT_PURPLE: return new int[]{255, 85, 255};
				case YELLOW: return new int[]{255, 255, 85};
				case WHITE:
				default: return new int[]{255, 255, 255};
			}
		}
		return new int[]{255, 255, 255};
	}

	private void pruneTrail(long nowMs) {
		int removed = 0;
		while (!this.modid$trailPoints.isEmpty() && removed < modid$maxPruneRemovalsPerTick) {
			TrailPoint first = this.modid$trailPoints.peekFirst();
			if (first == null || nowMs - first.timeMs <= this.modid$trailPointTtlMs) {
				break;
			}
			this.modid$trailPoints.removeFirst();
			removed++;
		}
		if (this.modid$trailPoints.isEmpty()) {
			this.modid$lastTrailPos = null;
			this.modid$cumulativeDistance = 0.0D;
		}
	}

	boolean isEnabled() {
		return this.modid$enabled;
	}

	int getLineWidth() {
		return this.modid$lineWidth;
	}

	int getLineOpacity() {
		return this.modid$lineOpacity;
	}

	TrailColor getColor() {
		return this.modid$color;
	}

	TrailMode getMode() {
		return this.modid$mode;
	}

	TrailColor getGradientEndColor() {
		return this.modid$gradientEndColor;
	}

	boolean arrowsEnabled() {
		return this.modid$arrowsEnabled;
	}

	int getArrowSpacingBlocks() {
		return this.modid$arrowSpacingBlocks;
	}

	int getArrowSize() {
		return this.modid$arrowSize;
	}

	int getTrailPointCount() {
		return this.modid$trailPoints.size();
	}

	Iterable<TrailPoint> getTrailPoints() {
		return this.modid$trailPoints;
	}

	void clearTrail() {
		this.modid$trailPoints.clear();
		this.modid$lastTrailPos = null;
		this.modid$cumulativeDistance = 0.0D;
	}

	boolean otherPlayersEnabled() {
		return this.modid$otherPlayersEnabled;
	}

	Iterable<modid$OtherPlayerTrail> getOtherPlayerTrails() {
		return this.modid$otherPlayerTrails.values();
	}

	static float normalizeOrDefault(float dx, float dy, float dz, int axis) {
		float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
		if (len <= 0.0F) {
			return axis == 1 ? 1.0F : 0.0F;
		}
		return axis == 0 ? dx / len : axis == 1 ? dy / len : dz / len;
	}
}
