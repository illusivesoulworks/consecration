package c4.consecration;

import c4.consecration.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(	modid = Consecration.MODID,
		name = Consecration.MODNAME,
		version = Consecration.MODVER,
		dependencies = "required-after:forge@[14.23.1.2555,)")
public class Consecration {

    public static final String MODID = "consecration";
    public static final String MODNAME = "Consecration";
    public static final String MODVER = "0.0.1";

    @SidedProxy(clientSide = "c4.consecration.proxy.ClientProxy", serverSide = "c4.consecration.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Consecration instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        proxy.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit(evt);
    }
}
