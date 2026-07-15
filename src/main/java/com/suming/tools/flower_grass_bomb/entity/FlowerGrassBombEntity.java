package com.suming.tools.flower_grass_bomb.entity;

import com.mojang.logging.LogUtils;
import com.suming.tools.flower_grass_bomb.init.FlowerGrassBombModule;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlowerGrassBombEntity extends ThrowableProjectile implements ItemSupplier {


    //SLF4J日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    //构造
    public FlowerGrassBombEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }
    public FlowerGrassBombEntity(LivingEntity shooter, Level level) {
        super(FlowerGrassBombModule.FLOWER_GRASS_BOMB.get(), shooter, level);
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
            LOGGER.debug("撞到方块！位置：({}, {}, {})", this.getX(), this.getY(), this.getZ());
            this.onHitBlock();
            //不继续执行,即为销毁
            return;
        }

        //超出边界销毁
        if (this.getY() < -64 || this.getY() > 320) {
            LOGGER.debug("超出世界边界，销毁");
            this.discard();
            return;
        }
        //超时销毁 100ticks = 5秒
        if (this.tickCount > 200) {
            LOGGER.debug("超时消失");
            this.discard();
        }
    }

    //撞击触发逻辑
    private void onHitBlock() {
        //落点位置
        BlockPos hitPos = this.blockPosition();
        Level level = this.level();

        LOGGER.info("宝珠在 ({}, {}, {}) 落地，开始种植花草", hitPos.getX(), hitPos.getY(), hitPos.getZ());

        //落地音效
        level.playSound(null, hitPos, SoundEvents.AMETHYST_BLOCK_CHIME  , SoundSource.NEUTRAL, 1.0F, 1.0F);

        //种植0
        plantFlowersAround(level, hitPos);

        //销毁实体
        this.discard();
    }


    private void plantFlowersAround(Level level, BlockPos hitPos) {
        //仅在服务端执行
        if (level.isClientSide()) {
            return;
        }
        //随机数生成器
        RandomSource random = level.random;
        //指定平面圆形范围和高度范围
        int radius = 5;
        int verticalRange = 2;

        //区分世界维度
        ResourceKey<Level> dimensionKey = level.dimension();
        if (dimensionKey == Level.OVERWORLD){
            List<BlockPos> plantablePositions = getPlantablePositions(level, hitPos, radius, verticalRange);

            plantablePositions.stream()
                    .filter(pos -> random.nextFloat() < 0.5f)
                    .forEach(pos -> {
                        if (plantFlower(level, pos, pos.below(), random)) {
                            LOGGER.info("在 ({}, {}, {}) 种植了主世界花草", pos.getX(), pos.getY(), pos.getZ());
                        }
                    });


        }else if(dimensionKey == Level.NETHER){
            List<BlockPos> plantablePositionsNether = getPlantablePositionsNether(level, hitPos, radius, verticalRange);

            for (BlockPos pos : plantablePositionsNether) {
                if (random.nextFloat() < 0.5f) {
                    if (plantFlowerNether(level, pos, pos.below(), random)) {
                        LOGGER.info("在 ({}, {}, {}) 种植了下界花草", pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }



        }else if(dimensionKey == Level.END){

        }else{

        }


    }

    //收集主世界可用种植位置
    private List<BlockPos> getPlantablePositions(Level level, BlockPos hitPos, int radius, int verticalRange) {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = hitPos.getX() - radius; x <= hitPos.getX() + radius; x++) {
            for (int y = hitPos.getY() - verticalRange; y <= hitPos.getY() + verticalRange; y++) {
                for (int z = hitPos.getZ() - radius; z <= hitPos.getZ() + radius; z++) {
                    BlockPos targetPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(targetPos);

                    // 检查目标位置是否是泥土
                    if (isDirtBlock(state)) {
                        // 检查上方是否为空
                        BlockPos abovePos = targetPos.above();
                        if (level.isEmptyBlock(abovePos)) {
                            positions.add(abovePos); // 存储可种植位置
                        }
                    }
                }
            }
        }

        return positions;
    }
    //收集下界可用种植位置
    private List<BlockPos> getPlantablePositionsNether(Level level, BlockPos hitPos, int radius, int verticalRange) {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = hitPos.getX() - radius; x <= hitPos.getX() + radius; x++) {
            for (int y = hitPos.getY() - verticalRange; y <= hitPos.getY() + verticalRange; y++) {
                for (int z = hitPos.getZ() - radius; z <= hitPos.getZ() + radius; z++) {
                    BlockPos targetPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(targetPos);
                    if (isDirtBlockNether(state)) {
                        //检查上方是否为空
                        BlockPos abovePos = targetPos.above();
                        if (level.isEmptyBlock(abovePos)) {
                            positions.add(abovePos);
                        }
                    }
                }
            }
        }

        return positions;
    }

    //快捷遍历操作方法(1.18+)
    private void traverseBlocks(BlockPos hitPos, int radius, int verticalRange){
        BlockPos.betweenClosedStream(
                hitPos.offset(-radius, -verticalRange, -radius),
                hitPos.offset(radius, verticalRange, radius)
        ).forEach(pos -> {    });

    }

    //检查落点是否为泥土
    private boolean isDirtBlock(BlockState state) {
        Block block = state.getBlock();
        //允许种植的泥土方块
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK;
    }
    //检查落点是否为下界土壤
    private boolean isDirtBlockNether(BlockState state) {
        Block block = state.getBlock();
        //允许种植的下界泥土方块
        return block == Blocks.CRIMSON_NYLIUM || block == Blocks.WARPED_NYLIUM;
    }


    //植物列表
    List<Block> flowers = List.of(
            Blocks.DANDELION,  //蒲公英 1
            Blocks.POPPY,      //虞美人  1
            Blocks.BLUE_ORCHID,  //兰花   1
            Blocks.ALLIUM,      //绒球葱   1
            Blocks.AZURE_BLUET,      //滨菊   1
            Blocks.RED_TULIP,       //红郁金香   1
            Blocks.ORANGE_TULIP,        //橙郁金香    1
            Blocks.WHITE_TULIP,        //白郁金香   1
            Blocks.PINK_TULIP,            //粉红色郁金香   1
            Blocks.OXEYE_DAISY,           //滨菊/牛津雏菊   1
            Blocks.CORNFLOWER,          //矢车菊 1
            Blocks.LILY_OF_THE_VALLEY,    //铃兰 1
            Blocks.SUNFLOWER,      //向日葵  2
            Blocks.LILAC,           //丁香  2
            Blocks.ROSE_BUSH,         //玫瑰丛  2
            Blocks.PEONY,        //牡丹  2
            Blocks.GRASS,         //草丛  1
            Blocks.FERN,        //蕨  1
            Blocks.TALL_GRASS,      //高草丛  2
            Blocks.LARGE_FERN        //大型蕨   2
    );
    private boolean plantFlower(Level level, BlockPos plantPos, BlockPos soilPos, RandomSource random) {

        //随机选择一种花
        Block flowerBlock;
        if (random.nextFloat() < 0.6f){
            //60%概率出杂草
            flowerBlock = Blocks.GRASS;
        }else{
            //40%概率选其他植物
            flowerBlock = flowers.get(random.nextInt(flowers.size()));
        }
        BlockState flowerState = flowerBlock.defaultBlockState();

        //检查是否能种植
        if (flowerState.canSurvive(level, plantPos)) {
            //放置植物
            level.setBlock(plantPos, flowerState, 3);

            return true;
        }

        return false;
    }




    //下界植物列表
    List<Block> netherFlowers = List.of(
            Blocks.WITHER_ROSE,            //凋零玫瑰 1
            Blocks.WARPED_ROOTS,            //诡异菌索 1
            Blocks.CRIMSON_ROOTS,           //绯红菌索  1
            Blocks.NETHER_SPROUTS,          //下界苗  1
            Blocks.CRIMSON_FUNGUS,          //绯红菌  1
            Blocks.WARPED_FUNGUS,            //诡异菌 1
            Blocks.RED_MUSHROOM,            //红蘑菇 1
            Blocks.BROWN_MUSHROOM            //棕色蘑菇 1
    );
    private boolean plantFlowerNether(Level level, BlockPos plantPos, BlockPos soilPos, RandomSource random) {
        //随机选择一种花
        Block flowerBlock;
        //60%概率种植
        if (random.nextFloat() < 0.6f){
            flowerBlock = netherFlowers.get(random.nextInt(netherFlowers.size()));
            BlockState flowerState = flowerBlock.defaultBlockState();

            //检查是否能种植
            if (flowerState.canSurvive(level, plantPos)) {
                //放置植物
                level.setBlock(plantPos, flowerState, 3);

                return true;
            }

        }

        return false;
    }



    //返回投掷物使渲染器显示
    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(FlowerGrassBombModule.FLOWER_GRASS_BOMB_ITEM.get());
    }
    @Override
    protected void defineSynchedData() {  }



}
