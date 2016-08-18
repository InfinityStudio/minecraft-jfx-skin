package moe.mickey.minecraft.skin.fx;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import javafx.animation.AnimationTimer;

public class SkinAnimationPlayer {
	
	protected final Random random = new Random();
	protected LinkedList<SkinAnimation> animations = new LinkedList<>();
	protected SkinAnimation playing;
	protected boolean running;
	protected int weightedSum = 0;
	protected long lastPlayTime = -1L;
	protected AnimationTimer animationTimer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			if (playing == null || !playing.isPlaying() && now - lastPlayTime > 10_000_000_000L) {
				int nextAni = random.nextInt(weightedSum);
				SkinAnimation tmp = null;
				for (SkinAnimation animation : animations) {
					nextAni -= animation.getWeight();
					if (nextAni < 0)
						break;
					if (nextAni == 0) {
						tmp = animation;
						break;
					}
					tmp = animation;
				}
				playing = tmp;
				if (playing == null && animations.size() > 0)
					playing = animations.getFirst();
				if (playing != null) {
					playing.playFromStart();
					lastPlayTime = now;
				}
			}
		}
	};
	
	public void addSkinAnimation(SkinAnimation... animations) {
		this.animations.addAll(Arrays.asList(animations));
		this.weightedSum = this.animations.stream().mapToInt(SkinAnimation::getWeight).sum();
		if (!running && weightedSum > 0) {
			animationTimer.start();
			running = true;
		}
	}
	
	public void stop() {
		animationTimer.stop();
		playing.stop();
	}

}
