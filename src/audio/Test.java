package audio;

import java.util.concurrent.TimeUnit;

public class Test {
	public static void main(String[] args) {
		Sounds audioController=Sounds.intro;
		System.out.println("Playing intro sound");
		audioController.playSound(Sounds.intro);
		sleep(10);
		System.out.println("Playing chomp sound");
		audioController.playSound(Sounds.chomp);
		sleep(10);
		System.out.println("Muting sound");
		audioController.toggleMute();
		System.out.println("Playing intro sound (but nothing should happen)");
		audioController.playSound(Sounds.intro);
		sleep(10);
		System.out.println("Unmuting sound");
		audioController.toggleMute();
		System.out.println("Playing death sound (should now play)");
		audioController.playSound(Sounds.death);
		sleep(5);
	}
	
	private static void sleep(int x) {
		try {
			TimeUnit.SECONDS.sleep(x);
		} catch (Exception e) {
		}
	}
}
