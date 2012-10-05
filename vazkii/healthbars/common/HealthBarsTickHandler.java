package vazkii.healthbars.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.IntHashMap;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Potion;
import net.minecraft.src.WorldServer;
import vazkii.codebase.common.CommonUtils;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class HealthBarsTickHandler implements ITickHandler {

	int ticksElapsed = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		++ticksElapsed;
		if (type.equals(EnumSet.of(TickType.SERVER))) for (WorldServer world : CommonUtils.getServer().theWorldServer)
			sendPackets(world.getEntityTracker());
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "Health Bars";
	}

	public IntHashMap getTrackedEntitiesIDs(EntityTracker tracker) {
		return ReflectionHelper.<IntHashMap, EntityTracker> getPrivateValue(EntityTracker.class, tracker, 2);
	}

	public void sendPackets(EntityTracker tracker) {
		IntHashMap map = getTrackedEntitiesIDs(tracker);
		Iterator<Integer> it = ReflectionHelper.<Set, IntHashMap> getPrivateValue(IntHashMap.class, map, 5).iterator();
		++ticksElapsed;

		while (it.hasNext()) {
			EntityTrackerEntry entry = (EntityTrackerEntry) map.lookup(it.next());
			Entity entity = entry.myEntity;
			if (ticksElapsed % entry.updateFrequency == 0 && entity instanceof EntityLiving) tracker.sendPacketToAllPlayersTrackingEntity(entity, getHealthPacket((EntityLiving) entity));

		}
	}

	public Packet250CustomPayload getHealthPacket(EntityLiving entity) {
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(byteStream);

		try {
			data.writeInt(entity.entityId);
			data.writeInt(entity.getHealth());
			data.writeInt(entity.isPotionActive(Potion.poison) ? 4 : HealthBarsHooks.attackingEntities.contains(entity.entityId) ? 3 : 2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		packet.channel = "healthbars_Vz";
		packet.data = byteStream.toByteArray();
		packet.length = packet.data.length;

		return packet;
	}

}
