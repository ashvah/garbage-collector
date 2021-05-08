package gc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Heap {
	private static final class Scope extends ArrayList<Var> {
		private static final long serialVersionUID = 1L;
	}

	private Stack<Scope> scopes = new Stack<Scope>();
	protected Set<Var> reachable = new HashSet<Var>();
	protected FreeList freelist;
	protected int[] data;

	public Heap(int size) {
		freelist = new FreeList(0, size);
		data = new int[size];
	}

	protected Scope currentScope() {
		return scopes.peek();
	}

	public void beginScope() {
		scopes.push(new Scope());
	}

	public void endScope() {
		reachable.removeAll(currentScope());
		scopes.pop();
	}

	public Var newVar() {
		Var v = new Var();
		currentScope().add(v);
		reachable.add(v);
		return v;
	}

	public void allocate(Var v, int size) throws InsufficientMemory {
		v.addr = freelist.acquire(size);
		Arrays.fill(data, v.addr, v.addr + size, -1);
	}

	public void assign(Var v1, Var v2) {
		v1.addr = v2.addr;
	}

	public void readField(Var v1, Var v2, int fieldOffset) {
		v1.addr = data[v2.addr + fieldOffset];
	}

	public void writeField(Var v1, int fieldOffset, Var v2) {
		data[v1.addr + fieldOffset] = v2.addr;
	}

	public boolean isNull(Var v) {
		return v.isNull();
	}

	public void setNull(Var v) {
		v.setNull();
	}
}
