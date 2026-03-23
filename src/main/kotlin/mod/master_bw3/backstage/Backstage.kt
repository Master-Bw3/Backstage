package mod.master_bw3.backstage

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Backstage : ModInitializer {
    private val logger = LoggerFactory.getLogger("backstage")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
	}
}