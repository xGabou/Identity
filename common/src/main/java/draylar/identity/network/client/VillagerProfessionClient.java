package draylar.identity.network.client;

import dev.architectury.networking.NetworkManager;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import draylar.identity.screen.VillagerProfessionScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class VillagerProfessionClient {

    @Environment(EnvType.CLIENT)
    public static void registerClientHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.OPEN_PROFESSION_SCREEN, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
            Identifier worldId = buf.readIdentifier();
            String existingName = null;
            String existingProfession = null;
            if (buf.readBoolean()) {
                existingName = buf.readString();
                String prof = buf.readString();
                existingProfession = prof.isEmpty() ? null : prof;
            }
            String finalExistingName = existingName;
            String finalExistingProfession = existingProfession;
            ClientNetworking.runOrQueue(context, player -> MinecraftClient.getInstance().setScreen(new VillagerProfessionScreen(professionId, pos, worldId, finalExistingName, finalExistingProfession)));
        });
    }
}

