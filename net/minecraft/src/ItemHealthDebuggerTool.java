package net.minecraft.src;

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

}
