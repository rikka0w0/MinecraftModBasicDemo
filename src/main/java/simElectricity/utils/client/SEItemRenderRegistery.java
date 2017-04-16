package simElectricity.utils.client;

import java.util.LinkedList;

import simElectricity.utils.SEBlock;
import simElectricity.utils.SEItem;
import simElectricity.utils.SEItemBlock;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is used to register variants for block and items, 
 * item and block classes instantiate this class during pre-init phase,
 * and the registerRenders() method should be called in init phase
 */
@SideOnly(Side.CLIENT)
public class SEItemRenderRegistery implements ItemMeshDefinition{
	private boolean hasSubTypes;
	private ModelResourceLocation[] resources;
	
	public SEItemRenderRegistery(SEItem seItem){
		this.hasSubTypes = seItem.getHasSubtypes();
		
    	if (hasSubTypes){
    		//The item contains subItems
    		String[] subItemUnlocalizedNames = seItem.getSubItemUnlocalizedNames();
    		this.resources = new ModelResourceLocation[subItemUnlocalizedNames.length];
    		
    		//Create ModelResourceLocation for each of them 
    		for (int i = 0; i < subItemUnlocalizedNames.length; i++) {
    			this.resources[i] = new ModelResourceLocation(
    					seItem.getRegistryName() + "_" + subItemUnlocalizedNames[i], "inventory");
    			
    			//And let MineCraft know the existence of all subItems(Variants)
    			ModelBakery.registerItemVariants(seItem, this.resources[i]);
    		}
    	}else{
    		//The item doesn't contain any subItem, use single texture instead
    		this.resources = new ModelResourceLocation[]{new ModelResourceLocation(seItem.getRegistryName(), "inventory")};
    	}
    	
    	ModelLoader.setCustomMeshDefinition(seItem, this);
	}
	
	public SEItemRenderRegistery(SEBlock seBlock){
		SEItemBlock seItemBlock = seBlock.getItemBlock();
		this.hasSubTypes = seItemBlock.getHasSubtypes();
		
    	if (hasSubTypes){
    		//The item contains subItems
    		String[] subItemUnlocalizedNames = seBlock.getSubBlockUnlocalizedNames();
    		this.resources = new ModelResourceLocation[subItemUnlocalizedNames.length];
    		
    		//Create ModelResourceLocation for each of them 
    		for (int i = 0; i < subItemUnlocalizedNames.length; i++) {
    			this.resources[i] = new ModelResourceLocation(
    					seBlock.getRegistryName() + "_" + subItemUnlocalizedNames[i], "inventory");
    			
    			//And let MineCraft know the existence of all subItems(Variants)
    			ModelBakery.registerItemVariants(seItemBlock, this.resources[i]);
    		}
    	}else{
    		//The item doesn't contain any subItem, use single texture instead
    		this.resources = new ModelResourceLocation[]{new ModelResourceLocation(seBlock.getRegistryName(), "inventory")};
    	}
    	
    	ModelLoader.setCustomMeshDefinition(seItemBlock, this);
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		if (this.hasSubTypes)
    		return this.resources[stack.getItemDamage()];
    	else
			return this.resources[0];
	}
}
