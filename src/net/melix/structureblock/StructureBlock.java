package net.melix.structureblock;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.plugin.PluginBase;

import java.nio.ByteOrder;

public class StructureBlock extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public static void send(Player player, Vector3 center, Vector3 pos1, Vector3 pos2) {

        Vector3 offset = calculateOffset(center, pos1, pos2);
        Vector3 size = calculateSize(pos1, pos2);

        CompoundTag nbt = new CompoundTag();
        nbt.putString("CustomName", "Custom Name");
        nbt.putString("id", "StructureBlock");
        nbt.putInt("x", center.getFloorX());
        nbt.putInt("y", 0);
        nbt.putInt("z", center.getFloorZ());
        nbt.putByte("isMovable", 1);

        nbt.putByte("isPowered", 0);
        nbt.putInt("data", 1);

        int centerY = Math.max(pos1.getFloorY(), pos2.getFloorY()) - ((Math.max(pos1.getFloorY(), pos2.getFloorY()) - Math.min(pos1.getFloorY(), pos2.getFloorY())));

        nbt.putInt("xStructureOffset", offset.getFloorX());
        nbt.putInt("yStructureOffset", centerY);
        nbt.putInt("zStructureOffset", offset.getFloorZ());

        nbt.putInt("xStructureSize", size.getFloorX());
        nbt.putInt("yStructureSize", size.getFloorY());
        nbt.putInt("zStructureSize", size.getFloorZ());

        nbt.putString("structureName", "Border");
        nbt.putString("dataField", "");

        nbt.putByte("ignoreEntities", 0);
        nbt.putByte("includePlayers", 0);
        nbt.putByte("removeBlocks", 0);
        nbt.putByte("showBoundingBox", 1);
        nbt.putByte("rotation", 0);
        nbt.putByte("mirror", 0);

        nbt.putFloat("integrity", 100.0F);
        nbt.putLong("seed", 0);

        try {
            Block block = Block.get(Block.STRUCTURE_BLOCK);
            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.x = center.getFloorX();
            packet.y = 0;
            packet.z = center.getFloorZ();
            packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, block.getId(), block.getFullId() & 0xf);
            packet.flags = UpdateBlockPacket.FLAG_NETWORK;
            player.getNetworkSession().sendPacket(packet);

            BlockEntityDataPacket pk = new BlockEntityDataPacket();
            pk.x = center.getFloorX();
            pk.y = 0;
            pk.z = center.getFloorZ();
            pk.namedTag = NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN, true);
            player.getNetworkSession().sendPacket(pk);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Vector3 getMin(Vector3 pos1, Vector3 pos2){
        return new Vector3(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z)).floor();
    }

    public static Vector3 calculateSize(Vector3 pos1, Vector3 pos2){
        return pos1.subtract(pos2).abs().add(1, 1, 1);
    }

    public static Vector3 calculateOffset(Vector3 center, Vector3 pos1, Vector3 pos2){
        return center.subtract(getMin(pos1, pos2)).multiply(-1).floor();
    }

}
