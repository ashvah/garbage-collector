package gc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MarkSweepHeap extends Heap {
	private static final int SIZE = -1;
	private static final int MARKER = -2;

	private int markTag = 0;
	private Set<Integer> allocatedObjectAddresses = new HashSet<Integer>();

	public MarkSweepHeap(int size) {
		super(size);
	}

	public void allocate(Var v, int size) throws InsufficientMemory {
		try {
			allocateObject(v, size);
		} catch (InsufficientMemory e) {
			markAndSweep();
			allocateObject(v, size);
		}
		// TODO
		allocatedObjectAddresses.add(v.addr);
	}

	/**
	 * Allocate memory with 2 extra slots, one for the object size, the other for
	 * the marker.
	 */
	private void allocateObject(Var v, int size) throws InsufficientMemory {
		super.allocate(v, size + 2);
		// TODO
		v.addr += 2;
		data[v.addr + SIZE] = size;
		data[v.addr + MARKER] = 0;
	}

	private void markAndSweep() {
		// TODO
		for (Iterator<Var> it = reachable.iterator(); it.hasNext();) {
			Var v = it.next();
			if (!v.isNull())
				mark(v.addr);
		}
		for (Iterator<Integer> it = allocatedObjectAddresses.iterator(); it.hasNext();) {
			if (sweep(it.next()))
				it.remove();
		}
	}

	private void mark(int addr) {
		// TODO
		data[addr + MARKER] = 1;
		for (int i = addr; i < addr + data[addr + SIZE]; ++i) {
			if (data[i] != -1)
				mark(data[i]);
		}
	}

	private boolean sweep(int addr) {
		// TODO
		if (data[addr + MARKER] == markTag) {
			freelist.release(addr - 2, data[addr + SIZE] + 2);
			data[addr + MARKER] = 0;
			return true;
		}
		data[addr + MARKER] = 0;
		return false;
	}
}
