package com.artillexstudios.axdisplayconverter;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.nms.wrapper.ServerPlayerWrapper;
import com.artillexstudios.axapi.packet.PacketEvents;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axdisplayconverter.entityid.EntityManager;
import com.artillexstudios.axdisplayconverter.listeners.PlayerListeners;
import com.artillexstudios.axdisplayconverter.packets.PacketListeners;
import com.artillexstudios.axdisplayconverter.packets.wrappers.ClientboundRemoveEntitiesWrapper;
import com.artillexstudios.axdisplayconverter.records.TrackedPlayer;
import com.artillexstudios.axdisplayconverter.version.VersionUtils;

import java.util.List;

public final class AxDisplayConverter extends AxPlugin {
    private static AxDisplayConverter instance;

    public static AxDisplayConverter getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        EntityManager.init();
        VersionUtils.init();

        PacketListeners packetListeners = new PacketListeners();
        PacketEvents.INSTANCE.addListener(packetListeners);
        getServer().getPluginManager().registerEvents(packetListeners, this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
    }

    @Override
    public void disable() {
        for (TrackedPlayer trackedPlayer : PacketListeners.getTracked().values()) {
            ServerPlayerWrapper wrapper = ServerPlayerWrapper.wrap(trackedPlayer.player());
            for (List<Integer> value : trackedPlayer.replacements().values()) {
                ClientboundRemoveEntitiesWrapper removeEntitiesWrapper = new ClientboundRemoveEntitiesWrapper(value.stream().mapToInt(i -> i).toArray());
                wrapper.sendPacket(removeEntitiesWrapper);
            }
        }
    }

    @Override
    public void updateFlags() {
        FeatureFlags.ENABLE_PACKET_LISTENERS.set(true);
        FeatureFlags.COMPONENT_CACHE_SIZE.set(10_000);
    }
}
