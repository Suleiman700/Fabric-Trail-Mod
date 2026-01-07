package com.example;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

final class TrailRenderer {
	private static int modid$lerpInt(int a, int b, float t) {
		return (int) Mth.clamp((float) a + ((float) (b - a)) * t, 0.0F, 255.0F);
	}

	private static int modid$opacityToAlpha(int opacityPercent) {
		int clamped = Mth.clamp(opacityPercent, 0, 100);
		return (int) ((float) clamped / 100.0F * 255.0F);
	}

	private static void modid$drawOtherTrail(WorldRenderContext context, TrailController.modid$OtherPlayerTrail trail, VertexConsumer vertexConsumer, float lineWidth, int alpha) {
		if (context.worldState() == null || context.worldState().cameraRenderState == null || context.worldState().cameraRenderState.pos == null) {
			return;
		}
		Vec3 camPos = context.worldState().cameraRenderState.pos;
		var poseStack = context.matrices();
		if (poseStack == null) {
			return;
		}
		var pose = poseStack.last();
		var matrix = pose.pose();
		var normal = pose;

		TrailController.TrailPoint prev = null;
		for (TrailController.TrailPoint p : trail.points) {
			if (p.breakPoint) {
				prev = null;
				continue;
			}
			if (prev != null) {
				float x1 = (float) (prev.pos.x - camPos.x);
				float y1 = (float) (prev.pos.y - camPos.y);
				float z1 = (float) (prev.pos.z - camPos.z);
				float x2 = (float) (p.pos.x - camPos.x);
				float y2 = (float) (p.pos.y - camPos.y);
				float z2 = (float) (p.pos.z - camPos.z);

				float dx = x2 - x1;
				float dy = y2 - y1;
				float dz = z2 - z1;
				float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
				float nx = len > 0.0F ? dx / len : 0.0F;
				float ny = len > 0.0F ? dy / len : 1.0F;
				float nz = len > 0.0F ? dz / len : 0.0F;

				vertexConsumer.addVertex(matrix, x1, y1, z1).setColor(trail.r, trail.g, trail.b, alpha).setNormal(normal, nx, ny, nz).setLineWidth(lineWidth);
				vertexConsumer.addVertex(matrix, x2, y2, z2).setColor(trail.r, trail.g, trail.b, alpha).setNormal(normal, nx, ny, nz).setLineWidth(lineWidth);
			}
			prev = p;
		}
	}

	private static void modid$drawArrow(VertexConsumer vertexConsumer, com.mojang.blaze3d.vertex.PoseStack.Pose pose, float tipX, float tipY, float tipZ, float dirX, float dirY, float dirZ, float lineWidth, float arrowLength, float arrowWidth, int r, int g, int b, int a) {
		float baseX = tipX - dirX * arrowLength;
		float baseY = tipY - dirY * arrowLength;
		float baseZ = tipZ - dirZ * arrowLength;

		float upX = Math.abs(dirY) > 0.9F ? 1.0F : 0.0F;
		float upY = Math.abs(dirY) > 0.9F ? 0.0F : 1.0F;
		float upZ = 0.0F;

		float sideX = dirY * upZ - dirZ * upY;
		float sideY = dirZ * upX - dirX * upZ;
		float sideZ = dirX * upY - dirY * upX;
		float sideLen = Mth.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
		if (sideLen <= 1.0e-4F) {
			return;
		}
		sideX /= sideLen;
		sideY /= sideLen;
		sideZ /= sideLen;

		float leftX = baseX + sideX * arrowWidth;
		float leftY = baseY + sideY * arrowWidth;
		float leftZ = baseZ + sideZ * arrowWidth;
		float rightX = baseX - sideX * arrowWidth;
		float rightY = baseY - sideY * arrowWidth;
		float rightZ = baseZ - sideZ * arrowWidth;

		var matrix = pose.pose();
		var normal = pose;
		vertexConsumer.addVertex(matrix, tipX, tipY, tipZ).setColor(r, g, b, a).setNormal(normal, dirX, dirY, dirZ).setLineWidth(lineWidth);
		vertexConsumer.addVertex(matrix, leftX, leftY, leftZ).setColor(r, g, b, a).setNormal(normal, dirX, dirY, dirZ).setLineWidth(lineWidth);

		vertexConsumer.addVertex(matrix, tipX, tipY, tipZ).setColor(r, g, b, a).setNormal(normal, dirX, dirY, dirZ).setLineWidth(lineWidth);
		vertexConsumer.addVertex(matrix, rightX, rightY, rightZ).setColor(r, g, b, a).setNormal(normal, dirX, dirY, dirZ).setLineWidth(lineWidth);
	}

