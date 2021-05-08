package gc.test;

import gc.Heap;
import gc.InsufficientMemory;
import gc.Var;

public class TestFragmentation implements TestCase {

	public void run(Heap heap) throws InsufficientMemory {
		heap.beginScope();

		Var p = heap.newVar();

		for (int i = 0; i < 3; i++) {
			heap.beginScope();
			Var temp = heap.newVar();
			heap.allocate(temp, 998);
			Var node = heap.newVar();
			heap.allocate(node, 1);
			heap.writeField(node, 0, p);
			heap.assign(p, node);
			heap.endScope();
		}

		for (int i = 0; i < 2; i++) {
			heap.beginScope();
			Var node = heap.newVar();
			heap.allocate(node, 1498);
			heap.writeField(node, 0, p);
			heap.assign(p, node);
			heap.endScope();
		}

		heap.endScope();
	}
}
