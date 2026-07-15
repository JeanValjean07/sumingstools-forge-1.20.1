package com.suming.tools;

import com.mojang.logging.LogUtils;
import com.suming.tools.flower_grass_bomb.entity.FlowerGrassBombEntity;
import com.suming.tools.flower_grass_bomb.init.FlowerGrassBombModule;
import com.suming.tools.flower_grass_bomb.item.FlowerGrassBombItem;
import com.suming.tools.soil_purifier_cleansing_orb.client.renderer.ThrownCleansingOrbRenderer;
import com.suming.tools.soil_purifier_cleansing_orb.entity.ThrownCleansingOrb;
import com.suming.tools.soil_purifier_cleansing_orb.event.SoilPurifierEvents;
import com.suming.tools.soil_purifier_cleansing_orb.init.SoilPurifierModule;
import com.suming.tools.soil_purifier_cleansing_orb.item.CleansingOrbItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod(SuMingsTools.MODID)
public class SuMingsTools {

    //ID,用例:游戏内部ID:sumingstools:cleansing_orb
    public static final String MODID = "sumingstools";
    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    //初始化注册器
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    //注册模组物品
    //土壤净化球
    public static final RegistryObject<Item> CLEANSING_ORB_ITEM = ITEMS.register("cleansing_orb", () -> new CleansingOrbItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<EntityType<ThrownCleansingOrb>> CLEANSING_ORB = ENTITIES.register("cleansing_orb",
                    () -> EntityType.Builder.<ThrownCleansingOrb>of(ThrownCleansingOrb::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .build("cleansing_orb"));
    //花草球
    public static final RegistryObject<Item> FLOWER_GRASS_BOMB_ITEM = ITEMS.register("flower_grass_bomb", () -> new FlowerGrassBombItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<EntityType<FlowerGrassBombEntity>> FLOWER_GRASS_BOMB = ENTITIES.register("flower_grass_bomb",
            () -> EntityType.Builder.<FlowerGrassBombEntity>of(FlowerGrassBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("flower_grass_bomb"));




    //构造函数
    public SuMingsTools(FMLJavaModLoadingContext context) {
        //获取Forge事件总线
        IEventBus modEventBus = context.getModEventBus();

        //注册commonSetup
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new SoilPurifierEvents());

        //将基本类型注册器注册进事件总线
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITIES.register(modEventBus);

        //modEventBus.addListener(this::onClientSetup);

        //
        MinecraftForge.EVENT_BUS.register(this);

        //注册addCreative
        modEventBus.addListener(this::addCreative);

        //
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }



    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("入口类 commonSetup");

        if (Config.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.magicNumberIntroduction, Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    //填充创造模式物品栏
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //放进工具与实用物品页签
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SoilPurifierModule.CLEANSING_ORB_ITEM.get());
            event.accept(FlowerGrassBombModule.FLOWER_GRASS_BOMB_ITEM.get());

            LOGGER.info("将物品添加到工具与实用物品清单");
        }

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("模组在服务端启动了");
    }


}
