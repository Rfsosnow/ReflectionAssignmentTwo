package testDriver;

public class ClassHashTuple {
	
	private Object givenObject;
	private boolean readYet;
	
	public ClassHashTuple(){
		readYet = false;
	}
	
	public ClassHashTuple(Object obj) {
		readYet = false;
		givenObject = obj;
	}
	
	public boolean classReadYet() {
		return readYet;
	}
	
	public Object getTupleObject() {
		return givenObject;
	}
		
	public void setReadYet(boolean readStatus) {
		readYet = readStatus;
	}
	
	public void setTupleObject(Object obj) {
		givenObject = obj;
	}
}
