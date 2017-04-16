package simElectricity.items;

import simElectricity.utils.SEItem;
import net.minecraft.creativetab.CreativeTabs;

public class MultiItem extends SEItem{
	public static final String[] subNames = {"type1","type2","type3"};

	public MultiItem() {
		super("multi_item", true);
	}

	@Override
	public void beforeRegister() {
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	@Override
    public String[] getSubItemUnlocalizedNames(){
    	return subNames;
    }

}
