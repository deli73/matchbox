package xyz.sunrose.matchbox.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LightMeterItem extends Item {
    private boolean lightSent = false;
    public LightMeterItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if(selected) {
                int sunLight = world.getLightLevel(LightType.SKY, entity.getBlockPos());
                int blockLight = world.getLightLevel(LightType.BLOCK, entity.getBlockPos());
                MutableText message = Text.translatable("item.matchbox.lightmeter.readout", sunLight, blockLight);
                player.sendMessage(message, true);
                lightSent = true;
            }
        }
        else {
            if(world.isClient && lightSent && !selected && entity instanceof PlayerEntity player){
                player.sendMessage(Text.empty(),true);
            }
            lightSent = false;
        }
    }
}