package draylar.identity.forge.network;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkDirection;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new Identifier("identity", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void registerPackets() {
        CHANNEL.registerMessage(
                id++,
                ForceDancePacket.class,
                ForceDancePacket::write,
                ForceDancePacket::read,
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        ServerPlayerEntity sender = ctx.get().getSender();
                        if (sender != null) {
                            ServerWorld world = sender.getServerWorld();
                            BlockPos pos = ((ForceDancePacket) msg).pos(); // ✅ cast correctly here
                            for (Entity entity : world.getEntitiesByType(AMEntityRegistry.COCKROACH.get(), e -> e.getBlockPos().equals(pos))) {
                                if (entity instanceof ForceDanceAccessor accessor) {
                                    accessor.identity$startForceDance();
                                }
                            }
                        }
                    });
                    ctx.get().setPacketHandled(true);
                }
        );

    }
}
