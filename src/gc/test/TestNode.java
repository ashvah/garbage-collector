package gc.test;

import gc.InsufficientMemory;
import gc.Heap;
import gc.Var;

public class TestNode implements TestCase {
	private int nodeSize;

	public TestNode(int nodeSize) {
		this.nodeSize = nodeSize;
	}

	/**
	 * var p;
	 * var q;
	 * p = new Node();
	 * q = new Node();
	 * q = p;
	 * p = new Node();
	 */
	public void run(Heap heap) throws InsufficientMemory {
		heap.beginScope();
		Var p = heap.newVar();
		Var q = heap.newVar();
		heap.allocate(p, nodeSize);
		heap.allocate(q, nodeSize);
		heap.assign(q, p);
		heap.allocate(p, nodeSize);
		heap.endScope();
	}
}
