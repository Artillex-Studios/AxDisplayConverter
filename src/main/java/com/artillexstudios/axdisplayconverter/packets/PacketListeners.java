package com.artillexstudios.axdisplayconverter.packets;

import com.artillexstudios.axapi.nms.wrapper.ServerPlayerWrapper;
import com.artillexstudios.axapi.nms.wrapper.ServerWrapper;
import com.artillexstudios.axapi.packet.ClientboundPacketTypes;
import com.artillexstudios.axapi.packet.PacketEvent;
import com.artillexstudios.axapi.packet.PacketListener;
import com.artillexstudios.axapi.packet.wrapper.clientbound.ClientboundAddEntityWrapper;
import com.artillexstudios.axapi.packet.wrapper.clientbound.ClientboundEntityMetadataWrapper;
import com.artillexstudios.axapi.packetentity.meta.Metadata;
import com.artillexstudios.axapi.packetentity.meta.serializer.EntityDataSerializers;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.Vector3d;
import com.artillexstudios.axdisplayconverter.entityid.EntityManager;
import com.artillexstudios.axdisplayconverter.packets.wrappers.ClientboundRemoveEntitiesWrapper;
import com.artillexstudios.axdisplayconverter.records.SpawningDisplay;
import com.artillexstudios.axdisplayconverter.records.TrackedPlayer;
import com.artillexstudios.axdisplayconverter.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketListeners extends PacketListener implements Listener {
    private final Map<Player, TrackedPlayer> tracked = new ConcurrentHashMap<>();
    // temporary map to store locations before the metadata packet comes in
    private final Map<Integer, SpawningDisplay> spawningLocation = new ConcurrentHashMap<>();

    public PacketListeners() {
        // if no metadata was sent within a second, remove
        Scheduler.get().runTimer(() -> {
            spawningLocation.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().date() > 1000);
        }, 20, 20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        ServerPlayerWrapper wrapper = ServerPlayerWrapper.wrap(event.getPlayer());
        wrapper.inject();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tracked.remove(event.getPlayer());
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.type() == ClientboundPacketTypes.ADD_ENTITY) {
            if (!VersionUtils.isOutdated(event.player())) return;
            ClientboundAddEntityWrapper wrapper = new ClientboundAddEntityWrapper(event);
            addEntity(event, wrapper);
        } else if (event.type() == ClientboundPacketTypes.SET_ENTITY_DATA) {
            if (!VersionUtils.isOutdated(event.player())) return;
            ClientboundEntityMetadataWrapper wrapper = new ClientboundEntityMetadataWrapper(event);
            setMetaData(event, wrapper);
        } else if (event.type() == ClientboundPacketTypes.REMOVE_ENTITIES) {
            if (!VersionUtils.isOutdated(event.player())) return;
            ClientboundRemoveEntitiesWrapper wrapper = new ClientboundRemoveEntitiesWrapper(event);
            removeEntities(event, wrapper);
        }
    }

    private void addEntity(PacketEvent event, ClientboundAddEntityWrapper wrapper) {
        if (wrapper.getEntityType() != EntityManager.getEntityIDs().get(EntityType.TEXT_DISPLAY)) return;

        int id = wrapper.getEntityId();
        spawningLocation.put(id, new SpawningDisplay(new Location(null, wrapper.getX(), wrapper.getY(), wrapper.getz(), wrapper.getYaw(), wrapper.getPitch()), System.currentTimeMillis()));
        TrackedPlayer trackedPlayer = tracked.computeIfAbsent(event.player(), k -> new TrackedPlayer(event.player(), new ConcurrentHashMap<>()));
        trackedPlayer.replacements().put(id, new ArrayList<>());
        event.cancelled(true);
    }

    private void setMetaData(PacketEvent event, ClientboundEntityMetadataWrapper wrapper) {
        int id = wrapper.entityId();

        for (Metadata.DataItem<?> item : wrapper.items()) {
            if (item.id() != 23) continue;
            if (!(item.getValue() instanceof Component component)) continue;
            event.cancelled(true);

            ServerPlayerWrapper playerWrapper = ServerPlayerWrapper.wrap(event.player());
            if (playerWrapper == null) return;

            TrackedPlayer trackedPlayer = tracked.getOrDefault(event.player(), new TrackedPlayer(event.player(), new ConcurrentHashMap<>()));

            String raw = StringUtils.LEGACY_COMPONENT_SERIALIZER.serialize(component);
            String[] lines = raw.split("\n");

            List<Integer> repIds = trackedPlayer.replacements().getOrDefault(id, new ArrayList<>());
            if (lines.length != repIds.size()) {
                // respawn if the line count has changed
                sendRemoveEntities(playerWrapper, repIds);
                repIds.clear();
            }

            if (repIds.isEmpty()) {
                // spawn
                SpawningDisplay display = spawningLocation.remove(id);
                if (display == null) return;
                for (int i = 0; i < lines.length; i++) {
                    int addId = ServerWrapper.INSTANCE.nextEntityId();
                    repIds.add(addId);

                    Location copy = display.location().clone();
                    copy.setY(getNewHeight(copy.getY(), lines.length, i));

                    ClientboundAddEntityWrapper addEntityWrapper = new ClientboundAddEntityWrapper(
                            addId,
                            UUID.randomUUID(),
                            EntityManager.getEntityIDs().get(EntityType.ARMOR_STAND),
                            copy.getX(),
                            copy.getY(),
                            copy.getZ(),
                            (byte) 0,
                            (byte) 0,
                            (byte) 0,
                            0,
                            new Vector3d()
                    );
                    playerWrapper.sendPacket(addEntityWrapper);
                }

                trackedPlayer.replacements().put(id, repIds);
            }

            // update
            for (int i = 0; i < lines.length; i++) {
                int repId = repIds.get(i);
                String line = lines[i];

                List<Metadata.DataItem<?>> items = new ArrayList<>();
                items.add(new Metadata.DataItem<>(15, EntityDataSerializers.BYTE, (byte) 0x10)); // marker
                items.add(new Metadata.DataItem<>(0, EntityDataSerializers.BYTE, (byte) 0x20)); // invisible
                items.add(new Metadata.DataItem<>(3, EntityDataSerializers.BOOLEAN, true)); // custom name visible
                items.add(new Metadata.DataItem<>(2, EntityDataSerializers.OPTIONAL_COMPONENT, Optional.of(StringUtils.format(line)))); // custom name
                ClientboundEntityMetadataWrapper metadataWrapper = new ClientboundEntityMetadataWrapper(repId, items);
                playerWrapper.sendPacket(metadataWrapper);
            }

            break;
        }
    }

    private void removeEntities(PacketEvent event, ClientboundRemoveEntitiesWrapper wrapper) {
        TrackedPlayer trackedPlayer = tracked.get(event.player());
        if (trackedPlayer == null) return;
        for (int id : wrapper.entityIds()) {
            List<Integer> ids = trackedPlayer.replacements().remove(id);
            if (ids != null && !ids.isEmpty()) {
                // send destroy packet to player
                ServerPlayerWrapper playerWrapper = ServerPlayerWrapper.wrap(event.player());
                sendRemoveEntities(playerWrapper, ids);
            }
        }
    }

    private void sendRemoveEntities(ServerPlayerWrapper playerWrapper, List<Integer> ids) {
        ClientboundRemoveEntitiesWrapper removeEntitiesWrapper = new ClientboundRemoveEntitiesWrapper(ids.stream().mapToInt(i -> i).toArray());
        playerWrapper.sendPacket(removeEntitiesWrapper);
    }

    // text display -> armor stand
    private static double getNewHeight(double y, int lines, int line) {
        return y + (-0.3 * line) + ((lines - 2) * 0.3);
    }
}
