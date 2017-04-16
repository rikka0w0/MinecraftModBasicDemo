package simElectricity.Network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IServerToClientSyncHanlder {

	/**
	 * Send a sync. packet to client immediately
	 * </p>
	 * This method will be called when the chunk containing the tileEntity is seen by a player
	 */
	public void sendRenderingInfoToClient();
}
