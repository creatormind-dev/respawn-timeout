package dev.creatormind.respawntimeout;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RespawnTimeoutMod implements ModInitializer {

    public static final String MOD_ID = "respawn-timeout";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        LOGGER.info("[Respawn Timeout] Initializing");
    }

}
