package simElectricity.blocks.tile;

import simElectricity.Network.ITileRenderingInfoSyncHandler;
import simElectricity.blocks.MultiBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * MC>1.8 tileEntity sync
 * http://jabelarminecraft.blogspot.com.au/p/minecraft-forge-1721710.html
 */

/**
 * In MC >1.8, networking and game objects are in different threads, 
 * custom packet can no longer be used to sync tileEntity fields from server to client.
 * </p>
 * For tileEntity field synchronization, override methods in tileEntity instead, and use</p>
 * world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 1);
 * to initiate a server->client sync
 */
public class TileCable extends TileEntitySE implements ITileRenderingInfoSyncHandler{
	private boolean[] connections = new boolean[]{false,false,false,false,false,false};
	
	public boolean[] getConnections(){
		return connections;
	}
	
	@Override
	public void invalidate()
    {
        super.invalidate();
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
	
	//TileEntity	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
	}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		return compound;
	}

	
	////////////////////////////////////////
	//Server->Client sync
	////////////////////////////////////////
	@Override
	public void sendRenderingInfoToClient() {
		//Update connection
		for (EnumFacing dir: EnumFacing.VALUES){
			TileEntity neighborTE = this.world.getTileEntity(this.pos.offset(dir));
			if (neighborTE instanceof TileCable)
				connections[dir.getIndex()] = true;
			else
				connections[dir.getIndex()] = false;
		}
		
		//Initiate Server->Client synchronization
		markTileEntityForS2CSync();
	}
	
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){
		byte bc = 0x00;
		if (connections[0]) bc |= 1;
		if (connections[1]) bc |= 2;
		if (connections[2]) bc |= 4;
		if (connections[3]) bc |= 8;
		if (connections[4]) bc |= 16;
		if (connections[5]) bc |= 32;
		nbt.setByte("connections", bc);
	}
	
	@SideOnly(value = Side.CLIENT)
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		byte bc = nbt.getByte("connections");
		
		connections[0] = (bc & 1) > 0;
		connections[1] = (bc & 2) > 0;
		connections[2] = (bc & 4) > 0;
		connections[3] = (bc & 8) > 0;
		connections[4] = (bc & 16) > 0;
		connections[5] = (bc & 32) > 0;
	
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
	}
}
