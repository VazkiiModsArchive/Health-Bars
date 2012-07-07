package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import vazkii.um.UpdateManagerMod;

public class mod_HealthBars extends BaseMod {
	
	public static KeyBinding key = new KeyBinding("Health Bars",Keyboard.KEY_B);
	
	public String getVersion() {
		return "by Vazkii. Version [1.1.2] for 1.2.5";
	}
	
	public void load() {
		blacklistedEntities = readBlacklist(blackList);
		barsEnabled = barsEnbaledOnStartup;
		ModLoader.registerKey(this, key, true);
		ModLoader.addName(healthDebuggerTool, "Vazkii co. Health Bars Blacklist Remote");
		
		new UpdateHandler(this);
	}
	
	public void keyboardEvent(KeyBinding event) {
		if (event.keyCode == key.keyCode && event.isPressed()){
			barsEnabled = !barsEnabled;
		}	
	}
	
	private String[] readBlacklist(String string){
		return string.split(";");
	}
	
    public static boolean isEntityBlackListed(Entity entity){
    	for(int k=0; k<mod_HealthBars.blacklistedEntities.length;k++){
    		String s = entity.getEntityString();
    		if (s.matches(mod_HealthBars.blacklistedEntities[k]))
    		return true;
    	}return false;
    }
	
	public static boolean barsEnabled = true;
	
	@MLProp public static int healthDebuggerToolID = 18819;
	@MLProp public static int maxHealthToRender = 150;
	@MLProp public static int minHealthToRender = 2;
	@MLProp public static boolean healthNumbers = true;
	@MLProp public static boolean barsEnbaledOnStartup = true;
	@MLProp public static int renderDistance = 12;
	@MLProp public static double barHeight = -1.6;
	@MLProp public static String blackList = "SnowMan;MushroomCow;Giant;Ghast";
	@MLProp public static boolean alignLeft = true;
	
	public static Item healthDebuggerTool = new ItemHealthDebuggerTool(healthDebuggerToolID).setIconCoord(11, 10).setItemName("healthDebuggerTool");
	
	//This is here for the array values to not be hardcoded, I'll get a way to customize them... eventually...
	public static float[]  barBackgroundRGBA = {
		1.0F, 1.0F, 1.0F, 1.0F
	};
	public static float[]  barOutlineRGBA = {
		0.0F, 0.0F, 0.0F, 1.0F
	};
	public static float[]  barNonAggroRGBA = {
		0.0F, 0.0F, 1.0F, 0.9F
	};
	public static float[]  barAggroRGBA = {
		1.0F, 0.0F, 0.0F, 0.9F
	};
	public static float[]  barPoisonedRGBA = {
		0.3F, 1.0F, 0.0F, 0.9F
	};
	public static String[] blacklistedEntities = {
	};

	public class UpdateHandler extends UpdateManagerMod {
		
		public UpdateHandler(cpw.mods.fml.common.modloader.BaseMod m) {
			super(m);
		}

		public String getModName() {
			return "Health Bars";
		}

		public String getChangelogURL() {
			return "https://dl.dropbox.com/u/34938401/Mods/On%20Topic/Mods/Health%20Bars/Changelog.txt";
		}

		public String getUpdateURL() {
			return "https://dl.dropbox.com/u/34938401/Mods/On%20Topic/Mods/Health%20Bars/Version.txt";
		}

		public String getModURL() {
			return "http://www.minecraftforum.net/topic/528166-123-mlforge-vazkiis-mods-ebonapi-last-updated-12512/";
		}
		
		public String getUMVersion() {
			return "1.1.2";
		}
	}
}
