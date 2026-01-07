package com.example;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class TrailConfigScreen extends Screen {
	private static final int modid$scrollTopPadding = 40;
	private static final int modid$footerButtonSpacing = 24;

	private static final class modid$ScrollEntry {
		private final net.minecraft.client.gui.components.AbstractWidget widget;
		private final int baseY;
		private final boolean scrolls;

		private modid$ScrollEntry(net.minecraft.client.gui.components.AbstractWidget widget, int baseY, boolean scrolls) {
			this.widget = widget;
			this.baseY = baseY;
			this.scrolls = scrolls;
		}
	}

	private final Screen modid$parent;
	private final TrailConfig modid$config;
	private final java.util.List<modid$ScrollEntry> modid$scrollEntries = new java.util.ArrayList<>();
	private int modid$scrollOffset = 0;
	private int modid$contentHeight = 0;
	private int modid$scrollTop = 0;
	private int modid$scrollBottom = 0;

	private EditBox modid$ttlSeconds;
	private CycleButton<TrailMode> modid$mode;
	private CycleButton<TrailColor> modid$color;
	private StringWidget modid$gradientEndLabel;
	private CycleButton<TrailColor> modid$gradientEndColor;
	private Button modid$toggleButton;
	private Button modid$arrowsButton;
	private EditBox modid$arrowSpacing;
	private EditBox modid$arrowSize;
	private Button modid$pauseOnSneakButton;
	private Button modid$eraseOnRetraceButton;
	private Button modid$keybindButton;
	private EditBox modid$lineWidth;
	private EditBox modid$lineOpacity;
	private Button modid$otherPlayersToggleButton;
	private EditBox modid$otherPlayersRange;
	private Button modid$otherPlayersKeybindButton;
	private EditBox modid$otherPlayersPointCap;
	private Button modid$resetButton;
	private Button modid$doneButton;
	private Button modid$cancelButton;
	private boolean modid$recordingKeybind = false;
	private boolean modid$recordingOtherPlayersKeybind = false;
	private boolean modid$arrowsEnabled;
	private boolean modid$pauseOnSneak;
	private boolean modid$eraseOnRetrace;
	private boolean modid$otherPlayersEnabled;

	private String modid$getKeyName(int keyCode) {
		String name = GLFW.glfwGetKeyName(keyCode, 0);
		return name != null ? name : "KEY_" + keyCode;
	}

	public TrailConfigScreen(Screen parent, TrailConfig config) {
		super(Component.literal("Trail Settings"));
		this.modid$parent = parent;
		this.modid$config = config;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int labelX = centerX - 140;
		int controlX = centerX - 30;
		int controlWidth = 200;
		int footerY = this.height - 28;
		int resetY = footerY - modid$footerButtonSpacing;
		this.modid$scrollTop = modid$scrollTopPadding;
		this.modid$scrollBottom = resetY - 8;
		int y = this.modid$scrollTop + 20;
		int rowHeight = 24;
		this.modid$scrollEntries.clear();

		this.modid$arrowsEnabled = this.modid$config.arrowsEnabled;
		this.modid$pauseOnSneak = this.modid$config.pauseOnSneak;
		this.modid$eraseOnRetrace = this.modid$config.eraseOnRetrace;
		this.modid$otherPlayersEnabled = this.modid$config.otherPlayersEnabled;

		this.modid$addLabel(Component.literal("Enable"), labelX, y + 6);
		this.modid$toggleButton = Button.builder(Component.literal(this.modid$config.enabled ? "Enabled" : "Disabled"), btn -> {
			this.modid$config.enabled = !this.modid$config.enabled;
			btn.setMessage(Component.literal(this.modid$config.enabled ? "Enabled" : "Disabled"));
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$toggleButton.setTooltip(Tooltip.create(Component.literal("Toggle mod on/off")));
		this.modid$addScrollWidget(this.modid$toggleButton, y);

		y += rowHeight;

		this.modid$addLabel(Component.literal("Trail TTL"), labelX, y + 6);
		this.modid$ttlSeconds = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("TTL (seconds)"));
		this.modid$ttlSeconds.setValue(Integer.toString(this.modid$config.ttlSeconds));
		this.modid$ttlSeconds.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$ttlSeconds.setTooltip(Tooltip.create(Component.literal("The time (in seconds) that the trail will last (1 - 10800)\n\nNote: Higher values can reduce FPS (more trail points to render).")));
		this.modid$addScrollWidget(this.modid$ttlSeconds, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Mode"), labelX, y + 6);
		this.modid$mode = CycleButton.builder((java.util.function.Function<TrailMode, Component>) m -> Component.literal(m.label()), (java.util.function.Supplier<TrailMode>) () -> TrailMode.STATIC)
			.withValues(TrailMode.values())
			.create(controlX, y, controlWidth, 20, Component.literal("Mode"), (button, value) -> {
				this.modid$updateGradientVisibility();
			});
		this.modid$mode.setValue(this.modid$config.trailMode());
		this.modid$mode.setTooltip(Tooltip.create(Component.literal("Static: single color\nGradient: start->end\nMotion: red when falling, green when rising")));
		this.modid$addScrollWidget(this.modid$mode, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Color"), labelX, y + 6);
		this.modid$color = CycleButton.builder((java.util.function.Function<TrailColor, Component>) c -> c.label(), (java.util.function.Supplier<TrailColor>) () -> TrailColor.WHITE)
			.withValues(TrailColor.values())
			.create(controlX, y, controlWidth, 20, Component.literal("Color"), (button, value) -> {
			});
		this.modid$color.setValue(this.modid$config.trailColor());
		this.modid$color.setTooltip(Tooltip.create(Component.literal("Trail color")));
		this.modid$addScrollWidget(this.modid$color, y);

		y += rowHeight;
		this.modid$gradientEndLabel = new StringWidget(this.font.width(Component.literal("Gradient End")), 10, Component.literal("Gradient End"), this.font);
		this.modid$gradientEndLabel.setX(labelX);
		this.modid$gradientEndLabel.setY(y + 6);
		this.modid$gradientEndLabel.setTooltip(Tooltip.create(Component.literal("Gradient End")));
		this.modid$scrollEntries.add(new modid$ScrollEntry(this.modid$gradientEndLabel, y + 6, true));
		this.addRenderableOnly(this.modid$gradientEndLabel);
		this.modid$gradientEndColor = CycleButton.builder((java.util.function.Function<TrailColor, Component>) c -> c.label(), (java.util.function.Supplier<TrailColor>) () -> TrailColor.WHITE)
			.withValues(TrailColor.values())
			.create(controlX, y, controlWidth, 20, Component.literal("Gradient End"), (button, value) -> {
			});
		this.modid$gradientEndColor.setValue(this.modid$config.gradientEndTrailColor());
		this.modid$gradientEndColor.setTooltip(Tooltip.create(Component.literal("The end color of the gradient")));
		this.modid$addScrollWidget(this.modid$gradientEndColor, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Line arrows"), labelX, y + 6);
		this.modid$arrowsButton = Button.builder(Component.literal(this.modid$arrowsEnabled ? "Shown" : "Hidden"), btn -> {
			this.modid$arrowsEnabled = !this.modid$arrowsEnabled;
			btn.setMessage(Component.literal(this.modid$arrowsEnabled ? "Shown" : "Hidden"));
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$arrowsButton.setTooltip(Tooltip.create(Component.literal("Show arrowheads on trail to indicate direction")));
		this.modid$addScrollWidget(this.modid$arrowsButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Arrow Spacing"), labelX, y + 6);
		this.modid$arrowSpacing = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Arrow Spacing"));
		this.modid$arrowSpacing.setValue(Integer.toString(this.modid$config.arrowSpacingBlocks));
		this.modid$arrowSpacing.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$arrowSpacing.setTooltip(Tooltip.create(Component.literal("Distance between arrows in blocks (1-40)")));
		this.modid$addScrollWidget(this.modid$arrowSpacing, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Arrow Size"), labelX, y + 6);
		this.modid$arrowSize = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Arrow Size"));
		this.modid$arrowSize.setValue(Integer.toString(this.modid$config.arrowSize));
		this.modid$arrowSize.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$arrowSize.setTooltip(Tooltip.create(Component.literal("Arrow size (1-10)")));
		this.modid$addScrollWidget(this.modid$arrowSize, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Pause on Sneak"), labelX, y + 6);
		this.modid$pauseOnSneakButton = Button.builder(Component.literal(this.modid$pauseOnSneak ? "Enabled" : "Disabled"), btn -> {
			this.modid$pauseOnSneak = !this.modid$pauseOnSneak;
			btn.setMessage(Component.literal(this.modid$pauseOnSneak ? "Enabled" : "Disabled"));
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$pauseOnSneakButton.setTooltip(Tooltip.create(Component.literal("When sneaking, stop adding new trail points")));
		this.modid$addScrollWidget(this.modid$pauseOnSneakButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Erase on Retrace"), labelX, y + 6);
		this.modid$eraseOnRetraceButton = Button.builder(Component.literal(this.modid$eraseOnRetrace ? "Enabled" : "Disabled"), btn -> {
			this.modid$eraseOnRetrace = !this.modid$eraseOnRetrace;
			btn.setMessage(Component.literal(this.modid$eraseOnRetrace ? "Enabled" : "Disabled"));
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$eraseOnRetraceButton.setTooltip(Tooltip.create(Component.literal("When you return close to an older part of your trail, erase everything newer than that point (radius: 0.75 blocks)")));
		this.modid$addScrollWidget(this.modid$eraseOnRetraceButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Keybind"), labelX, y + 6);
		this.modid$keybindButton = Button.builder(Component.literal(this.modid$getKeyName(this.modid$config.keyCode)), btn -> {
			this.modid$recordingKeybind = !this.modid$recordingKeybind;
			if (this.modid$recordingKeybind) {
				btn.setMessage(Component.literal("Press any key..."));
			} else {
				btn.setMessage(Component.literal(this.modid$getKeyName(this.modid$config.keyCode)));
			}
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$keybindButton.setTooltip(Tooltip.create(Component.literal("Click to record a keybind\n\nSingle press: resume/pause drawing\nDouble press: Clear Trail")));
		this.modid$addScrollWidget(this.modid$keybindButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Line Width"), labelX, y + 6);
		this.modid$lineWidth = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Line Width"));
		this.modid$lineWidth.setValue(Integer.toString(this.modid$config.lineWidth));
		this.modid$lineWidth.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$lineWidth.setTooltip(Tooltip.create(Component.literal("Line width (1-10)")));
		this.modid$addScrollWidget(this.modid$lineWidth, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Line Opacity"), labelX, y + 6);
		this.modid$lineOpacity = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Line Opacity"));
		this.modid$lineOpacity.setValue(Integer.toString(this.modid$config.lineOpacity));
		this.modid$lineOpacity.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$lineOpacity.setTooltip(Tooltip.create(Component.literal("How transparent the trail is (0-100)\n0: invisible\n100: fully visible")));
		this.modid$addScrollWidget(this.modid$lineOpacity, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Other Players"), labelX, y + 6);
		y += rowHeight;

		this.modid$addLabel(Component.literal("Enable drawing"), labelX, y + 6);
		this.modid$otherPlayersToggleButton = Button.builder(Component.literal(this.modid$otherPlayersEnabled ? "Enabled" : "Disabled"), btn -> {
			this.modid$otherPlayersEnabled = !this.modid$otherPlayersEnabled;
			btn.setMessage(Component.literal(this.modid$otherPlayersEnabled ? "Enabled" : "Disabled"));
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$otherPlayersToggleButton.setTooltip(Tooltip.create(Component.literal("Draw trails for other players (performance-heavy if many players)")));
		this.modid$addScrollWidget(this.modid$otherPlayersToggleButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Range"), labelX, y + 6);
		this.modid$otherPlayersRange = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Other Players Range"));
		this.modid$otherPlayersRange.setValue(Integer.toString(this.modid$config.otherPlayersRangeBlocks));
		this.modid$otherPlayersRange.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$otherPlayersRange.setTooltip(Tooltip.create(Component.literal("Only track other players within this distance (blocks)")));
		this.modid$addScrollWidget(this.modid$otherPlayersRange, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Hotkey"), labelX, y + 6);
		this.modid$otherPlayersKeybindButton = Button.builder(Component.literal(this.modid$getKeyName(this.modid$config.otherPlayersKeyCode)), btn -> {
			this.modid$recordingOtherPlayersKeybind = !this.modid$recordingOtherPlayersKeybind;
			if (this.modid$recordingOtherPlayersKeybind) {
				btn.setMessage(Component.literal("Press any key..."));
			} else {
				btn.setMessage(Component.literal(this.modid$getKeyName(this.modid$config.otherPlayersKeyCode)));
			}
		}).bounds(controlX, y, controlWidth, 20).build();
		this.modid$otherPlayersKeybindButton.setTooltip(Tooltip.create(Component.literal("Click to record a keybind\n\nSingle press: resume/pause drawing trails\nDouble press: clear trails")));
		this.modid$addScrollWidget(this.modid$otherPlayersKeybindButton, y);

		y += rowHeight;
		this.modid$addLabel(Component.literal("Point Cap"), labelX, y + 6);
		this.modid$otherPlayersPointCap = new EditBox(this.font, controlX, y, controlWidth, 20, Component.literal("Other Players Point Cap"));
		this.modid$otherPlayersPointCap.setValue(Integer.toString(this.modid$config.otherPlayersPointCap));
		this.modid$otherPlayersPointCap.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
		this.modid$otherPlayersPointCap.setTooltip(Tooltip.create(Component.literal("Safety cap for total other-player trail points (prevents lag)")));
		this.modid$addScrollWidget(this.modid$otherPlayersPointCap, y);

		this.modid$contentHeight = y + rowHeight + 10;
		this.modid$resetButton = Button.builder(Component.literal("Reset"), btn -> {
			TrailConfig defaults = new TrailConfig();
			this.modid$config.ttlSeconds = defaults.ttlSeconds;
			this.modid$config.color = defaults.color;
			this.modid$config.mode = defaults.mode;
			this.modid$config.gradientEndColor = defaults.gradientEndColor;
			this.modid$config.enabled = defaults.enabled;
			this.modid$config.keyCode = defaults.keyCode;
			this.modid$config.lineWidth = defaults.lineWidth;
			this.modid$config.lineOpacity = defaults.lineOpacity;
			this.modid$config.arrowsEnabled = defaults.arrowsEnabled;
			this.modid$config.arrowSpacingBlocks = defaults.arrowSpacingBlocks;
			this.modid$config.arrowSize = defaults.arrowSize;
			this.modid$config.pauseOnSneak = defaults.pauseOnSneak;
			this.modid$config.eraseOnRetrace = defaults.eraseOnRetrace;
			this.modid$config.otherPlayersEnabled = defaults.otherPlayersEnabled;
			this.modid$config.otherPlayersRangeBlocks = defaults.otherPlayersRangeBlocks;
			this.modid$config.otherPlayersKeyCode = defaults.otherPlayersKeyCode;
			this.modid$config.otherPlayersPointCap = defaults.otherPlayersPointCap;
			this.modid$config.save();
			ExampleModClient.modid$reloadConfig();
			this.minecraft.setScreen(new TrailConfigScreen(this.modid$parent, ExampleModClient.modid$getConfig()));
		}).bounds(centerX - 100, resetY, 200, 20).build();
		this.addRenderableWidget(this.modid$resetButton);

		this.modid$doneButton = Button.builder(Component.literal("Done"), btn -> {
			this.modid$applyAndSave();
			this.minecraft.setScreen(this.modid$parent);
		}).bounds(centerX - 100, footerY, 98, 20).build();
		this.addRenderableWidget(this.modid$doneButton);

		this.modid$cancelButton = Button.builder(Component.literal("Cancel"), btn -> {
			this.minecraft.setScreen(this.modid$parent);
		}).bounds(centerX + 2, footerY, 98, 20).build();
		this.addRenderableWidget(this.modid$cancelButton);

		this.modid$updateGradientVisibility();
		this.modid$clampScroll();
	}

	private void modid$updateGradientVisibility() {
		boolean show = this.modid$mode != null && this.modid$mode.getValue() == TrailMode.GRADIENT;
		if (this.modid$gradientEndLabel != null) {
			this.modid$gradientEndLabel.visible = show;
		}
		if (this.modid$gradientEndColor != null) {
			this.modid$gradientEndColor.visible = show;
		}
	}

	private void modid$addScrollWidget(net.minecraft.client.gui.components.AbstractWidget widget, int baseY) {
		this.modid$scrollEntries.add(new modid$ScrollEntry(widget, baseY, true));
		this.addRenderableWidget(widget);
	}

	private void modid$clampScroll() {
		int viewportHeight = this.modid$scrollBottom - this.modid$scrollTop;
		int max = Math.max(0, this.modid$contentHeight - viewportHeight);
		this.modid$scrollOffset = Mth.clamp(this.modid$scrollOffset, 0, max);
	}

	private void modid$applyAndSave() {
		String raw = this.modid$ttlSeconds.getValue();
		int ttl = this.modid$config.ttlSeconds;
		if (!raw.isEmpty()) {
			try {
				ttl = Integer.parseInt(raw);
			} catch (NumberFormatException ignored) {
			}
		}
		if (ttl < 1) {
			ttl = 1;
		}
		if (ttl > 10800) {
			ttl = 10800;
		}

		this.modid$config.ttlSeconds = ttl;
		this.modid$config.mode = this.modid$mode.getValue().name();
		this.modid$config.color = this.modid$color.getValue().name();
		this.modid$config.gradientEndColor = this.modid$gradientEndColor.getValue().name();

		String rawWidth = this.modid$lineWidth.getValue();
		int lineWidth = this.modid$config.lineWidth;
		if (!rawWidth.isEmpty()) {
			try {
				lineWidth = Integer.parseInt(rawWidth);
			} catch (NumberFormatException ignored) {
			}
		}
		if (lineWidth < 1) {
			lineWidth = 1;
		}
		if (lineWidth > 10) {
			lineWidth = 10;
		}

		this.modid$config.lineWidth = lineWidth;

		String rawOpacity = this.modid$lineOpacity.getValue();
		int lineOpacity = this.modid$config.lineOpacity;
		if (!rawOpacity.isEmpty()) {
			try {
				lineOpacity = Integer.parseInt(rawOpacity);
			} catch (NumberFormatException ignored) {
			}
		}
		if (lineOpacity < 0) {
			lineOpacity = 0;
		}
		if (lineOpacity > 100) {
			lineOpacity = 100;
		}
		this.modid$config.lineOpacity = lineOpacity;
		this.modid$config.arrowsEnabled = this.modid$arrowsEnabled;
		this.modid$config.pauseOnSneak = this.modid$pauseOnSneak;
		this.modid$config.eraseOnRetrace = this.modid$eraseOnRetrace;
		this.modid$config.otherPlayersEnabled = this.modid$otherPlayersEnabled;

		String rawOtherRange = this.modid$otherPlayersRange.getValue();
		int otherRange = this.modid$config.otherPlayersRangeBlocks;
		if (!rawOtherRange.isEmpty()) {
			try {
				otherRange = Integer.parseInt(rawOtherRange);
			} catch (NumberFormatException ignored) {
			}
		}
		if (otherRange < 0) {
			otherRange = 0;
		}
		if (otherRange > 4096) {
			otherRange = 4096;
		}
		this.modid$config.otherPlayersRangeBlocks = otherRange;

		String rawOtherCap = this.modid$otherPlayersPointCap.getValue();
		int otherCap = this.modid$config.otherPlayersPointCap;
		if (!rawOtherCap.isEmpty()) {
			try {
				otherCap = Integer.parseInt(rawOtherCap);
			} catch (NumberFormatException ignored) {
			}
		}
		if (otherCap < 1000) {
			otherCap = 1000;
		}
		if (otherCap > 200000) {
			otherCap = 200000;
		}
		this.modid$config.otherPlayersPointCap = otherCap;

		String rawArrowSpacing = this.modid$arrowSpacing.getValue();
		int arrowSpacing = this.modid$config.arrowSpacingBlocks;
		if (!rawArrowSpacing.isEmpty()) {
			try {
				arrowSpacing = Integer.parseInt(rawArrowSpacing);
			} catch (NumberFormatException ignored) {
			}
		}
		if (arrowSpacing < 1) {
			arrowSpacing = 1;
		}
		if (arrowSpacing > 40) {
			arrowSpacing = 40;
		}
		this.modid$config.arrowSpacingBlocks = arrowSpacing;

		String rawArrowSize = this.modid$arrowSize.getValue();
		int arrowSize = this.modid$config.arrowSize;
		if (!rawArrowSize.isEmpty()) {
			try {
				arrowSize = Integer.parseInt(rawArrowSize);
			} catch (NumberFormatException ignored) {
			}
		}
		if (arrowSize < 1) {
			arrowSize = 1;
		}
		if (arrowSize > 10) {
			arrowSize = 10;
		}
		this.modid$config.arrowSize = arrowSize;

		this.modid$config.validate();
		this.modid$config.save();
		ExampleModClient.modid$reloadConfig();
		ExampleModClient.modid$clearTrail();
	}

	private void modid$addLabel(Component text, int x, int y) {
		StringWidget label = new StringWidget(this.font.width(text), 10, text, this.font);
		label.setX(x);
		label.setY(y);
		label.setTooltip(Tooltip.create(text));
		this.modid$scrollEntries.add(new modid$ScrollEntry(label, y, true));
		this.addRenderableOnly(label);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		for (modid$ScrollEntry entry : this.modid$scrollEntries) {
			if (!entry.scrolls) {
				continue;
			}
			entry.widget.setY(entry.baseY - this.modid$scrollOffset);
			boolean inViewport = entry.widget.getY() + entry.widget.getHeight() >= this.modid$scrollTop && entry.widget.getY() <= this.modid$scrollBottom;
			entry.widget.visible = inViewport;
			entry.widget.active = inViewport;
		}
		this.modid$clampScroll();

		graphics.enableScissor(0, this.modid$scrollTop, this.width, this.modid$scrollBottom);
		for (modid$ScrollEntry entry : this.modid$scrollEntries) {
			entry.widget.render(graphics, mouseX, mouseY, partialTick);
		}
		graphics.disableScissor();

		int viewportHeight = this.modid$scrollBottom - this.modid$scrollTop;
		int max = Math.max(0, this.modid$contentHeight - viewportHeight);
		if (max > 0) {
			int barX1 = this.width - 8;
			int barX2 = this.width - 4;
			graphics.fill(barX1, this.modid$scrollTop, barX2, this.modid$scrollBottom, 0x66000000);

			int thumbHeight = Math.max(12, (int) ((float) viewportHeight * (float) viewportHeight / (float) this.modid$contentHeight));
			int thumbRange = viewportHeight - thumbHeight;
			int thumbY = this.modid$scrollTop + (int) ((float) this.modid$scrollOffset / (float) max * (float) thumbRange);
			graphics.fill(barX1, thumbY, barX2, thumbY + thumbHeight, 0xFFAAAAAA);
		}

		if (this.modid$resetButton != null) {
			this.modid$resetButton.render(graphics, mouseX, mouseY, partialTick);
		}
		if (this.modid$doneButton != null) {
			this.modid$doneButton.render(graphics, mouseX, mouseY, partialTick);
		}
		if (this.modid$cancelButton != null) {
			this.modid$cancelButton.render(graphics, mouseX, mouseY, partialTick);
		}

		graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		if (mouseY < this.modid$scrollTop || mouseY > this.modid$scrollBottom) {
			return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
		}
		this.modid$scrollOffset -= (int) (amountY * 20.0D);
		this.modid$clampScroll();
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (this.modid$recordingKeybind) {
			int keyCode = event.key();
			int scanCode = event.scancode();
			String keyName = GLFW.glfwGetKeyName(keyCode, scanCode);
			if (keyName == null) {
				keyName = "KEY_" + keyCode;
			}
			this.modid$config.keyCode = keyCode;
			this.modid$recordingKeybind = false;
			this.modid$keybindButton.setMessage(Component.literal(this.modid$getKeyName(this.modid$config.keyCode)));
			return true;
		}
		if (this.modid$recordingOtherPlayersKeybind) {
			int keyCode = event.key();
			int scanCode = event.scancode();
			String keyName = GLFW.glfwGetKeyName(keyCode, scanCode);
			if (keyName == null) {
				keyName = "KEY_" + keyCode;
			}
			this.modid$config.otherPlayersKeyCode = keyCode;
			this.modid$recordingOtherPlayersKeybind = false;
			this.modid$otherPlayersKeybindButton.setMessage(Component.literal(this.modid$getKeyName(this.modid$config.otherPlayersKeyCode)));
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
