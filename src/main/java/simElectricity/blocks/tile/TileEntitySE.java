package simElectricity.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntitySE extends TileEntity{
	/**
	 * As mentioned in documentations, developers spotted that Vanilla MineCraft tends to recreate the tileEntity when the blockState changes
	 * Being a modder, most of us don't want this. The following method tweaks the vanilla behavior and gives you the original behavior.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());	//Was "return !isVanilla || (oldBlock != newBlock);" in 1.7.10
	}
	
	protected void markTileEntityForS2CSync(){
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 2);
	}
	
	@SideOnly(value = Side.CLIENT)
	protected void markForRenderUpdate(){
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 1);
	}
	
	//Sync
	public void prepareS2CPacketData(NBTTagCompound nbt) {}
	
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {}
	
	@Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
		System.out.println("[DEBUG]:Server sent tile sync packet");
		NBTTagCompound tagCompound = new NBTTagCompound();
		prepareS2CPacketData(tagCompound);
		return new SPacketUpdateTileEntity(pos, 1, tagCompound);

    }

	@Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt)
    {	
		
		if (world.isRemote){
			System.out.println("[DEBUG]:Client recived tile sync packet");	//Debug
			
			//This is supposed to be Client ONLY!
			//SPacketUpdateTileEntity starts with S, means that this packet is sent from server to client
			onSyncDataFromServerArrived(pkt.getNbtCompound());		
		}
    }
}
