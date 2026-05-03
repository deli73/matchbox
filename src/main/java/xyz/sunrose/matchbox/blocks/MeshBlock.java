package xyz.sunrose.matchbox.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MeshBlock extends Block {
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0, 14, 0, 16, 16, 16);
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public MeshBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(HALF, BlockHalf.TOP).with(ENABLED, true));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide().getOpposite();
        BlockHalf half;
        if(direction.equals(Direction.DOWN)) {half = BlockHalf.BOTTOM;}
        else if(direction.equals(Direction.UP)) {half = BlockHalf.TOP;}
        else {
            double verticality = ctx.getHitPos().y - ctx.getBlockPos().getY();
            if (verticality < 0.5) {
                half = BlockHalf.BOTTOM;
            } else {
                half = BlockHalf.TOP;
            }
        }

        return this.getDefaultState().with(HALF,half);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        updateEnabled(world, pos, state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = switch (state.get(HALF)) {
            case TOP -> TOP_SHAPE;
            case BOTTOM -> BOTTOM_SHAPE;
        };
        if(context instanceof EntityShapeContext entityContext) {
            if(entityContext.getEntity() instanceof ItemEntity && state.get(ENABLED)) { //if it's an item and above the mesh, and the mesh is enabled,
                return VoxelShapes.empty(); //let it pass through
            }
        }
        return shape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getCollisionShape(state, world, pos, context);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, ENABLED);
    }

    //hopper borrow
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        updateEnabled(world, pos, state);
    }

    public void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean redstoneEnable = !world.isReceivingRedstonePower(pos);
        if (redstoneEnable != state.get(ENABLED)) {
            world.setBlockState(pos, state.with(ENABLED, redstoneEnable));
        }
    }
}
