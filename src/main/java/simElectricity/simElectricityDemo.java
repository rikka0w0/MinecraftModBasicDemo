package simElectricity;

import simElectricity.Network.MessageTileEntityUpdate;
import simElectricity.Network.NetworkManager;
import simElectricity.blocks.tile.TileCable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

//https://github.com/TheGreyGhost/MinecraftByExample

@Mod(modid = SEConstants.MODID, name = SEConstants.MODNAME, version = SEConstants.VERSION)
public class simElectricityDemo {
	
    @SidedProxy(clientSide="simElectricity.ClientProxy", serverSide="simElectricity.CommonProxy") 
    public static CommonProxy proxy;
    public SimpleNetworkWrapper networkChannel;
    
    @Instance(SEConstants.MODID)
    public static simElectricityDemo instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	ItemRegistery.init();
    	BlockRegistery.init();
    	
    	MinecraftForge.EVENT_BUS.register(new NetworkManager());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	GameRegistry.registerTileEntity(TileCable.class, "tile_cable");
    	
    	proxy.registerRenders();
    	
        //Register network channel
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(SEConstants.MODID);
        networkChannel.registerMessage(MessageTileEntityUpdate.Handler.class, MessageTileEntityUpdate.class, 0, Side.CLIENT);
        networkChannel.registerMessage(MessageTileEntityUpdate.Handler.class, MessageTileEntityUpdate.class, 1, Side.SERVER);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){

    }
}
