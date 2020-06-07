package io.github.boogiemonster1o1.worldsc.mixin;

import io.github.boogiemonster1o1.worldsc.WorldSC;
import net.fabricmc.api.Environment;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

import static net.fabricmc.api.EnvType.CLIENT;
import static net.minecraft.client.MinecraftClient.getInstance;

@Environment(CLIENT)
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
        super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
    }

    @Inject(method = "save", at = @At( value = "INVOKE",ordinal = 0,target = "Lnet/minecraft/server/world/ServerWorld;saveLevel()V"))
    public void doesItWork(CallbackInfo ci){
        Logger LOG = LogManager.getLogger(WorldSC.class);
        Runtime rt = Runtime.getRuntime();
        String worldName;
        String worldDir = getInstance().runDirectory.toString() + File.separator + "saves";
        String dotGitDir = getInstance().runDirectory.toString() + File.separator + "saves";
        try{
            worldName = getInstance().getServer().getLevelName();
            dotGitDir += worldName + ".git";
            File dotGitFile = new File(dotGitDir);
            if(dotGitFile.exists()){
                LOG.info(".git folder exists, not reinitalizing");
                rt.exec("cd " + worldDir + " &&" + "git add . && git commit -m \"\"");
            }
            else{

            }
        } catch (NullPointerException ignored) {
            LOG.info("Server seems to have stopped");
        } catch (IOException e) {
            e.printStackTrace();
            LOG.info("Could not commit changes!");
        }
    }
}
