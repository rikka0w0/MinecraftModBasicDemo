package simElectricity.Network;

import simElectricity.simElectricityDemo;
import simElectricity.blocks.tile.TileCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class NetworkManager{

    public NetworkManager() {
        
    }

    @Deprecated
    public static void updateTileEntityFields(TileEntity tileEntity, boolean updateClientBlock, String... fields) {
    	simElectricityDemo.instance.networkChannel.sendToDimension(
    			new MessageTileEntityUpdate(tileEntity, updateClientBlock, fields),
    			tileEntity.getWorld().provider.getDimension());
    }

    @Deprecated
    public static void updateTileEntityFieldsToServer(TileEntity tileEntity, String... fields) {
    	simElectricityDemo.instance.networkChannel.sendToServer(
    			new MessageTileEntityUpdate(tileEntity, false, fields));
    }


    /*
    public void updateNetworkFields(TileEntity tileEntity){
    	
    	if (!(tileEntity instanceof INetworkEventHandler))
    		return;



    	INetworkEventHandler networkEventHandler = (INetworkEventHandler) tileEntity;

    	ArrayList<String> fields = new ArrayList<String>();

    	networkEventHandler.addNetworkFields(fields);


    	//Return when no field needs to be updated
    	if (fields.isEmpty())
    		return;

    	updateTileEntityFields(tileEntity, fields.toArray(new String[0]));
    }



    @Override

    public void updateFunctionalSide(TileEntity tileEntity){

    	if (!(tileEntity instanceof ISEWrenchable))

    		return;



    	SimElectricity.instance.networkChannel.sendToDimension(

    			new MessageTileEntityUpdate(tileEntity, ((ISEWrenchable)tileEntity).getFunctionalSide(), false),

    			tileEntity.getWorldObj().provider.dimensionId);

    }



    @Override

    public void updateFacing(TileEntity tileEntity){

    	if (!(tileEntity instanceof ISidedFacing))

    		return;



    	SimElectricity.instance.networkChannel.sendToDimension(

    			new MessageTileEntityUpdate(tileEntity, ((ISidedFacing)tileEntity).getFacing(), true),

    			tileEntity.getWorldObj().provider.dimensionId);

    }
	*/




    @SubscribeEvent
    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {   	
        Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().chunkXPos, event.getChunk().chunkZPos);
        for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
            if (tileEntity instanceof ITileRenderingInfoSyncHandler){
            	((ITileRenderingInfoSyncHandler)tileEntity).sendRenderingInfoToClient();
            }
        }
    }
 }
