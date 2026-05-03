package xyz.sunrose.matchbox.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import xyz.sunrose.matchbox.Matchbox;

import java.util.function.Function;

public class MatchboxItems {
    public static Identifier id(String name) {
        return Identifier.of(Matchbox.MODID, name);
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        return Registry.register(Registries.ITEM, id(name), itemFactory.apply(settings));
    }

    public static Item MATCHBOX = register(
            "matchbox", MatchboxToolItem::new,
            new Item.Settings().maxDamage(64)
    );

    public static Item DETACHER = register(
            "detacher", DetacherToolItem::new,
            new Item.Settings().maxCount(1)
    );

    public static Item WOOD_GLUE = register(
            "wood_glue", WoodGlueItem::new,
            new Item.Settings().maxDamage(64)
    );

    public static final Item ALTIMETER = register(
           "altimeter", AltimeterItem::new,
            new Item.Settings().maxCount(1)
    );

    public static final Item LIGHTMETER = register(
            "lightmeter", LightMeterItem::new,
            new Item.Settings().maxCount(1)
    );

    /*public static Item REDSTONE_TOOL = Registry.register(
            Registry.ITEM, new Identifier(Matchbox.MODID, "redstone_tool"),
            new RedstoneToolItem(new FabricItemSettings().maxCount(1).group(ItemGroup.REDSTONE))
    );*/

    public static void init () {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(MATCHBOX);
            entries.add(DETACHER);
            entries.add(WOOD_GLUE);
            entries.addAfter(Items.CLOCK, ALTIMETER);
            entries.add(LIGHTMETER);
        });
    }

    public static void clientInit() {
//        ModelPredicateProviderRegistry.register(
//                ALTIMETER, new Identifier("alt"),
//                (stack, world, entity, seed) -> entity != null ? remap(-64, 320, entity.getY()) : 0.0F
//        );
    }

    private static float remap(double minIn, double maxIn, double value) {
        double diffAbove = value - minIn;
        double range = maxIn - minIn;
        double diff = diffAbove / range;
        if(diff < 0.0f) diff = 0.0f;
        if(diff > 1.0f) diff = 1.0f;
        return (float) diff;
    }
}
