package moe.mickey.minecraft.skin.fx;

import static moe.mickey.minecraft.skin.fx.FunctionHelper.*;

import java.io.File;
import java.io.FileInputStream;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class SkinCanvas extends Group {
	
	public static final Image ALEX = new Image(SkinCanvas.class.getResourceAsStream("/alex.png"));
	public static final Image STEVE = new Image(SkinCanvas.class.getResourceAsStream("/steve.png"));
	public static final Image CHOCOLATE = new Image(SkinCanvas.class.getResourceAsStream("/chocolate.png"));
	
	public static final SkinCube ALEX_LARM = new SkinCube(3, 12, 4, 14F / 64F, 16F / 64F, 32F / 64F, 48F / 64F, true);
	public static final SkinCube ALEX_RARM = new SkinCube(3, 12, 4, 14F / 64F, 16F / 64F, 40F / 64F, 16F / 64F, true);
	
	public static final SkinCube STEVEN_LARM = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 32F / 64F, 48F / 64F, false);
	public static final SkinCube STEVEN_RARM = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 40F / 64F, 16F / 64F, false);
	
	protected Image skin;
	protected boolean isSlim;
	
	protected double preW, preH, sensitivity;
	protected boolean msaa;
	
	protected SubScene subScene;
	protected Group root = new Group();
	
	protected SkinMultipleCubes headOuter = new SkinMultipleCubes(8, 8,  8, 32F / 64F, 0F, 1.125, 0.2, false);
	protected SkinMultipleCubes bodyOuter = new SkinMultipleCubes(8, 12, 4, 16F / 64F, 32F / 64F, 1.125, 0.2, false);
	protected SkinMultipleCubes larmOuter = new SkinMultipleCubes(4, 12, 4, 48F / 64F, 48F / 64F, 1.125, 0.2, true);
	protected SkinMultipleCubes rarmOuter = new SkinMultipleCubes(4, 12, 4, 40F / 64F, 32F / 64F, 1.125, 0.2, true);
	protected SkinMultipleCubes llegOuter = new SkinMultipleCubes(4, 12, 4, 0F, 48F / 64F, 1.125, 0.2, true);
	protected SkinMultipleCubes rlegOuter = new SkinMultipleCubes(4, 12, 4, 0F, 32F / 64F, 1.125, 0.2, true);
	
	protected SkinCube head = new SkinCube(8, 8,  8, 32F / 64F, 16F / 64F, 0F, 0F, false);
	protected SkinCube body = new SkinCube(8, 12, 4, 24F / 64F, 16F / 64F, 16F / 64F, 16F / 64F, false);
	protected SkinCube larm = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 32F / 64F, 48F / 64F, false);
	protected SkinCube rarm = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 40F / 64F, 16F / 64F, false);
	protected SkinCube lleg = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 16F / 64F, 48F / 64F, false);
	protected SkinCube rleg = new SkinCube(4, 12, 4, 16F / 64F, 16F / 64F, 0F, 16F / 64F, false);
	
	protected Rotate larmRotate = new Rotate(0, -larm.getWidth() / 2, -larm.getHeight() / 2, 0, Rotate.Z_AXIS);
	protected Rotate rarmRotate = new Rotate(0, +rarm.getWidth() / 2, -rarm.getHeight() / 2, 0, Rotate.Z_AXIS);
	
	protected PerspectiveCamera camera = new PerspectiveCamera(true);
	
	protected Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
	protected Rotate yRotate = new Rotate(180, Rotate.Y_AXIS);
	protected Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);
	protected Translate translate = new Translate(0, 0, -80);
	protected Scale scale = new Scale(1, 1);
	
	protected float armShakingMaxAngle = 7.5F;
	protected float shakingSpeed = .5F;
	
	protected AnimationTimer shaking = new AnimationTimer() {
		float lastRotate = 0;
		int flag = 1;
		long last = 0;
		@Override
		public void handle(long now) {
			float rotate = (now - last) / 1000_000_000F * armShakingMaxAngle * shakingSpeed;
			rotate %= armShakingMaxAngle;
			rotate = lastRotate + rotate * flag;
			if (rotate < 0 || rotate > armShakingMaxAngle) {
				flag *= -1;
				rotate -= rotate % armShakingMaxAngle;
			}
			lastRotate = rotate;
			larmRotate.setAngle(+rotate);
			rarmRotate.setAngle(-rotate);
			last = now;
		}
	};
	
	private double lastX = -1, lastY = -1;
	
	public Image getSkin() {
		return skin;
	}
	
	public void updateSkin(Image skin, boolean isSlim) {
		if (SkinHelper.isNoRequest(skin) && SkinHelper.isSkin(skin)) {
			this.skin = SkinHelper.x32Tox64(skin);
			int multiple = Math.max((int) (1024 / skin.getWidth()), 1);
			if (multiple > 1)
				this.skin = SkinHelper.enlarge(this.skin, multiple);
			if (this.isSlim != isSlim)
				updateSkinModel(isSlim);
			bindMaterial();
		}
	}
	
	protected void updateSkinModel(boolean isSlim) {
		this.isSlim = isSlim;
		alwayB(SkinMultipleCubes::setWidth, isSlim ? 3 : 4, larmOuter, rarmOuter);
		alwayB(SkinCube::setWidth, isSlim ? 3D : 4D, larm, rarm);
		
		alwayB(Node::setTranslateX, -(body.getWidth() + larm.getWidth()) / 2, larmOuter, larm);
		alwayB(Node::setTranslateX, +(body.getWidth() + rarm.getWidth()) / 2, rarmOuter, rarm);
		if (isSlim) {
			larm.setModel(ALEX_LARM.getModel());
			rarm.setModel(ALEX_RARM.getModel());
		} else {
			larm.setModel(STEVEN_LARM.getModel());
			rarm.setModel(STEVEN_RARM.getModel());
		}
		
		larmRotate.setPivotX(-larm.getWidth() / 2);
		rarmRotate.setPivotX(+rarm.getWidth() / 2);
	}
	
	public double getSensitivity() {
		return sensitivity;
	}
	
	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public SkinCanvas(double preW, double preH) {
		this(STEVE, preW, preH, true, .5);
	}
	
	public SkinCanvas(Image skin, double preW, double preH, boolean msaa, double sensitivity) {
		this.skin = skin;
		this.preW = preW;
		this.preH = preH;
		this.msaa = msaa;
		this.sensitivity = sensitivity;
		
		init();
	}
	
	protected Material createMaterial() {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(skin);
		return material;
	}
	
	protected void bindMaterial() {
		Material material = createMaterial();
		for (Node node : root.getChildren())
			if (node instanceof Shape3D)
				((Shape3D) node).setMaterial(material);
			else if (node instanceof SkinMultipleCubes)
				((SkinMultipleCubes) node).updateSkin(skin);
	}
	
	protected Group createPlayerModel() {
		alwayB(Node::setTranslateY, -(body.getHeight() + head.getHeight()) / 2, headOuter, head);
		
		alwayB(Node::setTranslateX, -(body.getWidth() + larm.getWidth()) / 2, larmOuter, larm);
		alwayB(Node::setTranslateX, +(body.getWidth() + rarm.getWidth()) / 2, rarmOuter, rarm);
		
		alwayB(link2(Node::getTransforms, ObservableList::add), larmRotate, larmOuter, larm);
		alwayB(link2(Node::getTransforms, ObservableList::add), rarmRotate, rarmOuter, rarm);
		
		alwayB(Node::setTranslateX, -(body.getWidth() - lleg.getWidth()) / 2, llegOuter, lleg);
		alwayB(Node::setTranslateX, +(body.getWidth() - rleg.getWidth()) / 2, rlegOuter, rleg);
		
		alwayB(Node::setTranslateY, +(body.getHeight() + lleg.getHeight()) / 2, llegOuter, lleg);
		alwayB(Node::setTranslateY, +(body.getHeight() + rleg.getHeight()) / 2, rlegOuter, rleg);
		
		root.getTransforms().addAll(xRotate);
		
		root.getChildren().addAll(
				headOuter, head,
				bodyOuter, body,
				larmOuter, larm, 
				rarmOuter, rarm,
				llegOuter, lleg,
				rlegOuter, rleg
		);
		updateSkin(skin, false);
		
		return root;
	}
	
	protected SubScene createSubScene() {
		Group group = new Group();
		group.getChildren().add(createPlayerModel());
		group.getTransforms().add(zRotate);
		
		camera.getTransforms().addAll(yRotate, translate, scale);
		
		subScene = new SubScene(group, preW, preH, true,
				msaa ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        
        return subScene;
	}
	
	protected void init() {
		getChildren().add(createSubScene());
		
		addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			lastX = -1;
			lastY = -1;
		});
		addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			if (!(lastX == -1 || lastY == -1)) {
				if (e.isAltDown() || e.isControlDown() || e.isShiftDown()) {
					if (e.isShiftDown())
						zRotate.setAngle(zRotate.getAngle() - (e.getSceneY() - lastY) * sensitivity);
					if (e.isAltDown())
						yRotate.setAngle(yRotate.getAngle() + (e.getSceneX() - lastX) * sensitivity);
					if (e.isControlDown())
						xRotate.setAngle(xRotate.getAngle() + (e.getSceneY() - lastY) * sensitivity);
				} else {
					double yaw = yRotate.getAngle() + (e.getSceneX() - lastX) * sensitivity;
					yaw %= 360;
					if (yaw < 0)
						yaw += 360;
					
					int flagX = yaw < 90 || yaw > 270 ? 1 : -1;
					int flagZ = yaw < 180 ? -1 : 1;
					double kx = Math.abs(90 - yaw % 180) / 90 * flagX, kz = Math.abs(90 - (yaw + 90) % 180) / 90 * flagZ;
					
					xRotate.setAngle(xRotate.getAngle() + (e.getSceneY() - lastY) * sensitivity * kx);
					yRotate.setAngle(yaw);
					zRotate.setAngle(zRotate.getAngle() + (e.getSceneY() - lastY) * sensitivity * kz);
				}
			}
			lastX = e.getSceneX();
			lastY = e.getSceneY();
		});
		addEventHandler(ScrollEvent.SCROLL, e -> {
			double delta = (e.getDeltaY() > 0 ? 1 : e.getDeltaY() == 0 ? 0 : -1) / 10D * sensitivity;
			scale.setX(Math.min(Math.max(scale.getX() - delta, 0.1), 10));
			scale.setY(Math.min(Math.max(scale.getY() - delta, 0.1), 10));
		});
		addEventHandler(DragEvent.DRAG_OVER, e -> {
			if (e.getDragboard().hasFiles()) {
                File file = e.getDragboard().getFiles().get(0);
                if (file.getAbsolutePath().endsWith(".png"))
                	e.acceptTransferModes(TransferMode.COPY);
            }
		});
		addEventHandler(DragEvent.DRAG_DROPPED, e -> {
			if (e.isAccepted())
				try {
					File skin = e.getDragboard().getFiles().get(0);
					updateSkin(new Image(new FileInputStream(skin)), skin.getName().contains("3"));
				} catch (Exception ex) {
					// Ignore
				}
		});
		
		shaking.start();
	}
	
	public void destroy() {
		shaking.stop();
	}

}
