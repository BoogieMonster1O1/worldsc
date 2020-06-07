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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.spi.DateFormatProvider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;

import static java.io.File.separator;
import static net.fabricmc.api.EnvType.CLIENT;
import static net.minecraft.client.MinecraftClient.getInstance;

@Environment(CLIENT)
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
        super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
    }

    @Inject(method = "save", at = @At( value = "INVOKE",ordinal = 0,target = "Lnet/minecraft/server/world/ServerWorld;saveLevel()V"))
    public void gitSourceControl(CallbackInfo ci){
        Logger LOG = LogManager.getLogger(WorldSC.class);
        Runtime rt = Runtime.getRuntime();
        String worldName;
        String ignoreFile;
        String worldDir = getInstance().runDirectory.toString() + separator + "saves";
        try{
            worldName = getInstance().getServer().getLevelName();
            worldDir += worldName + separator;
            ignoreFile = worldDir + ".gitignore";
            File gitignore = new File(ignoreFile);
            if(gitignore.exists()){
                LOG.info("Committing changes");
                rt.exec("cd " + worldDir + " &&" + "git add . && git commit -m \""+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) +"\"");
            }
            else{
                boolean created = gitignore.createNewFile();
                if(!created){
                    throw new IOException("Could not create gitignore");
                }
                FileWriter gitignoreWrite = new FileWriter(gitignore);
                PrintWriter gitignorePrint = new PrintWriter(gitignoreWrite);
                gitignorePrint.println("# Gitignore created by World Source Control");
                gitignorePrint.println("# Add files that you think should be excluded from World Source Control");
                gitignorePrint.println("# Do not remove .DS_Store if you're on a mac");
                gitignorePrint.println();
                gitignorePrint.println(".DS_Store");
                gitignorePrint.flush();
                gitignorePrint.close();
                gitignoreWrite.flush();
                gitignoreWrite.close();
                LOG.info("Initializing git repository");
                rt.exec("cd " + worldDir + " &&" + "git init && git add . && git commit -m \"Initial Commit\"");
            }
        } catch (NullPointerException ignored) {
            LOG.info("Server seems to have stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
