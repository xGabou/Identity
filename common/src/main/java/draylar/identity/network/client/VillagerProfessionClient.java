package draylar.identity.network.client;

import dev.architectury.networking.NetworkManager;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.impl.Payload.OpenProfessionScreenPayload;
import draylar.identity.screen.VillagerProfessionScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class VillagerProfessionClient {

    @Environment(EnvType.CLIENT)
    public static void registerClientHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                OpenProfessionScreenPayload.ID,
                OpenProfessionScreenPayload.CODEC,
                (payload, context) -> {
                    Identifier professionId = payload.professionId();
                    net.minecraft.util.math.BlockPos pos = payload.pos();
                    Identifier worldId = payload.worldId();
                    String existingName = payload.existingName().orElse(null);
                    String existingProfession = payload.existingProfessionId().orElse(null);
                    ClientNetworking.runOrQueue(context, player ->
                            MinecraftClient.getInstance().setScreen(new VillagerProfessionScreen(professionId, pos, worldId, existingName, existingProfession))
                    );
                }
        );
    }
}