	private static void modid$drawTrail(WorldRenderContext context, TrailController controller, VertexConsumer vertexConsumer, float lineWidth) {
		if (context.worldState() == null || context.worldState().cameraRenderState == null || context.worldState().cameraRenderState.pos == null) {
			return;
		}
		Vec3 camPos = context.worldState().cameraRenderState.pos;
		var poseStack = context.matrices();
		if (poseStack == null) {
			return;
		}
		var pose = poseStack.last();
		var matrix = pose.pose();
		var normal = pose;

		TrailMode mode = controller.getMode();
		TrailColor startColor = controller.getColor();
		TrailColor endColor = controller.getGradientEndColor();
		int pointCount = controller.getTrailPointCount();
		int arrowSpacingBlocks = controller.getArrowSpacingBlocks();
		float arrowSpacing = (float) arrowSpacingBlocks;
		int arrowSize = controller.getArrowSize();
		float baseArrowLength = 0.12F + (float) arrowSize * 0.04F;
		float baseArrowWidth = baseArrowLength * 0.5F;
		int alpha = modid$opacityToAlpha(controller.getLineOpacity());

		TrailController.TrailPoint prev = null;
		int idx = 0;
		for (TrailController.TrailPoint p : controller.getTrailPoints()) {
			if (p.breakPoint) {
				prev = null;
				idx++;
				continue;
			}
			if (prev != null) {
				float t = pointCount <= 1 ? 0.0F : (float) idx / (float) (pointCount - 1);
				int r;
				int g;
				int b;
				if (mode == TrailMode.GRADIENT) {
					r = modid$lerpInt(startColor.r, endColor.r, t);
					g = modid$lerpInt(startColor.g, endColor.g, t);
					b = modid$lerpInt(startColor.b, endColor.b, t);
				} else if (mode == TrailMode.MOTION) {
					double dy = p.pos.y - prev.pos.y;
					if (dy < -0.001D) {
						r = TrailColor.RED.r;
						g = TrailColor.RED.g;
						b = TrailColor.RED.b;
					} else if (dy > 0.001D) {
						r = TrailColor.GREEN.r;
						g = TrailColor.GREEN.g;
						b = TrailColor.GREEN.b;
					} else {
						r = startColor.r;
						g = startColor.g;
						b = startColor.b;
					}
				} else {
					r = startColor.r;
					g = startColor.g;
					b = startColor.b;
				}

				float x1 = (float) (prev.pos.x - camPos.x);
				float y1 = (float) (prev.pos.y - camPos.y);
				float z1 = (float) (prev.pos.z - camPos.z);
				float x2 = (float) (p.pos.x - camPos.x);
				float y2 = (float) (p.pos.y - camPos.y);
				float z2 = (float) (p.pos.z - camPos.z);

				float dx = x2 - x1;
				float dy = y2 - y1;
				float dz = z2 - z1;
				float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
				float nx = len > 0.0F ? dx / len : 0.0F;
				float ny = len > 0.0F ? dy / len : 1.0F;
				float nz = len > 0.0F ? dz / len : 0.0F;

				vertexConsumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, alpha).setNormal(normal, nx, ny, nz).setLineWidth(lineWidth);
				vertexConsumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, alpha).setNormal(normal, nx, ny, nz).setLineWidth(lineWidth);

				if (controller.arrowsEnabled() && len >= 0.2F && arrowSpacing > 0.0F) {
					double dist1 = prev.dist;
					double dist2 = p.dist;
					double distDelta = dist2 - dist1;
					if (distDelta > 1.0e-6D) {
						int startK = (int) Math.floor(dist1 / (double) arrowSpacing) + 1;
						int endK = (int) Math.floor(dist2 / (double) arrowSpacing);
						for (int k = startK; k <= endK; k++) {
							double targetDist = (double) k * (double) arrowSpacing;
							double u = (targetDist - dist1) / distDelta;
							if (u < 0.0D || u > 1.0D) {
								continue;
							}
							float tipX = (float) (x1 + (x2 - x1) * (float) u);
							float tipY = (float) (y1 + (y2 - y1) * (float) u);
							float tipZ = (float) (z1 + (z2 - z1) * (float) u);
							float arrowLength = Math.min(baseArrowLength, arrowSpacing);
							modid$drawArrow(vertexConsumer, pose, tipX, tipY, tipZ, nx, ny, nz, lineWidth, arrowLength, baseArrowWidth, r, g, b, alpha);
						}
					}
				}
			}
			prev = p;
			idx++;
		}
	}

	void render(WorldRenderContext context, TrailController controller) {
		if (controller.getTrailPointCount() < 2) {
			return;
		}
		if (context.matrices() == null || context.consumers() == null) {
			return;
		}
		if (context.worldState() == null || context.worldState().cameraRenderState == null || context.worldState().cameraRenderState.pos == null) {
			return;
		}

		PoseStack poseStack = context.matrices();
		MultiBufferSource consumers = context.consumers();
		RenderType renderType = RenderTypes.lines();
		VertexConsumer vertexConsumer = consumers.getBuffer(renderType);
		float lineWidth = (float) controller.getLineWidth();
		modid$drawTrail(context, controller, vertexConsumer, lineWidth);

		if (controller.otherPlayersEnabled()) {
			int alpha = modid$opacityToAlpha(controller.getLineOpacity());
			for (TrailController.modid$OtherPlayerTrail trail : controller.getOtherPlayerTrails()) {
				if (trail.points.size() < 2) {
					continue;
				}
				VertexConsumer otherConsumer = consumers.getBuffer(renderType);
				modid$drawOtherTrail(context, trail, otherConsumer, lineWidth, alpha);
			}
		}
	}
}
