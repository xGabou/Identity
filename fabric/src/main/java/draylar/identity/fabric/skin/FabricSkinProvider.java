//package draylar.identity.fabric.skin;
//
//import com.mojang.authlib.GameProfile;
//import draylar.identity.skin.SkinLocation;
//import draylar.identity.skin.SkinProvider;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.network.AbstractClientPlayerEntity;
//import net.minecraft.client.texture.PlayerSkinProvider;
//import net.minecraft.util.Identifier;
//
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class FabricSkinProvider implements SkinProvider {
//    private final Map<UUID, Identifier> skinCache = new ConcurrentHashMap<>();
//
//    @Override
//    public void requestSkin(GameProfile profile) {
//        MinecraftClient.getInstance().getSkinProvider().loadSkin(profile, (type, identifier, model) -> {
//            if (type == PlayerSkinProvider.SkinType.SKIN) {
//                skinCache.put(profile.getId(), identifier);
//            }
//        }, true);
//    }
//
//    @Override
//    public SkinLocation getSkin(GameProfile profile) {
//        Identifier id = skinCache.getOrDefault(profile.getId(),
//                AbstractClientPlayerEntity.getSkinId(profile.getName()));
//        return new SkinLocation(id.getNamespace(), id.getPath());
//    }
//
//}
