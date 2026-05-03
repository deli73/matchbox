package xyz.sunrose.matchbox.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xyz.sunrose.matchbox.Matchbox;
import xyz.sunrose.matchbox.mixin.AccessorAxeItem;

import java.util.Map;

public class WoodGlueItem extends Item {

    public WoodGlueItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        Map<Block, Block> strippedBlocks = AccessorAxeItem.getStrippedBlocks();
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if(strippedBlocks.containsValue(blockState.getBlock())) {
            // TODO custom sound
            world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f);

            BlockState newState = getUnstrippedBlock(blockState, strippedBlocks);
            world.setBlockState(blockPos, newState,
                    Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS
            );

            //standard stuff
            world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
            if (playerEntity != null) {
                context.getStack().damage(1, playerEntity, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }



    private BlockState getUnstrippedBlock(BlockState state, Map<Block, Block> strippedBlocks) {
        Block block = state.getBlock();
        if (!strippedBlocks.containsValue(block)) {
            return state;
        }
        for (Map.Entry<Block, Block> entry : strippedBlocks.entrySet()) {
            if (entry.getValue().equals(block)) {
                Block newBlock = entry.getKey();
                return newBlock.getStateWithProperties(state);
            }
        }
        Matchbox.LOGGER.error("This shouldn't be possible??");
        return null;
    }


}
