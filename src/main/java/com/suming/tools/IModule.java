package com.suming.tools;

import net.minecraftforge.eventbus.api.IEventBus;

public interface IModule {
    // 每个模块都需要实现这个方法，来注册自己的东西
    void register(IEventBus modEventBus);
}
