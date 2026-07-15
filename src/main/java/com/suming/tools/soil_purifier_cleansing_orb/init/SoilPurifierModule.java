package com.suming.tools.soil_purifier_cleansing_orb.init;

import com.suming.tools.IModule;
import com.suming.tools.SuMingsTools;
import com.suming.tools.soil_purifier_cleansing_orb.entity.ThrownCleansingOrb;
import com.suming.tools.soil_purifier_cleansing_orb.item.CleansingOrbItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoilPurifierModule implements IModule {

    //注册实体
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SuMingsTools.MODID);
    public static final RegistryObject<EntityType<ThrownCleansingOrb>> CLEANSING_ORB = ENTITIES.register("cleansing_orb",
                    () -> EntityType.Builder.<ThrownCleansingOrb>of(
                                    ThrownCleansingOrb::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .build("cleansing_orb"));

    //注册物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SuMingsTools.MODID);
    public static final RegistryObject<Item> CLEANSING_ORB_ITEM = ITEMS.register("cleansing_orb", () -> new CleansingOrbItem(new Item.Properties().stacksTo(16)));


    //实现IModule
    @Override
    public void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
    }


}