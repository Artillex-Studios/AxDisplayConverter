package com.artillexstudios.axdisplayconverter.packets.wrappers;

import com.artillexstudios.axapi.packet.ClientboundPacketTypes;
import com.artillexstudios.axapi.packet.FriendlyByteBuf;
import com.artillexstudios.axapi.packet.PacketEvent;
import com.artillexstudios.axapi.packet.PacketType;
import com.artillexstudios.axapi.packet.wrapper.PacketWrapper;

public class ClientboundRemoveEntitiesWrapper extends PacketWrapper {
    private int[] entityIds;

    public ClientboundRemoveEntitiesWrapper(int[] entityIds) {
        this.entityIds = entityIds;
    }

    public ClientboundRemoveEntitiesWrapper(PacketEvent event) {
        super(event);
    }

    @Override
    public void write(FriendlyByteBuf out) {
        out.writeVarIntArray(this.entityIds);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityIds = buf.readVarIntArray();
    }

    @Override
    public PacketType packetType() {
        return ClientboundPacketTypes.REMOVE_ENTITIES;
    }

    public int[] entityIds() {
        return entityIds;
    }

    public void entityIds(int[] entityIds) {
        this.entityIds = entityIds;
    }
}
