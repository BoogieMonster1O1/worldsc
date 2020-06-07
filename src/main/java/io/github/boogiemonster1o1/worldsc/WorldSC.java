package io.github.boogiemonster1o1.worldsc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Level;

import static io.github.boogiemonster1o1.worldsc.WorldSCInit.log;
import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public class WorldSC implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        log(Level.INFO,"Starting World Source Control...");
    }
}
