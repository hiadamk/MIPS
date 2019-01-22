package client;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import utils.Settings;
import utils.enums.Controls;



public class KeyController implements EventHandler<KeyEvent>{
	
	private Controls mapping;
	
	public Controls getActiveKey() {
		return activeKey;
	}
	
	private Controls activeKey;
	
	
	public KeyController() {
		mapping = null;
		activeKey = null;
	}
	
	@Override
	public void handle(KeyEvent e) {
		if(mapping != null) {
			Settings.setKey(mapping, e.getCode());
			mapping=null;
			return;
		}
		if(e.getCode() == Settings.getKey(Controls.UP)) {
			activeKey = Controls.UP;
		}else if(e.getCode() == Settings.getKey(Controls.DOWN)) {
			activeKey = Controls.DOWN;
		}else if(e.getCode() == Settings.getKey(Controls.LEFT)) {
			activeKey = Controls.LEFT;
		}else if(e.getCode() == Settings.getKey(Controls.RIGHT)) {
			activeKey = Controls.RIGHT;
		}
	}
}
