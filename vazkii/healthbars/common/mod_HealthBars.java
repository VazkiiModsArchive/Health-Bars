package vazkii.healthbars.common;

import java.io.File;

import updatemanager.common.ModConverter;
import vazkii.codebase.client.CornerText;
import vazkii.codebase.common.CommonUtils;
import vazkii.codebase.common.EnumVazkiiMods;
import vazkii.codebase.common.IOUtils;
import vazkii.codebase.common.mod_Vazcore;
import vazkii.healthbars.client.HealthBarsConfig;
import vazkii.healthbars.client.HealthBarsCornerTextHandler;
import vazkii.healthbars.client.HealthBarsKeyHandler;

import net.minecraft.src.NBTTagCompound;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;

import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "healthbars_Vazkii", name = "Health Bars", version = "by Vazkii. Version [2.0.3] for 1.4.4/5")
@NetworkMod(channels = { "healthbars_Vz" }, packetHandler = HealthBarsPacketHandler.class)
public class mod_HealthBars {

	public static boolean barsEnabled = true;

	@Init
	public void onInit(FMLInitializationEvent event) {
		mod_Vazcore.loadedVzMods.add(EnumVazkiiMods.HEALTH_BARS.getAcronym());
		TickRegistry.registerTickHandler(new HealthBarsTickHandler(), Side.SERVER);
		MinecraftForge.EVENT_BUS.register(new HealthBarsHooks());
		new HealthBarsUpdateHandler(ModConverter.getMod(getClass()));

		if (!CommonUtils.getSide().isClient()) return;

		File configFile = IOUtils.getConfigFile(EnumVazkiiMods.HEALTH_BARS);
		File cacheFile = IOUtils.getCacheFile(EnumVazkiiMods.HEALTH_BARS);

		NBTTagCompound comp = IOUtils.getTagCompoundInFile(cacheFile);
		barsEnabled = comp.hasKey("barsEnabled") ? comp.getBoolean("barsEnabled") : true;

		new HealthBarsConfig(configFile);
		KeyBindingRegistry.registerKeyBinding(new HealthBarsKeyHandler());
		CornerText.registerCornerTextHandler(new HealthBarsCornerTextHandler());
	}

}
