package xyz.sunrose.matchbox.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AltimeterItem extends Item {
    private static final int SEA_LEVEL = 62;

    public AltimeterItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        int y = (int) user.getY();
        if (!world.isClient) {
            MutableText message;
            if(y == SEA_LEVEL) {
                message = Text.translatable("item.matchbox.altimeter.readout.sea", SEA_LEVEL);
            } else if (y < SEA_LEVEL) {
                int below = SEA_LEVEL - y;
                message = Text.translatable("item.matchbox.altimeter.readout.below", below, y);
            } else {
                int above = y - SEA_LEVEL;
                message = Text.translatable("item.matchbox.altimeter.readout.above", above, y);
            }
            user.sendMessage(message, true);
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}