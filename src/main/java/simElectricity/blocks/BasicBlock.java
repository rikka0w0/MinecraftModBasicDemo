package simElectricity.blocks;

import simElectricity.utils.SEBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BasicBlock extends SEBlock{
    public BasicBlock() {
        super("tut_Block", Material.ROCK, false);
    }

	@Override
	public void beforeRegister() {
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
}