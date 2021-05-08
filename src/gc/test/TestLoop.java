package gc.test;

import gc.Heap;
import gc.InsufficientMemory;
import gc.Var;

public class TestLoop implements TestCase {

	private int nodeSize, loopSize;

	public TestLoop(int nodeSize, int loopSize) {
		this.nodeSize = nodeSize;
		this.loopSize = loopSize;
	}

	public void run(Heap heap) throws InsufficientMemory {
		for (int i = 0; i < 1000; i++) {
			heap.beginScope();
			Var last = heap.newVar();
			Var ptr = heap.newVar();
			heap.allocate(last, nodeSize);
			heap.assign(ptr, last);
			for (int j = 1; j < loopSize; j++) {
				heap.beginScope();
				Var node = heap.newVar();
				heap.allocate(node, nodeSize);
				heap.writeField(node, 0, ptr);
				heap.assign(ptr, node);
				heap.endScope();
			}
			heap.writeField(last, 0, ptr);
			heap.endScope();
		}
	}
}
