package simElectricity.blocks;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import simElectricity.Network.IServerToClientSyncHanlder;
import simElectricity.blocks.tile.TileCable;
import simElectricity.utils.SEBlock;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Forge Documentation, basis of blockState
 * https://mcforge.readthedocs.io/en/latest/blockstates/states/
 * 
 * Forge BlockState
 * https://mcforge.readthedocs.io/en/latest/blockstates/forgeBlockstates/#sub-models
 * 
 * Vanilla minecraft BlockState, Block model, Item model
 * http://minecraft.gamepedia.com/Model
 * 
 * Creating Minecraft >1.8 texture
 * http://greyminecraftcoder.blogspot.com.au/2014/12/block-models-texturing-quads-faces.html
 * 
 * Block rendering
 * http://greyminecraftcoder.blogspot.com.au/2014/12/block-rendering-18.html
 * http://greyminecraftcoder.blogspot.com.au/2014/12/block-models-18.html
 * 
 * Forge blockState JSON structure
 * https://gist.github.com/RainWarrior/0618131f51b8d37b80a6
 * 
 * 1.8 Rendering Primer by williewillus (formatted to markdown by gigaherz)
 * https://gist.github.com/williewillus/57d7093efa80163e96e0
 */
public class MultiBlock extends SEBlock implements ITileEntityProvider{
	public static final String[] subNames = {"d4", "d6", "d8", "d10", "d12", "d14"};
	
	public static IProperty<Boolean> pBoolDown;
	public static IProperty<Boolean> pBoolUp;
	public static IProperty<Boolean> pBoolNorth;
	public static IProperty<Boolean> pBoolSouth;
	public static IProperty<Boolean> pBoolWest;
	public static IProperty<Boolean> pBoolEast;
	public static IProperty<Integer> pIntCenter;	//0-5 DUNSWE 6 MultipleConnection 7 NoConnection
	public static IProperty<Integer> pIntType;
	
    public MultiBlock() {
    	super("multi_block", Material.ROCK, true);
    }
    
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return false;
    }
	

	
    //Block states
	@Override
	public void registerBlockState(LinkedList<IProperty> properties){
		pIntType = PropertyInteger.create("atype",0 , subNames.length - 1);
		pBoolDown = PropertyBool.create("bdown");
		pBoolUp = PropertyBool.create("bup");
		pBoolNorth = PropertyBool.create("bnorth");
		pBoolSouth = PropertyBool.create("bsouth");
		pBoolWest = PropertyBool.create("bwest");
		pBoolEast = PropertyBool.create("beast");
		pIntCenter = PropertyInteger.create("center",0 , 7);
		
		properties.add(pIntType);
		properties.add(pBoolDown);
		properties.add(pBoolUp);
		properties.add(pBoolNorth);
		properties.add(pBoolSouth);
		properties.add(pBoolWest);
		properties.add(pBoolEast);
		properties.add(pIntCenter);
	}
	
	@Override
	public IBlockState setDefaultBlockState(IBlockState baseState){
		return super.setDefaultBlockState(baseState)
				.withProperty(pIntType, 0)
				.withProperty(pBoolDown, false)
				.withProperty(pBoolUp, false)
				.withProperty(pBoolNorth, false)
				.withProperty(pBoolSouth, false)
				.withProperty(pBoolWest, false)
				.withProperty(pBoolEast, false)
				.withProperty(pIntCenter, 0);
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(pIntType, meta & 15);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state)
    {
		int meta = state.getValue(pIntType);
		meta = meta & 15;
		return meta;
    }
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
		TileEntity tileEntity = (worldIn instanceof ChunkCache) 
				? ((ChunkCache)worldIn).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) 
				: worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileCable){
			TileCable tileCable = (TileCable) tileEntity;
			boolean[] connections = tileCable.getConnections();
		
			
			int center = 0;
			for (boolean b: connections){
				if (b)
					center++;
			}
			
			if (center == 0)
				center = 7;	//NC
			else if (center == 1){
				if (connections[0])
					center = 1;
				else if (connections[1])
					center = 0;
				else if (connections[2])
					center = 3;
				else if (connections[3])
					center = 2;
				else if (connections[4])
					center = 5;
				else if (connections[5])
					center = 4;
			}else
				center = 6;	//MC
			
			
			int meta = this.getMetaFromState(state);
			
			return state.withProperty(pIntType, meta)
						.withProperty(pBoolDown, connections[0])
						.withProperty(pBoolUp, connections[1])
						.withProperty(pBoolNorth, connections[2])
						.withProperty(pBoolSouth, connections[3])
						.withProperty(pBoolWest, connections[4])
						.withProperty(pBoolEast, connections[5])
						.withProperty(pIntCenter, center);
		}
		
		int meta = this.getMetaFromState(state);
		return state.withProperty(pIntType, meta);
    }
	
	//TileEntity
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCable();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
    //Block
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	if (worldIn.isRemote)
    		return;				//Supposed to process on server side only!
    	
    	TileCable cable = (TileCable) worldIn.getTileEntity(pos);
    	cable.sendRenderingInfoToClient();

		for (EnumFacing dir: EnumFacing.VALUES){
			TileEntity neighborTE = worldIn.getTileEntity(pos.offset(dir));
			if (neighborTE instanceof IServerToClientSyncHanlder)
				((IServerToClientSyncHanlder) neighborTE).sendRenderingInfoToClient();
		}
    	return;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos neighborPos)
    {
    	if (worldIn.isRemote)
    		return;				//Supposed to process on server side only!
    	
    	//We know our tileEntity must be at this location!
    	TileCable cable = (TileCable) worldIn.getTileEntity(pos);
    	cable.sendRenderingInfoToClient();
    }
    
    
    //Rendering
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
    
	//Collision Box
	@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_){
		super.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));
		
		
    }
	
	@Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return blockState.getBoundingBox(worldIn, pos);
    }
}
