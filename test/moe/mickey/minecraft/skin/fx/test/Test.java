package moe.mickey.minecraft.skin.fx.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import moe.mickey.minecraft.skin.fx.animation.SkinAniWavingArms;

public class Test extends Application {
	
	public SkinCanvas createSkinCanvas() {
		SkinCanvas canvas = new SkinCanvas(SkinCanvas.CHOCOLATE, 400, 400, true, .5);
		canvas.getAnimationplayer().addSkinAnimation(new SkinAniWavingArms(100, 2000, 7.5, canvas), new SkinAniRunning(100, 100, 30, canvas));
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