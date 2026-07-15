package com.suming.tools.soil_purifier_cleansing_orb.entity;

import com.mojang.logging.LogUtils;
import com.suming.tools.soil_purifier_cleansing_orb.init.SoilPurifierModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class ThrownCleansingOrb extends ThrowableProjectile implements ItemSupplier {

    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    //构造
    public ThrownCleansingOrb(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }
    public ThrownCleansingOrb(LivingEntity shooter, Level level) {
        super(SoilPurifierModule.CLEANSING_ORB.get(), shooter, level);
    }


    //核心功能逻辑
    @Override
    public void tick() {
        LOGGER.info("投掷物品(触发tick()方法)");
        //直线飞行
        this.setNoGravity(true);

        //获取速度
        Vec3 velocity = this.getDeltaMovement();

        //用move()方法移动时会自动处理碰撞检测
        this.move(MoverType.SELF, velocity);

        //设置移动阻力(逐渐减速)
        this.setDeltaMovement(velocity.scale(0.99F));

        //检测方块碰撞
        if (this.horizontalCollision || this.verticalCollision) {
            LOGGER.debug("宝珠撞到方块！位置：({}, {}, {})", this.getX(), this.getY(), this.getZ());
            this.onHitBlock();
            //不继续执行,即为销毁
            return;
        }

        //超出边界
        if (this.getY() < -64 || this.getY() > 320) {
            LOGGER.debug("宝珠超出世界边界，销毁");
            this.discard();
            return;
        }
        //设置超时消失
        if (this.tickCount > 200) {  //20ticks = 1秒,100ticks = 5秒,
            LOGGER.debug("宝珠超时消失");
            this.discard();
        }
    }

    private void onHitBlock() {
        //落点位置
        BlockPos hitPos = this.blockPosition();
        Level level = this.level();

        LOGGER.info("宝珠在 ({}, {}, {}) 落地，开始净化土壤", hitPos.getX(), hitPos.getY(), hitPos.getZ());

        //落地音效
        level.playSound(null, hitPos, SoundEvents.AMETHYST_BLOCK_CHIME  , SoundSource.NEUTRAL, 1.0F, 1.0F);

        //遍历半径
        int range = 5;
        int replacedCount = 0;

        //遍历半径内所有方块
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos checkPos = hitPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    //检测是否需替换
                    if (isSoilNeedsCleansing(block)) {
                        //执行替换为普通泥土GRASS_BLOCK
                        level.setBlock(checkPos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                        replacedCount++;
                        LOGGER.debug("净化土壤：{} 位置 ({}, {}, {})",
                                block.getName().getString(),
                                checkPos.getX(), checkPos.getY(), checkPos.getZ());
                    }
                }
            }
        }

        LOGGER.info("净化完成！共替换了 {} 个土壤方块", replacedCount);

        //销毁实体
        this.discard();
    }

    //土壤方块列表
    private boolean isSoilNeedsCleansing(Block block) {
        return block == Blocks.COARSE_DIRT ||       //粗泥
                block == Blocks.DIRT ||            //泥土
                block == Blocks.PODZOL ||            //灰化土
                block == Blocks.ROOTED_DIRT ||       //缠根泥土
                block == Blocks.GRASS_BLOCK;       //草方块
    }
    //block == Blocks.DIRT_PATH        //土径
    //block == Blocks.FARMLAND         //耕地
    //block == Blocks.MYCELIUM ||          //菌丝

    //音效列表
    private static final List<SoundEvent> NOTE_SOUNDS = List.of(
            SoundEvents.BELL_BLOCK,        // 钟琴 - 神圣
            SoundEvents.AMETHYST_BLOCK_CHIME    // 风铃 - 空灵
            //SoundEvents.NOTE_BLOCK_FLUTE      // 长笛 - 悠扬
            //SoundEvents.NOTE_BLOCK_PLING      // 电钢琴 - 清脆
            //SoundEvents.NOTE_BLOCK_XYLOPHONE,   // 木琴 - 轻快
            //SoundEvents.NOTE_BLOCK_HARP        // 竖琴 - 柔和
            //SoundEvents.NOTE_BLOCK_BANJO,       // 班卓琴 - 欢快
            //SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE // 铁琴 - 明亮

    );

    //返回投掷物使渲染器显示
    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(SoilPurifierModule.CLEANSING_ORB_ITEM.get());
    }

    @Override
    protected void defineSynchedData() {  }

}
