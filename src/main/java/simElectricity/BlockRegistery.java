package simElectricity;

import simElectricity.blocks.BasicBlock;
import simElectricity.blocks.MultiBlock;
import simElectricity.utils.SEBlock;


public class BlockRegistery {
    public static SEBlock tutBlock;
    public static SEBlock multiBlock;
	
	public static void init(){
    	tutBlock = new BasicBlock();
    	multiBlock = new MultiBlock();
	}
}
