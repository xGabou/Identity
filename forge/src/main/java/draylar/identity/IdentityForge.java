package draylar.identity;

import draylar.identity.command.IdentityCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("identity")
public class IdentityForge {
    public IdentityForge() {
        Identity.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        IdentityCommand.register(event.getDispatcher());
    }
}
