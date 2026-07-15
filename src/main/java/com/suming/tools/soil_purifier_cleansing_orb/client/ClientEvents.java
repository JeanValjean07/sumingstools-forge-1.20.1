package com.suming.tools.soil_purifier_cleansing_orb.client;

import com.suming.tools.soil_purifier_cleansing_orb.init.SoilPurifierModule;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sumingstools", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //注册投掷物渲染器
        event.registerEntityRenderer(
                SoilPurifierModule.CLEANSING_ORB.get(),
                ThrownItemRenderer::new
        );
    }
}