package c4.consecration.proxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        super.preInit(evt);
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {
        super.postInit(evt);
    }
}