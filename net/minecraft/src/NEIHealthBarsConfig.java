package net.minecraft.src;

import codechicken.nei.API;
import codechicken.nei.IConfigureNEI;
import codechicken.nei.MultiItemRange;

public class NEIHealthBarsConfig implements IConfigureNEI{

	public void loadConfig() {
		MultiItemRange r = new MultiItemRange();
		r.add(mod_HealthBars.healthDebuggerTool);
		API.addSetRange("Vazkii Mods.Health Bars", r);
	}

}
