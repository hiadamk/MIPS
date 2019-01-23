package utils.enums;

public enum Direction {
	UP {
		@Override public String toString() {
			return "up";
		}
	},
	DOWN {
		@Override public String toString() {
			return "down";
		}
	},
	LEFT {
		@Override public String toString() {
			return "left";
		}
	},
	RIGHT {
		@Override public String toString() {
			return "right";
		}
	}
}
