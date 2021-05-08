package gc;

public final class Var {
	protected int addr;

	protected Var() {
		setNull();
	}

	protected boolean isNull() {
		return addr == -1;
	}

	protected void setNull() {
		addr = -1;
	}
}
