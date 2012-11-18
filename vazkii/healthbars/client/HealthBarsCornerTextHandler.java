package vazkii.healthbars.client;

import java.util.Arrays;
import java.util.List;

import vazkii.codebase.client.ClientUtils;
import vazkii.codebase.client.CornerTextEntry;
import vazkii.codebase.client.ICornerTextHandler;
import vazkii.codebase.common.CommonUtils;

import net.minecraft.src.Entity;

public class HealthBarsCornerTextHandler implements ICornerTextHandler {

	@Override
	public List<CornerTextEntry> updateCornerText(float partialTicks) {
		Entity entity = ClientUtils.getEntityLookedAt(partialTicks);
		if (!HealthBarsConfig.blacklistingMode || entity == null) return null;

		String name = CommonUtils.getEntityName(entity);

		return Arrays.asList(new CornerTextEntry[] { new CornerTextEntry(name + (HealthBarsConfig.isEntityBlacklisted(name) ? " (Blacklisted)" : " (Not Blacklisted)"), 0xFFFFFF) });
	}

}
