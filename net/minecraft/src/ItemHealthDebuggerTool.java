package net.minecraft.src;

import java.util.List;

public class ItemHealthDebuggerTool extends Item {

	public ItemHealthDebuggerTool(int par1) {
		super(par1);
	}
	
    public void useItemOnEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving)
    {
    	String entityName = par2EntityLiving.getEntityString();
    	boolean isBlacklisted = mod_HealthBars.isEntityBlackListed(par2EntityLiving);
    	
    	ModLoader.getMinecraftInstance().thePlayer.addChatMessage(new StringBuilder().append("Entity Name: '").append(entityName).append("' | ").append("Is Blacklisted?: ").append(isBlacklisted).append(".").toString());
    }
    
    public void addInformation(ItemStack stack, List list){
    	list.add("§7Use on an entity to get their name");
    	list.add("§7then take that name to the blacklist");
    	list.add("§7on the Health Bars config to disable");
    	list.add("§7Health Bars on that entity.");
    }

}
