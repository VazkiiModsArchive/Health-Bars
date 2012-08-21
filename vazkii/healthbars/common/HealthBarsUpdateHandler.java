package vazkii.healthbars.common;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Mod;
import vazkii.codebase.common.VazkiiUpdateHandler;
import vazkii.easyfps.client.EasyFPSReference;

public class HealthBarsUpdateHandler extends VazkiiUpdateHandler {

	public HealthBarsUpdateHandler(Mod m) {
		super(m);
	}

	@Override
	public String getModName() {
		return "Health Bars";
	}

	@Override
	public String getUMVersion() {
		return HealthBarsReference.VERSION;
	}

	@Override
	public String getUpdateURL() {
		return HealthBarsReference.UPDATE_URL;
	}
	
	@Override
	public String getChangelogURL() {
		return HealthBarsReference.CHANGELOG_URL;
	}
	
	@Override
	public ItemStack getIconStack(){
		return new ItemStack(Item.appleGold);
	}

}
