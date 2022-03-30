package xyz.sunrose.matchbox;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.sunrose.matchbox.items.MatchboxItems;

public class Matchbox implements ModInitializer {
	public static final String MODID = "matchbox";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		MatchboxItems.init();
	}
}
