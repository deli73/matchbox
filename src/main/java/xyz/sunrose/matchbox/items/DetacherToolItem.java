package xyz.sunrose.matchbox.items;

import net.minecraft.block.*;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import xyz.sunrose.matchbox.Matchbox;

public class DetacherToolItem extends Item {
    public DetacherToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.contains(ConnectingBlock.NORTH) &&
                blockState.contains(ConnectingBlock.SOUTH) &&
                blockState.contains(ConnectingBlock.EAST) &&
                blockState.contains(ConnectingBlock.WEST)
        ) {
            Direction side = hitSide(context);
            if (  blockState.get(ConnectingBlock.FACING_PROPERTIES.get(side)) ) { // only execute if the relevant side has a connection
                //detach the relevant side
                BlockState finalState = detachSide(world, blockState, blockPos, side);
                world.setBlockState(blockPos, finalState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE); //flags to not update neighbors

                world.playSound(
                        player, blockPos, Matchbox.SAW_SOUND,
                        SoundCategory.BLOCKS, 1f, 0.8f + 0.01f * world.random.nextFloat()
                );

                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
                return ActionResult.SUCCESS;
            }
        }

        else if(blockState.contains(Properties.NORTH_WALL_SHAPE) &&
                blockState.contains(Properties.SOUTH_WALL_SHAPE) &&
                blockState.contains(Properties.WEST_WALL_SHAPE) &&
                blockState.contains(Properties.EAST_WALL_SHAPE)) {
            Direction side = hitSide(context);
            EnumProperty<WallShape> sideToCheck = getWallShapeEnumProperty(side);
            if (  blockState.get(sideToCheck) != WallShape.NONE) { // only execute if the relevant side has a connection
                //detach the relevant side
                BlockState finalState = detachWallSide(world, blockState, blockPos, side, sideToCheck);
                world.setBlockState(blockPos, finalState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE); //flags to not update neighbors

                world.playSound(
                        player, blockPos, Matchbox.SAW_SOUND,
                        SoundCategory.BLOCKS, 1f, 0.8f + 0.01f * world.random.nextFloat()
                );

                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
                return ActionResult.SUCCESS;
            }
        }

        return super.useOnBlock(context);
    }

    private static @Nullable EnumProperty<WallShape> getWallShapeEnumProperty(Direction side) {
        EnumProperty<WallShape> sideToCheck = null;
        switch (side) {
            case DOWN, UP -> throw new IllegalArgumentException("Matchbox getting invalid side input in wall's useOnBlock");
            case NORTH -> sideToCheck = Properties.NORTH_WALL_SHAPE;
            case SOUTH -> sideToCheck = Properties.SOUTH_WALL_SHAPE;
            case WEST -> sideToCheck = Properties.WEST_WALL_SHAPE;
            case EAST -> sideToCheck = Properties.EAST_WALL_SHAPE;
        }
        return sideToCheck;
    }

    private BlockState detachSide(World world, BlockState state, BlockPos pos, Direction dir){
        // set the state to false
        BlockState newState = state.with(ConnectingBlock.FACING_PROPERTIES.get(dir), false);
        // set the neightbor's opposite-direction state to false if applicable
        BlockState neighbor = world.getBlockState(pos.offset(dir));
        if (neighbor.getBlock() instanceof HorizontalConnectingBlock) {
            Direction opposite = dir.getOpposite();
            world.setBlockState(pos.offset(dir), neighbor.with(ConnectingBlock.FACING_PROPERTIES.get(opposite), false), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        }
        return newState;
    }

    private BlockState detachWallSide(World world, BlockState state, BlockPos pos, Direction dir, EnumProperty<WallShape> side) {
        //TODO fix vertical stuff

        // disconnect the wall on our side...
        BlockState newState =  state.contains(WallBlock.UP) ? state.with(side, WallShape.NONE).with(WallBlock.UP, true) : state.with(side, WallShape.NONE);
        BlockState neighbor = world.getBlockState(pos.offset(dir));
        if (neighbor.getBlock() instanceof WallBlock) {
            world.setBlockState(
                    pos.offset(dir),
                     neighbor.contains(WallBlock.UP) ? neighbor.with(oppositeSide(dir), WallShape.NONE).with(WallBlock.UP, true) :
                             neighbor.with(oppositeSide(dir), WallShape.NONE),
                    Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        }
        return newState;
    }

    private EnumProperty<WallShape> oppositeSide(Direction side) {
        switch (side) {
            case NORTH -> {
                return WallBlock.SOUTH_SHAPE;
            }
            case SOUTH -> {
                return WallBlock.NORTH_SHAPE;
            }
            case WEST -> {
                return WallBlock.EAST_SHAPE;
            }
            case EAST -> {
                return WallBlock.WEST_SHAPE;
            }
            default -> throw new IllegalArgumentException("Matchbox getting invalid input to oppositeSide");
        }
    }

    private Direction hitSide(ItemUsageContext context) {
        Vec3d pos = context.getHitPos();
        //position relative to center of block
        double relX = MathHelper.fractionalPart(pos.x) - 0.5;
        double relZ = MathHelper.fractionalPart(pos.z) - 0.5;
        double absX = MathHelper.sign(relX) * relX;
        double absZ = MathHelper.sign(relZ) * relZ;

        if (relX < 0 && absX >= absZ) { //negative X - West
            return Direction.WEST;
        }
        else if (relX >= 0 && absX >= absZ) { //positive X - East
            return Direction.EAST;
        }
        else if (relZ < 0 && absZ > absX) { //negative Z - North
            return Direction.NORTH;
        }
        else if (relZ >= 0 && absZ > absX) { //positive Z - South
            return Direction.SOUTH;
        }
        else { // ????
            Matchbox.LOGGER.debug("Invalid hit location, defaulting to north");
            return Direction.NORTH;
        }

    }
}
