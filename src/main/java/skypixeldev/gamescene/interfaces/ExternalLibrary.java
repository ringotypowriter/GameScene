package skypixeldev.gamescene.interfaces;

import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.scene.gameObject.BaseObject;
import skypixeldev.gamescene.scene.gameObject.GObjectManager;
import skypixeldev.gamescene.scene.itemParser.ItemProvider;

public interface ExternalLibrary {
    default void registerFunction(IFunction function){
        FunctionManager.register(function);
    }
    default void registerGameObject(String name, Class<? extends BaseObject> clazz){
        GObjectManager.register(name,clazz);
    }

    void onLoadFunctions();
    void onLoadGameObjects();
    ItemProvider getItemProvider();

}
