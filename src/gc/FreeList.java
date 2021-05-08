package gc;

/**
 * For simplicity, the free list is maintained in the JVM memory instead of in
 * our own implementation of the heap.
 */
final class FreeList {
	private int addr;
	private int size;
	private FreeList next;

	public FreeList(int addr, int size) {
		this(addr, size, null);
	}

	private FreeList(int addr, int size, FreeList next) {
		this.addr = addr;
		this.size = size;
		this.next = next;
	}

	public int acquire(int requiredSize) throws InsufficientMemory {
		if (size < requiredSize) {
			if (next == null)
				throw new InsufficientMemory();
			return next.acquire(requiredSize);
		}
		if (size == requiredSize) {
			int retAddr = addr;
			if (next == null) {
				size = 0;
			} else {
				addr = next.addr;
				size = next.size;
				next = next.next;
			}
			return retAddr;
		}
		assert size > requiredSize;
		int retAddr = addr;
		addr += requiredSize;
		size -= requiredSize;
		return retAddr;
	}

	public void release(int addr, int size) {
		insertToFront(addr, size);
		sortByAddr();
		mergeConsecutives();
	}

	private void insertToFront(int addr, int size) {
		next = new FreeList(this.addr, this.size, next);
		this.addr = addr;
		this.size = size;
	}

	private void sortByAddr() {
		if (next != null && addr > next.addr) {
			int tAddr = addr;
			addr = next.addr;
			next.addr = tAddr;
			int tSize = size;
			size = next.size;
			next.size = tSize;
			next.sortByAddr();
		}
	}

	private void mergeConsecutives() {
		if (next != null && addr + size == next.addr) {
			size += next.size;
			next = next.next;
		}
	}
}
