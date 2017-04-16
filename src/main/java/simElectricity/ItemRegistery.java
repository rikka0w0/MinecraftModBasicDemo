package simElectricity;

import simElectricity.items.ItemTutItem;
import simElectricity.items.MultiItem;
import simElectricity.utils.SEItem;



import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRegistery {
	
	public static SEItem tutItem;
	public static SEItem multiItem;
	
	public static void init(){
		tutItem = new ItemTutItem();
		multiItem = new MultiItem();
	}
}
