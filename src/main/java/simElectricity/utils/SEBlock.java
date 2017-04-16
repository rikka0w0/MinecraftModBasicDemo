package simElectricity.utils;

import java.util.LinkedList;

import simElectricity.utils.client.SEItemRenderRegistery;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SEBlock extends Block{
	protected SEItemBlock itemBlock;
	
    public SEBlock(String unlocalizedName, Material material, boolean hasSubBlock) {
        super(material);
        this.setUnlocalizedName(unlocalizedName);
        this.setRegistryName(unlocalizedName);				//Key!
        
        this.setDefaultState(setDefaultBlockState(this.blockState.getBaseState()));
        this.beforeRegister();
        
        GameRegistry.register(this);
        itemBlock = new SEItemBlock(this, hasSubBlock);
        GameRegistry.register(itemBlock, this.getRegistryName());
        new SEItemRenderRegistery(this);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
    	if (itemBlock.getHasSubtypes()){
            for (int ix = 0; ix < getSubBlockUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
    	}else{
    		super.getSubBlocks(itemIn, tab, subItems);
    	}

    }
    
	@Override
	public final int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
	    return new ItemStack(itemBlock, 1, this.getMetaFromState(world.getBlockState(pos)));
	}
    
    public final SEItemBlock getItemBlock(){
    	return this.itemBlock;
    }
    
    public abstract void beforeRegister();
    
    /**
     * Only use for subBlocks
     * 
     * @return an array of unlocalized names
     */
    public String[] getSubBlockUnlocalizedNames(){
    	return null;
    }
        
	//BlockState --------------------------------------------------------------------
	/**
	 * @param properties use properties.add() to add custom blockStates
	 */
	public void registerBlockState(LinkedList<IProperty> properties){}
	
	/**
	 * Initialize properties and set their default value
	 * @param baseState use baseState.withProperty(name, value) to set default value for properties
	 * @return
	 */
	public IBlockState setDefaultBlockState(IBlockState baseState){return baseState;}
	
	@Override
	protected final BlockStateContainer createBlockState(){
		LinkedList<IProperty> customProperties = new LinkedList<IProperty>();
		registerBlockState(customProperties);
		
		IProperty[] properties = new IProperty[customProperties.size()];
		
		int i = 0;
		for (IProperty p: customProperties){
			properties[i] = p;
			i++;
		}
			
		return new BlockStateContainer(this, properties);
	}
}
