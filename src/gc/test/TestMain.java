package gc.test;

import java.lang.reflect.Constructor;

import gc.CopyCollectHeap;
import gc.Heap;
import gc.InsufficientMemory;
import gc.MarkSweepHeap;
import gc.RefCountHeap;

public class TestMain {
	private void run(TestCase test, Class<? extends Heap> heapClass, int size) {
		String testName = test.getClass().getSimpleName();
		String heapName = heapClass.getSimpleName();
		String caseName = testName + "@" + heapName;

		try {
			Constructor<? extends Heap> C = heapClass.getConstructor(int.class);
			Heap heapOfSize = C.newInstance(size);

			long startTime = System.currentTimeMillis();
			test.run(heapOfSize);
			long elapsed = System.currentTimeMillis() - startTime;

			try {
				Heap heapOfSizeMinusOne = C.newInstance(size - 1);
				test.run(heapOfSizeMinusOne);
				System.out.println("? " + caseName);
			} catch (InsufficientMemory e) {
				System.out.println("✔ " + caseName + " (" + elapsed + "ms)");
			}
		} catch (Exception e) {
			System.out.println("✘ " + caseName);
		}
	}

	public void run() {
		run(new TestNode(100), Heap.class, 300);
		run(new TestNode(100), RefCountHeap.class, 204);
		run(new TestNode(100), MarkSweepHeap.class, 204);
		run(new TestNode(100), CopyCollectHeap.class, 408);

		run(new TestChain(10, 1000), Heap.class, 10000000);
		run(new TestChain(10, 1000), RefCountHeap.class, 12000);
		run(new TestChain(10, 1000), MarkSweepHeap.class, 12000);
		run(new TestChain(10, 1000), CopyCollectHeap.class, 24000);

		run(new TestCycle(50), Heap.class, 100000);
		run(new TestCycle(50), MarkSweepHeap.class, 104);
		run(new TestCycle(50), CopyCollectHeap.class, 208);

		run(new TestLoop(10, 1000), Heap.class, 10000000);
		run(new TestLoop(10, 1000), MarkSweepHeap.class, 12000);
		run(new TestLoop(10, 1000), CopyCollectHeap.class, 24000);

		run(new TestFragmentation(), Heap.class, 5993);
		run(new TestFragmentation(), RefCountHeap.class, 4009);
		run(new TestFragmentation(), MarkSweepHeap.class, 6009);
		run(new TestFragmentation(), CopyCollectHeap.class, 6018);
	}

	public static void main(String[] args) {
		TestMain test = new TestMain();
		test.run();
	}
}
