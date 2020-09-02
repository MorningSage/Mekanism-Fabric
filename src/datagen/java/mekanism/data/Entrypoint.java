package mekanism.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.Bootstrap;
import net.minecraft.data.DataGenerator;

public class Entrypoint implements PreLaunchEntrypoint {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void dump() throws Exception {
        Path output = Paths.get("../src/generated/resources");

        LOGGER.info("Writing generated resources to {}", output.toAbsolutePath());

        DataGenerator generator = new DataGenerator(output, Collections.emptyList());
        //generator.install(new BlockDropProvider(output));
        //generator.install(new SlabStairRecipes(output));
        //generator.install(new ConventionTagProvider(output));
        //generator.install(new ToolTagProviders(output));
        generator.run();
    }

    @Override
    public void onPreLaunch() {
        if (!"true".equals(System.getProperty("mekanism.generateData"))) {
            System.out.println("Mekanism: Skipping data generation. System property not set.");
            return;
        }

        Bootstrap.initialize();

        try {
            dump();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);

    }

}
