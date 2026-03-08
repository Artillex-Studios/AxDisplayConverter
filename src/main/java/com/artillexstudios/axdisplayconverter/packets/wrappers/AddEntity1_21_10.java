package com.artillexstudios.axdisplayconverter.packets.wrappers;

import com.artillexstudios.axapi.packet.ClientboundPacketTypes;
import com.artillexstudios.axapi.packet.FriendlyByteBuf;
import com.artillexstudios.axapi.packet.PacketEvent;
import com.artillexstudios.axapi.packet.PacketType;
import com.artillexstudios.axapi.packet.wrapper.PacketWrapper;
import com.artillexstudios.axapi.utils.Vector3d;

import java.util.UUID;

public final class AddEntity1_21_10 extends PacketWrapper {
    private int entityId;
    private UUID uuid;
    private int entityType;
    private double x;
    private double y;
    private double z;
    private Vector3d movement;
    private byte pitch;
    private byte yaw;
    private byte headYaw;
    private int data;

    public AddEntity1_21_10(PacketEvent event) {
        super(event);
    }

    public AddEntity1_21_10(int entityId, UUID uuid, int entityType, double x, double y, double z, byte pitch, byte yaw, byte headYaw, int data, short xo, short yo, short zo) {
        this(entityId, uuid, entityType, x, y, z, new Vector3d((double)xo, (double)yo, (double)zo), pitch, yaw, headYaw, data);
    }

    public AddEntity1_21_10(int entityId, UUID uuid, int entityType, double x, double y, double z, Vector3d movement, byte pitch, byte yaw, byte headYaw, int data) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.headYaw = headYaw;
        this.data = data;
        this.movement = movement;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setEntityId(UUID uuid) {
        this.uuid = uuid;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getz() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public byte getHeadYaw() {
        return this.headYaw;
    }

    public void setHeadYaw(byte headYaw) {
        this.headYaw = headYaw;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setMovement(Vector3d movement) {
        this.movement = movement;
    }

    public Vector3d getMovement() {
        return this.movement;
    }

    public int getEntityType() {
        return this.entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public void write(FriendlyByteBuf out) {
        out.writeVarInt(this.entityId);
        out.writeUUID(this.uuid);
        out.writeVarInt(this.entityType);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.versionedWriteLpVec3(this.movement);
        out.writeByte(this.pitch);
        out.writeByte(this.yaw);
        out.writeByte(this.headYaw);
        out.writeVarInt(this.data);
    }

    public void read(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.uuid = buf.readUUID();
        this.entityType = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.movement = buf.versionedReadLpVec3();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.headYaw = buf.readByte();
        this.data = buf.readVarInt();
    }

    public PacketType packetType() {
        return ClientboundPacketTypes.ADD_ENTITY;
    }
}