package gc.test;

import gc.InsufficientMemory;
import gc.Heap;
import gc.Var;

public class TestCycle implements TestCase {
	private int nodeSize;

	public TestCycle(int nodeSize) {
		this.nodeSize = nodeSize;
	}

	/**
	 * for (int i = 0; i < 1000; i++) {
	 *     var x;
	 *     var y;
	 *     x = new Node();
	 *     y = new Node();
	 *     x.next = y;
	 *     y.next = x;
	 * }
	 */
	public void run(Heap heap) throws InsufficientMemory {
		for (int i = 0; i < 1000; i++) {
			heap.beginScope();
			Var x = heap.newVar();
			Var y = heap.newVar();
			heap.allocate(x, nodeSize);
			heap.allocate(y, nodeSize);
			heap.writeField(x, 0, y);
			heap.writeField(y, 0, x);
			heap.endScope();
		}
	}
}
