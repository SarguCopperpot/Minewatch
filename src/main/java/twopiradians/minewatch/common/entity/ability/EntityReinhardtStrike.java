package twopiradians.minewatch.common.entity.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.common.CommonProxy.EnumParticle;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.entity.EntityMW;
import twopiradians.minewatch.common.sound.ModSoundEvents;
import twopiradians.minewatch.common.util.EntityHelper;

public class EntityReinhardtStrike extends EntityMW {

	public EntityReinhardtStrike(World worldIn) {
		this(worldIn, null);
		ModSoundEvents.REINHARDT_STRIKE_DURING.playFollowingSound(this, 3, 1, true);
	}

	public EntityReinhardtStrike(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn, -1);
		this.setSize(1.5f, 2f);
		this.lifetime = 6000;
		this.setNoGravity(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance){
		return distance < 5000;
	}

	@Override
	public void onUpdate() {		
		if (world.isRemote) {
			for (int i=0; i<3; ++i) {
				world.spawnParticle(EnumParticleTypes.FLAME, 
						posX+(world.rand.nextDouble()-0.5f)*1.5d, 
						posY+height/2d+(world.rand.nextDouble()-0.5f)*1.5d, 
						posZ+(world.rand.nextDouble()-0.5f)*1.5d, 
						0, 0, 0, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
						posX+(world.rand.nextDouble()-0.5f)*1.5d, 
						posY+height/2d+(world.rand.nextDouble()-0.5f)*1.5d, 
						posZ+(world.rand.nextDouble()-0.5f)*1.5d, 
						0, 0, 0, new int[0]);
			}
			boolean enemy = EntityHelper.shouldHit(this, Minewatch.proxy.getClientPlayer(), false);
			Minewatch.proxy.spawnParticlesCustom(EnumParticle.CIRCLE, world, prevPosX, prevPosY+height/2d, prevPosZ, 0, 0, 0,
					enemy ? 0xFF6666 : 0xFFFFF5, enemy ? 0xFF6666 : 0xF2DEA2, 0.2f, 10, 14, 12, 0, 0);
		}
		
		super.onUpdate(); 
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
		return 15728880;
    }
	
	@Override
	protected void onImpactMoveToHitPosition(RayTraceResult result) {
		if (result.typeOfHit == RayTraceResult.Type.BLOCK)
			super.onImpactMoveToHitPosition(result);
	}

	@Override
	public void onImpact(RayTraceResult result) {
		super.onImpact(result);
		

		if (result.typeOfHit == RayTraceResult.Type.BLOCK && world.isRemote) {
			ModSoundEvents.REINHARDT_STRIKE_COLLIDE.playSound(this, 4, 1);
			double x = posX; 
			double y = posY;
			double z = posZ;
			if (result.sideHit == EnumFacing.SOUTH)
				z = Math.ceil(z);
			else if (result.sideHit == EnumFacing.EAST)
				x = Math.ceil(x);
			else if (result.sideHit == EnumFacing.UP)
				y = Math.ceil(y);
			Minewatch.proxy.spawnParticlesCustom(EnumParticle.REINHARDT_STRIKE, world, x, y, z, 0, 0, 0, 0xFFFFFF, 0xFFFFFF, 1.0f, 100, 20, 20, world.rand.nextFloat(), 0, result.sideHit, true);
		}

		EntityHelper.attemptDamage(this, result.entityHit, 100, false, false);
	}
	
	@Override
	public void applyEntityCollision(Entity entityIn) {}
	
	@Override
	protected boolean isValidImpact(RayTraceResult result, boolean nearest) {
		return super.isValidImpact(result, nearest) && 
				!(result.entityHit != null && result.entityHit.hurtResistantTime > 0);
	}

}