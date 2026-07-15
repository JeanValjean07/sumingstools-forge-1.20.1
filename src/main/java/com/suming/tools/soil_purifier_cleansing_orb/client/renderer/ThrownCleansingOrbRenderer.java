package com.suming.tools.soil_purifier_cleansing_orb.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.suming.tools.soil_purifier_cleansing_orb.entity.ThrownCleansingOrb;
import com.suming.tools.soil_purifier_cleansing_orb.init.SoilPurifierModule;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;

import static com.suming.tools.SuMingsTools.MODID;


@OnlyIn(Dist.CLIENT)
public class ThrownCleansingOrbRenderer extends EntityRenderer<ThrownCleansingOrb> {

    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    //构造
    public ThrownCleansingOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    //纹理位置
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/cleansing_orb.png");
    //渲染
    @Override
    public void render(ThrownCleansingOrb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        //旋转-始终面向玩家
        // 获取相机方向
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.position();
        Vec3 direction = cameraPos.subtract(entityPos).normalize();

        // 计算旋转
        float yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
        float pitch = (float) Math.toDegrees(Math.asin(direction.y));

        // 应用旋转
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw + 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        //this.entityRenderDispatcher.getRenderer(entity);
        //poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() + 180.0F));
        //poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot()));

        //缩放
        float scale = 0.5F;
        poseStack.scale(scale, scale, scale);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        //VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        // 绘制一个面向玩家的平面
        this.renderFlatItem(poseStack, vertexConsumer, packedLight);


        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderFlatItem(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float halfSize = 0.5F;

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // ★ 使用浮点颜色
        // 顶点顺序：左上、右上、右下、左下
        consumer.vertex(matrix, -halfSize, -halfSize, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0, 0, 1)
                .endVertex();

        consumer.vertex(matrix, halfSize, -halfSize, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0, 0, 1)
                .endVertex();

        consumer.vertex(matrix, halfSize, halfSize, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0, 0, 1)
                .endVertex();

        consumer.vertex(matrix, -halfSize, halfSize, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0, 0, 1)
                .endVertex();
    }

    private void renderSprite(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight) {
        Matrix4f matrix = poseStack.last().pose();

        float halfSize = 0.5F;

        LOGGER.info("66666666666");
        // 注意：使用 RenderType.text() 可能不需要法线，但仍建议保留 overlayCoords
        vertexConsumer.vertex(matrix, -halfSize, -halfSize, 0)
                .uv(0, 1)
                .color(255, 255, 255, 255)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 0, 1)
                .endVertex();
        LOGGER.info("77777777777777");
        vertexConsumer.vertex(matrix, halfSize, -halfSize, 0)
                .uv(1, 1)
                .color(255, 255, 255, 255)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .endVertex();
        LOGGER.info("88888888888");
        vertexConsumer.vertex(matrix, halfSize, halfSize, 0)
                .uv(1, 0)
                .color(255, 255, 255, 255)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .endVertex();
        LOGGER.info("99999999999999");
        vertexConsumer.vertex(matrix, -halfSize, halfSize, 0)
                .uv(0, 0)
                .color(255, 255, 255, 255)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .endVertex();
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownCleansingOrb entity) {
        return TEXTURE;
    }

}

