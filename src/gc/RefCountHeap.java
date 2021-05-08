package gc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A reference-counting heap.
 */
public class RefCountHeap extends Heap {
	private static final int SIZE = -1;
	private static final int COUNTER = -2;

	public RefCountHeap(int size) {
		super(size);
	}

	public void endScope() {
		// TODO decrease counters
		ArrayList<Var> list = currentScope();
		Var v;
		for (int i = 0; i < list.size(); i++) {
			v = list.get(i);
			if (!v.isNull())
				decreaseCounter(v.addr);
		}
		super.endScope();
	}

	/**
	 * Allocate memory with 2 extra slots, one for the object size, the other for
	 * the reference counter.
	 */
	public void allocate(Var v, int size) throws InsufficientMemory {
		super.allocate(v, size + 2);
		v.addr += 2;
		data[v.addr + SIZE] = size;
		data[v.addr + COUNTER] = 1;
	}

	public void assign(Var v1, Var v2) {
		if (!v1.isNull())
			decreaseCounter(v1.addr);
		super.assign(v1, v2);
		increaseCounter(v1.addr);
	}

	public void readField(Var v1, Var v2, int fieldOffset) {
		// TODO decrease counter
		if (!v1.isNull())
			decreaseCounter(v1.addr);
		super.readField(v1, v2, fieldOffset);
		// TODO increase counter
		if (v1.addr != -1)
			increaseCounter(v1.addr);
	}

	public void writeField(Var v1, int fieldOffset, Var v2) {
		// TODO decrease counter
		if (data[v1.addr + fieldOffset] != -1)
			decreaseCounter(data[v1.addr + fieldOffset]);
		super.writeField(v1, fieldOffset, v2);
		// TODO increase counter
		if (v2.addr != -1)
			increaseCounter(v2.addr);
	}

	private void increaseCounter(int addr) {
		// TODO
		data[addr + COUNTER] += 1;
	}

	private void decreaseCounter(int addr) {
		// TODO
		if (data[addr + COUNTER] <= 0)
			return;

		data[addr + COUNTER] -= 1;

		if (data[addr + COUNTER] == 0) {
			int size = data[addr + SIZE];
			freelist.release(addr - 2, size + 2);
			for (int i = addr; i < addr + size; ++i) {
				if (data[i] != -1) {
					decreaseCounter(data[i]);
				}
			}
			Arrays.fill(data, addr - 2, addr + size, -1);
		}
	}
}
