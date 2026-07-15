package com.suming.tools.flower_grass_bomb.init;

import com.suming.tools.IModule;
import com.suming.tools.SuMingsTools;
import com.suming.tools.flower_grass_bomb.entity.FlowerGrassBombEntity;
import com.suming.tools.flower_grass_bomb.item.FlowerGrassBombItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FlowerGrassBombModule implements IModule {

    //注册实体
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SuMingsTools.MODID);
    public static final RegistryObject<EntityType<FlowerGrassBombEntity>> FLOWER_GRASS_BOMB = ENTITIES.register("flower_grass_bomb",
            () -> EntityType.Builder.<FlowerGrassBombEntity>of(
                            FlowerGrassBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("flower_grass_bomb"));

    //注册物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SuMingsTools.MODID);
    public static final RegistryObject<Item> FLOWER_GRASS_BOMB_ITEM = ITEMS.register("flower_grass_bomb", () -> new FlowerGrassBombItem(new Item.Properties().stacksTo(16)));


    //实现IModule
    @Override
    public void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
    }

}
