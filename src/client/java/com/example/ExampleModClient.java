package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class ExampleModClient implements ClientModInitializer {
	private static TrailConfig modid$config = TrailConfig.load();
	private static final TrailController modid$trailController = new TrailController();
	private static final TrailRenderer modid$trailRenderer = new TrailRenderer();

	@Override
	public void onInitializeClient() {
		modid$applyConfig(modid$config);

		ClientTickEvents.END_CLIENT_TICK.register((Minecraft client) -> {
			modid$trailController.tick(client, modid$config);
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			if (!modid$trailController.isEnabled()) {
				return;
			}
			modid$trailRenderer.render(context, modid$trailController);
		});
	}

	static TrailConfig modid$getConfig() {
		return modid$config;
	}

	static void modid$reloadConfig() {
		modid$config = TrailConfig.load();
		modid$applyConfig(modid$config);
	}

	static void modid$applyConfig(TrailConfig config) {
		modid$trailController.applyConfig(config);
	}

	static void modid$clearTrail() {
		modid$trailController.clearTrail();
	}
}