package com.suming.tools.soil_purifier_cleansing_orb.item;

import com.mojang.logging.LogUtils;
import com.suming.tools.soil_purifier_cleansing_orb.entity.ThrownCleansingOrb;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CleansingOrbItem extends Item {

    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    //构造
    public CleansingOrbItem(Properties properties) {
        super(properties);
    }

    //右键点击触发
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        LOGGER.info("投掷物品(右键点击物品触发use)");

        //获取玩家当前手中的物品堆叠
        ItemStack heldStack = player.getItemInHand(hand);
        //在服务端执行投掷逻辑
        if (!level.isClientSide) {
            //创建投掷物实体
            ThrownCleansingOrb orb = new ThrownCleansingOrb(player, level);
            orb.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            orb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.0F, 0.0F);
            level.addFreshEntity(orb);

            //播放音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F);

            //消耗物品(仅在非创造模式时)
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(heldStack, level.isClientSide());
    }
}