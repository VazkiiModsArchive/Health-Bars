package vazkii.healthbars.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;
import vazkii.codebase.client.ClientUtils;
import vazkii.codebase.common.CommonUtils;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HealthBarsPacketHandler implements IPacketHandler {

	public static HashMap<Integer, Integer> entityStatus = new HashMap();

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (CommonUtils.getSide().isClient()) {
			DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			try {
				int id = dataStream.readInt();
				int hp = dataStream.readInt();
				int status = dataStream.readInt();
				Entity entity = ((WorldClient) ClientUtils.getClientPlayer().worldObj).getEntityByID(id);
				if (entity instanceof EntityLiving) {
					((EntityLiving) entity).setEntityHealth(hp);
					entityStatus.put(id, status);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
