package simElectricity.Network;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

import simElectricity.simElectricityDemo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageTileEntityUpdate implements IMessage{
	private int xCoord, yCoord, zCoord, dimensionID,
				fieldsCount; //-1:Facing, -2:FunctionalSide, 0: nothing to update, >1:fields
	private boolean markForUpdateOnClient;
	private String[] fields;
	private short[] types;
	private Object[] values;

    public MessageTileEntityUpdate() {
    }

    private void initHead(TileEntity te){
        if (te == null)
            return;

        if (te.getWorld() == null)
            return;

        World world = te.getWorld();
        BlockPos pos = te.getPos();
    	this.xCoord = pos.getX();
    	this.yCoord = pos.getY();
    	this.zCoord = pos.getZ();
    	this.dimensionID = world.provider.getDimension();
    }

    //Server -> Client only
    public MessageTileEntityUpdate(TileEntity te, EnumFacing direction, boolean isFacing) {
    	initHead(te);
    	this.markForUpdateOnClient = true;
    	this.fieldsCount = (byte) (isFacing ? -1 : -2);
    	this.values = new EnumFacing[]{direction};
    }

    //Server <-> Client
    public MessageTileEntityUpdate(TileEntity te, boolean markForUpdateOnClient, String[] fields) {
    	initHead(te);
    	this.markForUpdateOnClient = markForUpdateOnClient;
    	
    	if (fields.length == 1 && fields[0] == null)
    		this.fieldsCount = 0;
    	else
    		this.fieldsCount = fields.length;

    	if (this.fieldsCount == 0){
    		//SEUtils.logWarn("No fields to be update! This might be a bug!");
    		return;
    	}

    	this.fields = fields;
    	this.types = new short[fieldsCount];
    	this.values = new Object[fieldsCount];
    	try{

    		for (int i=0; i < fieldsCount; i++){
	    		Field f = te.getClass().getField(fields[i]);

	    		values[i] = f.get(te);

				if (f.getType() == boolean.class){
					types[i] = 0;
				}else if (f.getType() == int.class){
					types[i] = 1;
				}else if (f.getType() == float.class){
					types[i] = 2;
				}else if (f.getType() == double.class){
					types[i] = 3;
				}else if (f.getType() == String.class){
					types[i] = 4;
				}else if (f.getType() == boolean[].class){
					types[i] = 5;
				}else if (f.getType() == int[].class){
					types[i] = 6;
				}else if (f.getType() == EnumFacing.class){
					types[i] = 7;
				}else {
					types[i] = -1;
				}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(xCoord);
    	buf.writeInt(yCoord);
    	buf.writeInt(zCoord);
    	buf.writeInt(dimensionID);
    	buf.writeBoolean(markForUpdateOnClient);
    	buf.writeInt(fieldsCount);

    	if (fieldsCount == -1 || fieldsCount == -2){
    		buf.writeByte(((EnumFacing)values[0]).ordinal());
    	}

    	for (int i=0; i< fieldsCount;i++){
	    	buf.writeShort(types[i]);
	    	ByteBufUtils.writeUTF8String(buf,fields[i]);
	    	switch (types[i]){
	    	case 0:
	    		buf.writeBoolean((Boolean) values[i]);
	    		break;
	    	case 1:
	    		buf.writeInt((Integer) values[i]);
	    		break;
	    	case 2:
	    		buf.writeFloat((Float) values[i]);
	    		break;
	    	case 3:
	    		buf.writeDouble((Double) values[i]);
	    		break;
	    	case 4:
	    		ByteBufUtils.writeUTF8String(buf,(String) values[i]);
	    		break;
	    	case 5:
	            buf.writeInt(((boolean[]) values[i]).length);
	            for (boolean j : (boolean[]) values[i]) {
	                buf.writeBoolean(j);
	            }
	            break;
	    	case 6:
	            buf.writeInt(((int[]) values[i]).length);
	            for (int j : (int[]) values[i]) {
	                buf.writeInt(j);
	            }
	            break;
	    	case 7:
	    		buf.writeByte(((EnumFacing)values[i]).ordinal());
	    		break;
	    	}
    	}
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	xCoord = buf.readInt();
    	yCoord = buf.readInt();
    	zCoord = buf.readInt();
    	dimensionID = buf.readInt();
    	markForUpdateOnClient = buf.readBoolean();
    	fieldsCount = buf.readInt();

    	if (fieldsCount > 0){
	    	fields = new String[fieldsCount];
	    	types = new short[fieldsCount];
	    	values = new Object[fieldsCount];

	    	for (int i=0; i< fieldsCount;i++){
		    	types[i] = buf.readShort();
		    	fields[i] = ByteBufUtils.readUTF8String(buf);
		    	switch (types[i]){
		    	case 0:
		    		values[i] = buf.readBoolean();
		    		break;
		    	case 1:
		    		values[i] = buf.readInt();
		    		break;
		    	case 2:
		    		values[i] = buf.readFloat();
		    		break;
		    	case 3:
		    		values[i] = buf.readDouble();
		    		break;
		    	case 4:
		    		values[i] = ByteBufUtils.readUTF8String(buf);
		    		break;
		    	case 5:
		            boolean[] arrayBoolean = new boolean[buf.readInt()];
		            for (int j = 0; j < arrayBoolean.length; j++) {
		                arrayBoolean[j] = buf.readBoolean();
		            }
		            values[i] = arrayBoolean;
		            break;
		    	case 6:
		            int[] arrayInt = new int[buf.readInt()];
		            for (int j = 0; j < arrayInt.length; j++) {
		                arrayInt[j] = buf.readInt();
		            }
		            values[i] = arrayInt;
		            break;
		    	case 7:
		    		values[i] = EnumFacing.getFront(buf.readByte());
		    		break;
		    	}
	    	}
    	}else if (fieldsCount == -1 || fieldsCount == -2){
	    	fields = new String[0];
	    	types = new short[0];
			values = new EnumFacing[]{EnumFacing.getFront(buf.readByte())};
		}
    }



    public static class Handler implements IMessageHandler<MessageTileEntityUpdate, IMessage> {
    	public void UpdateClient(final World world, final BlockPos pos, final String[] fields, final Object[] values, final boolean markForUpdateOnClient, final int retried){
        	TileEntity te = world.getTileEntity(pos);
        	IBlockState state = world.getBlockState(pos);
        	
            if (te == null){
            	if (retried < 100){
            		System.out.println("[DEBUG]:Another task scheduled!");
            		
            		IThreadListener tl = simElectricityDemo.proxy.getClientThread();
            		tl.addScheduledTask(new Runnable() {
    					@Override
    					public void run() {
    						UpdateClient(world, pos, fields, values, markForUpdateOnClient, retried + 1);
    					}
            		});
            	}else{
            		System.out.println("[DEBUG]:Retried too many times!");
            	}
            	
                return;			            	
            }
            
        	//Set value to variables
        	try {
        		for (int i=0; i<fields.length;i++){
        			Field f = te.getClass().getField(fields[i]);
        			f.set(te, values[i]);
        		}
            } catch (Exception e) {
                e.printStackTrace();
            }

        	/*
        	//Update facing or functionalSide
        	if (message.fieldsCount == -1){
        		((ISidedFacing)te).setFacing((ForgeDirection) message.values[0]);
        	}else if (message.fieldsCount == -2){
        		((ISEWrenchable)te).setFunctionalSide((ForgeDirection) message.values[0]);
        	}

        	//Fire onFieldUpdate event
        	if (te instanceof INetworkEventHandler)
        		((INetworkEventHandler)te).onFieldUpdate(message.fields, message.values);
        	*/
        	
        	//Mark block for update on client side
        	if (markForUpdateOnClient)
        		world.notifyBlockUpdate(te.getPos(), world.getBlockState(te.getPos()), world.getBlockState(te.getPos()), 3);
    	}
    	
    	//http://greyminecraftcoder.blogspot.com.au/2015/01/thread-safety-with-network-messages.html
    	//
        @Override
        public IMessage onMessage(final MessageTileEntityUpdate message, final MessageContext ctx) {

        	
        	/* This is unlikely to happen, if you find error due to this, please let me know!
        	 * if (world.provider.getDimension() != message.dimensionID){
                SEUtils.logWarn("An dimensionID mismatch error occurred during sync! This could be an error");
        		return null;
        	}
        	*/

        	if (ctx.side == Side.CLIENT){
        		//Above 1.8, we have to make sure that anything in run() is called from the main thread
        		IThreadListener tl = simElectricityDemo.proxy.getClientThread();
        		tl.addScheduledTask(new Runnable() {
					@Override
					public void run() {
						
						World world = simElectricityDemo.proxy.getClientWorld();
			        	BlockPos pos = new BlockPos(message.xCoord, message.yCoord, message.zCoord);
			        	UpdateClient(world, pos, message.fields, message.values
			        					,message.markForUpdateOnClient && ctx.side == Side.CLIENT
			        					,0);
					}});
        	}else{
        		World world  = ctx.getServerHandler().playerEntity.world;
        	}


            return null;
        }
    }

}
