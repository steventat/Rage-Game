package a3;

class ControlledEntity {
	public static final int MOVE_LEFT = 0;
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_FORWARD = 2;
	public static final int MOVE_BACKWARD = 3;
	public static final int MOVE_UP = 4;
	public static final int MOVE_DOWN = 5;
	public static final int ROTATE_LEFT = 6;
	public static final int ROTATE_RIGHT = 7;
	public static final int ROTATE_UP = 8;
	public static final int ROTATE_DOWN = 9;

	private boolean[] inputs = new boolean[10];

	public boolean shouldMoveLeft() {
		return inputs[MOVE_LEFT];
	}

	public void setMoveLeft(boolean on) {
		if(on) {
			inputs[MOVE_RIGHT] = false;
			inputs[MOVE_LEFT] = true;
		} else {
			inputs[MOVE_LEFT] = false;
		}
	}

	public boolean shouldMoveRight() {
		return inputs[MOVE_RIGHT];
	}

	public void setMoveRight(boolean on) {
		if(on) {
			inputs[MOVE_LEFT] = false;
			inputs[MOVE_RIGHT] = true;
		} else {
			inputs[MOVE_RIGHT] = false;
		}
	}

	public boolean shouldMoveUp() {
		return inputs[MOVE_UP];
	}

	public void setMoveUp(boolean on) {
		if(on) {
			inputs[MOVE_DOWN] = false;
			inputs[MOVE_UP] = true;
		} else {
			inputs[MOVE_UP] = false;
		}
	}

	public boolean shouldMoveDown() {
		return inputs[MOVE_DOWN];
	}

	public void setMoveDown(boolean on) {
		if(on) {
			inputs[MOVE_UP] = false;
			inputs[MOVE_DOWN] = true;
		} else {
			inputs[MOVE_DOWN] = false;
		}
	}

	public boolean shouldMoveForward() {
		return inputs[MOVE_FORWARD];
	}

	public void setMoveForward(boolean on) {
		if(on) {
			inputs[MOVE_BACKWARD] = false;
			inputs[MOVE_FORWARD] = true;
		} else {
			inputs[MOVE_FORWARD] = false;
		}
	}

	public boolean shouldMoveBackward() {
		return inputs[MOVE_BACKWARD];
	}

	public void setMoveBackward(boolean on) {
		if(on) {
			inputs[MOVE_FORWARD] = false;
			inputs[MOVE_BACKWARD] = true;
		} else {
			inputs[MOVE_BACKWARD] = false;
		}
	}

	public boolean shouldRotateLeft() {
		return inputs[ROTATE_LEFT];
	}

	public void setRotateLeft(boolean on) {
		if(on) {
			inputs[ROTATE_RIGHT] = false;
			inputs[ROTATE_LEFT] = true;
		} else {
			inputs[ROTATE_LEFT] = false;
		}
	}

	public boolean shouldRotateRight() {
		return inputs[ROTATE_RIGHT];
	}

	public void setRotateRight(boolean on) {
		if(on) {
			inputs[ROTATE_LEFT] = false;
			inputs[ROTATE_RIGHT] = true;
		} else {
			inputs[ROTATE_RIGHT] = false;
		}
	}

	public boolean shouldRotateUp() {
		return inputs[ROTATE_UP];
	}

	public void setRotateUp(boolean on) {
		if(on) {
			inputs[ROTATE_DOWN] = false;
			inputs[ROTATE_UP] = true;
		} else {
			inputs[ROTATE_UP] = false;
		}
	}

	public boolean shouldRotateDown() {
		return inputs[ROTATE_DOWN];
	}

	public void setRotateDown(boolean on) {
		if(on) {
			inputs[ROTATE_UP] = false;
			inputs[ROTATE_DOWN] = true;
		} else {
			inputs[ROTATE_DOWN] = false;
		}
	}
}
