package xyz.sunrose.matchbox.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xyz.sunrose.matchbox.Matchbox;

public class MatchboxToolItem extends Item {
    public MatchboxToolItem(Settings settings) {
        super(settings);
    }

    boolean canLight(BlockState state) {
        if(state.contains(Properties.WATERLOGGED)){
            return !state.get(Properties.WATERLOGGED) && !state.get(Properties.LIT);
        }
        return !state.get(Properties.LIT);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        // basically copied from flint ant steel
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        //modifying to use block tag and lit state
        boolean isInTag = blockState.isIn(Matchbox.LIGHTABLE_BLOCKS);
        if(isInTag && blockState.contains(Properties.LIT) && canLight(blockState)) {
            // TODO custom sound
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlockState(blockPos, blockState.with(Properties.LIT, true),
                    Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS
            );
            world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
            if (playerEntity != null) {
                context.getStack().damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
