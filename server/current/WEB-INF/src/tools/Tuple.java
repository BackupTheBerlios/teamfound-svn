/*
 * Created on Mar 27, 2006
 */
package tools;

public class Tuple<el1, el2>{
	protected el1 a;
	protected el2 b;
	
	public Tuple(el1 a, el2 b) {
		this.a = a;
		this.b = b;
	}
	
	public el1 getFirst() {
		return a;
	}
	
	public el2 getSecond() {
		return b;
	}
}
