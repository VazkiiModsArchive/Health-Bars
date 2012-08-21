package vazkii.healthbars.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.ReflectionHelper;

import vazkii.codebase.client.ClientUtils;
import vazkii.codebase.common.CommonUtils;
import vazkii.healthbars.client.HealthBarsConfig;

import net.minecraft.src.WorldServer;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Frustrum;
import net.minecraft.src.IntHashMap;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Potion;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class HealthBarsHooks{

	public static Set<Integer> attackingEntities = new LinkedHashSet();
	
	@ForgeSubscribe
	public void onEntityGetAttackTarget(LivingSetAttackTargetEvent event){
		if(event.target == null)
			attackingEntities.remove(event.entity.entityId);
		else attackingEntities.add(event.entity.entityId);
	}
	
	@ForgeSubscribe
	public void onRenderWorldLast(RenderWorldLastEvent event){
		Minecraft mc = CommonUtils.getMc();
		
		if(!mc.isGuiEnabled() || !mod_HealthBars.barsEnabled)
			return;
		
		EntityLiving cameraEntity = mc.renderViewEntity;
		Vec3 renderingVector = cameraEntity.getPosition(event.partialTicks);
		Frustrum frustrum = new Frustrum();
		
        double viewX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * (double)event.partialTicks;
        double viewY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * (double)event.partialTicks;
        double viewZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * (double)event.partialTicks;
        frustrum.setPosition(viewX, viewY, viewZ);
        
        for(Entity entity : ClientUtils.getWorldEntityList(mc.theWorld)){
            if (entity != null && entity instanceof EntityLiving && entity.isInRangeToRenderVec3D(renderingVector) && (entity.ignoreFrustumCheck || frustrum.isBoundingBoxInFrustum(entity.boundingBox)) && entity.isEntityAlive())
            	renderHealthBar((EntityLiving)entity, event.partialTicks, cameraEntity);
        }
	}	

	public void renderHealthBar(EntityLiving entity, float partialTicks, Entity viewPoint){	
		float distance = entity.getDistanceToEntity(viewPoint);
        if(distance > (float)HealthBarsConfig.maxRenderDistance || !entity.canEntityBeSeen(viewPoint) || entity.getHealth() < HealthBarsConfig.minHealthToRender || entity.getHealth() > HealthBarsConfig.maxHealthToRender || entity instanceof EntityPlayer || HealthBarsConfig.isEntityBlacklisted(CommonUtils.getEntityName(entity)))
        	return;
        
        if(HealthBarsConfig.renderOnLookOnly && ClientUtils.getEntityLookedAt(partialTicks) != entity)
        	return;
        
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        
        float scale = 0.026666672F;
        int maxHealth = entity.getMaxHealth();
        int health = entity.getHealth();
        int barColorIndex = HealthBarsPacketHandler.entityStatus.containsKey(entity.entityId) ? HealthBarsPacketHandler.entityStatus.get(entity.entityId) : 2;
        
        if(HealthBarsConfig.healthNumbers)
        	renderLabel(entity, String.format("%s/%s (", (double)health/2, (double)maxHealth/2) + (health * 100 / maxHealth) + "%)", (float)(x - RenderManager.renderPosX), (float)(y - RenderManager.renderPosY + entity.height + 0.6), (float)(z - RenderManager.renderPosZ), HealthBarsConfig.maxRenderDistance);        	
        
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(x - RenderManager.renderPosX), (float)(y - RenderManager.renderPosY + entity.height + 0.3), (float)(z - RenderManager.renderPosZ));
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false); 
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(HealthBarsConfig.RGBs[0][0], HealthBarsConfig.RGBs[0][1], HealthBarsConfig.RGBs[0][2], 255);
        tessellator.addVertex(-maxHealth - 3 , -1, 0.0D);
        tessellator.addVertex(-maxHealth - 1, 3, 0.0D);
        tessellator.addVertex(maxHealth + 3, 3, 0.0D);
        tessellator.addVertex(maxHealth + 1, -1, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(HealthBarsConfig.RGBs[1][0], HealthBarsConfig.RGBs[1][1], HealthBarsConfig.RGBs[1][2], 255);
        tessellator.addVertex(-maxHealth - 1, 0, 0.0D);
        tessellator.addVertex(-maxHealth, 2, 0.0D);
        tessellator.addVertex(maxHealth + 1, 2, 0.0D);
        tessellator.addVertex(maxHealth, 0, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(HealthBarsConfig.RGBs[barColorIndex][0], HealthBarsConfig.RGBs[barColorIndex][1], HealthBarsConfig.RGBs[barColorIndex][2], 255);
        if(HealthBarsConfig.alignLeft){
            tessellator.addVertex(-maxHealth - 1, 0, 0.0D);
            tessellator.addVertex(-maxHealth , 2, 0.0D);
            tessellator.addVertex(health*2-maxHealth + 1, 2, 0.0D);
            tessellator.addVertex(health*2-maxHealth, 0, 0.0D);
        }else{
            tessellator.addVertex(-health - 1, 0, 0.0D);
            tessellator.addVertex(-health, 2, 0.0D);
            tessellator.addVertex(health + 1, 2, 0.0D);
            tessellator.addVertex(health, 0, 0.0D);
        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
	}
	
	public static void renderLabel(EntityLiving par1EntityLiving, String par2Str, double par3, double par5, double par7, int par9)
    {
		Minecraft mc = CommonUtils.getMc();
		RenderManager renderManager = RenderManager.instance;
		
		if(renderManager.livingPlayer == null || par1EntityLiving == null)
			return;
		
        double var10 = par1EntityLiving.getDistanceSqToEntity(renderManager.livingPlayer);
        
        if (var10 <= (double)(par9 * par9))
        {
            FontRenderer var12 = mc.fontRenderer;
            float var13 = 1.6F;
            float var14 = 0.016666668F * var13;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par3, (float)par5, (float)par7);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-var14, -var14, var14);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Tessellator var15 = Tessellator.instance;
            byte var16 = 0;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            var15.startDrawingQuads();
            int var17 = var12.getStringWidth(par2Str) / 2;
            var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            var15.addVertex((double)(-var17 - 1), (double)(-1 + var16), 0.0D);
            var15.addVertex((double)(-var17 - 1), (double)(8 + var16), 0.0D);
            var15.addVertex((double)(var17 + 1), (double)(8 + var16), 0.0D);
            var15.addVertex((double)(var17 + 1), (double)(-1 + var16), 0.0D);
            var15.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

}
