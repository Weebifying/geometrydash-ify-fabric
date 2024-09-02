package wb.weebify.geometrydash;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wb.weebify.geometrydash.gd.FntFontDrawer;

public class GeometryDashify implements ModInitializer {
	public static final String MOD_ID = "geometrydash-ify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static FntFontDrawer BIGFONT_DRAWER;
	public static FntFontDrawer GOLDFONT_DRAWER;

	@Override
	public void onInitialize() {
		GeometryDashify.BIGFONT_DRAWER = new FntFontDrawer("big_font-uhd");
		GeometryDashify.GOLDFONT_DRAWER = new FntFontDrawer("gold_font-uhd");
	}
}