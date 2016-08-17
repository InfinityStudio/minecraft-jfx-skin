package moe.mickey.minecraft.skin.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {
	
	public SkinCanvas createSkinCanvas() {
		SkinCanvas canvas = new SkinCanvas(SkinCanvas.CHOCOLATE, 400, 400, true, .5);
		return canvas;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("FX - Minecraft skin preview");
		Scene scene = new Scene(createSkinCanvas());
		stage.setScene(scene);
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}