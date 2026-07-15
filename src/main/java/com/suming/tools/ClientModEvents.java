package com.suming.tools;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import static com.suming.tools.SuMingsTools.MODID;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        /*
        //注册实体渲染器
        event.enqueueWork(() -> {
            EntityRenderers.register(
                    SoilPurifierModule.CLEANSING_ORB.get(),
                    ThrownCleansingOrbRenderer::new
            );
        });

         */
    }
}

