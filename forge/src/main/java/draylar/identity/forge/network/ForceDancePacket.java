package draylar.identity.forge.network;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record ForceDancePacket(BlockPos pos) {
    public static final String ID = "force_dance";

    public static void sendToServer(BlockPos pos) {
        NetworkHandler.CHANNEL.sendToServer(new ForceDancePacket(pos));
    }

    public static void sendToClients(Entity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                    new ForceDancePacket(entity.getBlockPos())
            );
        }
    }

    public static void handle(ForceDancePacket packet, ServerPlayerEntity sender) {
        World world = sender.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            for (Entity entity : serverWorld.getEntitiesByType(AMEntityRegistry.COCKROACH.get(), e -> e.getBlockPos().equals(packet.pos))) {
                if (entity instanceof ForceDanceAccessor accessor) {
                    accessor.identity$startForceDance();
                }
            }
        }
    }

    public static void handleClient(ForceDancePacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world != null) {
            for (Entity entity : world.getEntities()) {
                if (entity.getBlockPos().equals(packet.pos) && entity instanceof EntityCockroach cockroach) {
                    cockroach.setNearbySongPlaying(packet.pos, true);
                    cockroach.setDancing(true);
                }
            }
        }
    }


    public static ForceDancePacket read(PacketByteBuf buf) {
        return new ForceDancePacket(buf.readBlockPos());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}

