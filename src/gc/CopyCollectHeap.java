package gc;

import java.util.Arrays;
import java.util.Iterator;

/**
 * For simplicity, implement Fenichel's algorithm instead of Cheney's algorithm.
 * 
 * Semi-space garbage collection [Fenichel, 1969] is a copying algorithm, which
 * means that reachable objects are relocated from one address to another during
 * a collection. Available memory is divided into two equal-size regions called
 * "from-space" and "to-space".
 * 
 * Allocation is simply a matter of keeping a pointer into to-space which is
 * incremented by the amount of memory requested for each allocation (that is,
 * memory is allocated sequentially out of to-space). When there is insufficient
 * space in to-space to fulfill an allocation, a collection is performed.
 * 
 * A collection consists of swapping the roles of the regions, and copying the
 * live objects from from-space to to-space, leaving a block of free space
 * (corresponding to the memory used by all unreachable objects) at the end of
 * the to-space.
 * 
 * Since objects are moved during a collection, the addresses of all references
 * must be updated. This is done by storing a "forwarding address" for an object
 * when it is copied out of from-space. Like the mark-bit, this forwarding
 * address can be thought of as an additional field of the object, but is
 * usually implemented by temporarily repurposing some space from the object.
 * 
 * The primary benefits of semi-space collection over mark-sweep are that the
 * allocation costs are extremely low (no need to maintain and search the free
 * list), and fragmentation is avoided.
 */
public class CopyCollectHeap extends Heap {
	private static final int SIZE = -1;
	private static final int FORWARD = -2;

	private int toSpace;
	private int fromSpace;
	private int allocPtr;

	/**
	 * Though the super constructor is invoked and the free list is initialized, the
	 * free list is not used in the implementation of this copy collector.
	 */
	public CopyCollectHeap(int size) {
		super(size);
		toSpace = 0;
		fromSpace = size / 2;
		allocPtr = toSpace;
	}

	public void allocate(Var v, int size) throws InsufficientMemory {
		// TODO
		int limits = toSpace + fromSpace;
		// v.addr = -1;
		if (allocPtr - toSpace + size + 2 > limits) {
			collect();
		}

		if (allocPtr - toSpace + size + 2 <= limits) {
			v.addr = allocPtr + 2;
			allocPtr += size + 2;
			Arrays.fill(data, v.addr, v.addr + size, -1);
			data[v.addr + SIZE] = size;
			data[v.addr + FORWARD] = -1;
		} else {
			throw new InsufficientMemory();
		}
	}

	private void collect() {
		// TODO
		allocPtr = fromSpace;
		fromSpace = toSpace;
		toSpace = allocPtr;

		for (Iterator<Var> it = reachable.iterator(); it.hasNext();) {
			Var v = it.next();
			if (!v.isNull() && data[v.addr + FORWARD] == -1) {
				copy(v.addr);
				v.addr = data[v.addr + FORWARD];
			}
		}

		int size;
		int addr = toSpace + 2;
		while (addr < allocPtr) {
			size = data[addr + SIZE];
			for (int i = addr; i < addr + size; ++i) {
				if (data[i] != -1)
					data[i] = data[data[i] + FORWARD];
			}
			addr += size + 2;
		}

		Arrays.fill(data, fromSpace, fromSpace * 2 + toSpace, -1);
	}

	private int copy(int addr) {
		// TODO
		int size = data[addr + SIZE];
		int newaddr = allocPtr + 2;
		allocPtr += size + 2;
		data[addr + FORWARD] = newaddr;
		data[newaddr + FORWARD] = -1;
		data[newaddr + SIZE] = data[addr + SIZE];
		for (int i = 0; i < size; ++i) {
			data[newaddr + i] = data[addr + i];
			if (data[addr + i] != -1 && data[data[addr + i] + FORWARD] == -1)
				copy(data[addr + i]);
		}
		return 0;
	}
}
