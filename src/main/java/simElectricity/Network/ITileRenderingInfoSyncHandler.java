package simElectricity.Network;

public interface ITileRenderingInfoSyncHandler {
	/**
	 * Send a sync. packet to client immediately, to update client rendering
	 * </p>
	 * This method will be called when the chunk containing the tileEntity is seen by a player
	 */
	public void sendRenderingInfoToClient();
}
