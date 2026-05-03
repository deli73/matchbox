package xyz.sunrose.matchbox.blocks;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import xyz.sunrose.matchbox.Matchbox;

import java.util.function.Function;

public class MatchboxBlocks {
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem){
        // Create a registry key for the block
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Matchbox.MODID, name));
        // Create the block instance
        Block block = blockFactory.apply(settings);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Matchbox.MODID, name));

            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    public static final Block MESH = register(
            "mesh", MeshBlock::new,
            AbstractBlock.Settings.copy(Blocks.CHAIN).strength(4,6).nonOpaque(),
            true
    );

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(MESH.asItem()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(MESH.asItem()));
    }

    public static void clientInit() {
        //net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap.putBlock(MESH, BlockRenderLayer.CUTOUT_MIPPED);
        BlockRenderLayerMap.INSTANCE.putBlock(MESH, RenderLayer.getCutoutMipped());
    }
}
