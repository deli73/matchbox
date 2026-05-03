package xyz.sunrose.matchbox;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class MatchboxBlockTagProvider extends FabricTagProvider<Block> {
    public static final TagKey<Block> LIGHTABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Matchbox.id("matchbox_lightable"));

    /**
     * Constructs a new {@link FabricTagProvider} with the default computed path.
     *
     * <p>Common implementations of this class are provided.
     *
     * @param output           the {@link FabricDataOutput} instance
     * @param registryKey
     * @param registriesFuture the backing registry for the tag type
     */
    public MatchboxBlockTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<Block>> registryKey, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registryKey, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getTagBuilder(LIGHTABLE_BLOCKS)
                .addOptionalTag(BlockTags.CAMPFIRES.id())
                .addOptionalTag(BlockTags.CANDLES.id())
                .addOptionalTag(BlockTags.CANDLE_CAKES.id())
                // SUPPLEMENTARIES BLOCKS
                .addOptionalTag(Identifier.of("supplementaries","candle_holders")) //Candle holders
                .addOptional(Identifier.of("supplementaries", "gunpowder")) //Placable gunpowder
                .addOptionalTag(Identifier.of("supplementaries", "sconces")); //Sconces
//                // BBB BLOCKS
//                .addOptionalTag(Identifier.of("bbb", "braziers")); //Braziers
                //.setReplace(true);

    }
}
