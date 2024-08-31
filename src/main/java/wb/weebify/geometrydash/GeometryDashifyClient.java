package wb.weebify.geometrydash;

import net.fabricmc.api.ClientModInitializer;
import wb.weebify.geometrydash.gd.MenuGameLayer;

public class GeometryDashifyClient implements ClientModInitializer {
    public static MenuGameLayer MENU_GAME_LAYER = new MenuGameLayer();

    @Override
    public void onInitializeClient() {

    }
}
