package xyz.sunrose.matchbox;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.sunrose.matchbox.blocks.MatchboxBlocks;
import xyz.sunrose.matchbox.items.MatchboxItems;

public class Matchbox implements ModInitializer {
	public static final String MODID = "matchbox";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String name) {return Identifier.of(MODID, name);}

	public static final Identifier SAW_SOUND_ID = Identifier.of(MODID, "saw");
	public static SoundEvent SAW_SOUND = Registry.register(
			Registries.SOUND_EVENT, SAW_SOUND_ID, SoundEvent.of(SAW_SOUND_ID)
	);

	public static final TagKey<Block> LIGHTABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Matchbox.id("matchbox_lightable"));

	@Override
	public void onInitialize() {
		MatchboxItems.init();
		MatchboxBlocks.init();
	}
}
