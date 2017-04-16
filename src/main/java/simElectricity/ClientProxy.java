package simElectricity;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import simElectricity.utils.client.SEItemRenderRegistery;

public class ClientProxy extends CommonProxy{
	@Override
	public World getClientWorld(){
		return Minecraft.getMinecraft().world;
	}
	
	@Override
	public IThreadListener getClientThread() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public void registerRenders() {

	}
}
