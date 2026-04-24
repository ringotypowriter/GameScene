package skypixeldev.gamescene.config;

import java.io.File;

public class GConfigMirror extends GConfig{
    public GConfigMirror(GConfig config){
        configFile = config.getConfigFile();
        configuration = config.getConfiguration();
    }

    @Override
    public void saveToFile(){
        // NOTHING
    }
}
