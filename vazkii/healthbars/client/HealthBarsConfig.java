package vazkii.healthbars.client;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import vazkii.healthbars.common.HealthBarsReference;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class HealthBarsConfig extends Configuration {

	public final String CATEGORY_COLORS = "colors";

	public TreeMap<String, Property> colorProps = new TreeMap();

	private static String[] RGB_TO_GET = new String[] { "barOutline", "barBackground", "barNeutral", "barAggro", "barPoisoned" };

	private static String[] RGB_NAMES = new String[] { "Bar Outline", "Bar Background", "Bar Neutral Status", "Bar Aggro Status", "Bar Poisoned Status" };

	public static int[][] RGBs = new int[RGB_TO_GET.length][3];

	public static List<String> blacklistedEntities = new LinkedList();

	// Props Start
	// ============================================================================
	public static int maxRenderDistance = 20;
	public static int maxHealthToRender = 150;
	public static int minHealthToRender = 2;

	public static boolean healthNumbers = true;
	public static boolean alignLeft = true;
	public static boolean renderOnLookOnly = false;
	public static boolean blacklistingMode = false;

	public static String blacklist = "SnowMan;MushroomCow;Giant;Ghast";

	public static String barOutlineRGB = "0,0,0";
	public static String barBackgroundRGB = "255,255,255";
	public static String barNeutralRGB = "0,0,255";
	public static String barAggroRGB = "255,0,0";
	public static String barPoisonedRGB = "76,255,0";

	// Props End
	// ==============================================================================

	public HealthBarsConfig(File file) {
		super(file);
		categories.put(CATEGORY_COLORS, colorProps);
		categories.remove(CATEGORY_BLOCK);
		categories.remove(CATEGORY_ITEM);

		load();

		Property propMaxRenderDistance = getOrCreateIntProperty("maxRenderDistance", CATEGORY_GENERAL, 20);
		propMaxRenderDistance.comment = "The max distance to render health bars.";
		maxRenderDistance = propMaxRenderDistance.getInt(20);

		Property propMaxHealthToRender = getOrCreateIntProperty("maxHealthToRender", CATEGORY_GENERAL, 150);
		propMaxHealthToRender.comment = "The maximum health that will have a health bar rendered.";
		maxHealthToRender = propMaxHealthToRender.getInt(150);

		Property propMinHealthToRender = getOrCreateIntProperty("minHealthToRender", CATEGORY_GENERAL, 2);
		propMinHealthToRender.comment = "The minimum health that will have a health bar rendered.";
		minHealthToRender = propMinHealthToRender.getInt(150);

		Property propHealthNumbers = getOrCreateBooleanProperty("healthNumbers", CATEGORY_GENERAL, true);
		propHealthNumbers.comment = "Set to true to enable numerical health over the visual health bars.";
		healthNumbers = propHealthNumbers.getBoolean(true);

		Property propAlignLeft = getOrCreateBooleanProperty("alignLeft", CATEGORY_GENERAL, true);
		propAlignLeft.comment = "Set to true to have the bar align to the left. Set to false to have it align to the center.";
		alignLeft = propAlignLeft.getBoolean(true);

		Property propRenderOnLookOnly = getOrCreateBooleanProperty("renderOnLookOnly", CATEGORY_GENERAL, false);
		propRenderOnLookOnly.comment = "Set to true to have health bars render only if the entity is over the crosshair.";
		renderOnLookOnly = propRenderOnLookOnly.getBoolean(false);

		Property propBlacklistingMode = getOrCreateBooleanProperty("blacklistingMode", CATEGORY_GENERAL, false);
		propBlacklistingMode.comment = "Set to true to enable blacklisting mode (in the corner of the screen, the mob's name you're looking at appears, so you can add it into the blacklist).";
		blacklistingMode = propBlacklistingMode.getBoolean(false);

		Property propBlacklist = getOrCreateProperty("blacklist", CATEGORY_GENERAL, HealthBarsReference.DEFAULT_BLACKLIST);
		propBlacklist.comment = "Mobs with their names in the blacklist won't have health bars, separate mob names with ';'.";
		blacklist = propBlacklist.value;
		blacklistedEntities.addAll(Arrays.asList(blacklist.split(";")));

		for (int i = 0; i < RGB_TO_GET.length; i++) {
			Property propRgb = getOrCreateProperty(RGB_TO_GET[i], CATEGORY_COLORS, getField(RGB_TO_GET[i]));
			propRgb.comment = String.format("The RGB color of the %s.", RGB_NAMES[i]);
			String[] tokens = propRgb.value.split(",");
			for (int j = 0; j < 3; j++)
				RGBs[i][j] = Integer.parseInt(tokens[j]);
		}

		save();
	}

	public static String getField(String field) {
		return (String) ReflectionHelper.getPrivateValue(HealthBarsConfig.class, null, field + "RGB");
	}

	public static boolean isEntityBlacklisted(String name) {
		return blacklistedEntities.contains(name);
	}
}
