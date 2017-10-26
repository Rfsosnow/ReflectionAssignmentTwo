package testDriver;
import java.lang.reflect.*;
import java.lang.Class.*;

public class Inspector {
	
	boolean rec = false;
	
	public Inspector() {
	}
	
	public void inspect(Object obj, boolean recursive) {
		rec = recursive;
		
		
		//Section for declaring class
		String name;
		
		//add a branch for if the object is an array
		name = obj.getClass().getSimpleName();
		System.out.println("The Simple Object name is: "+name);
		
		Class[] implmnts = obj.getClass().getInterfaces();
		
		//Section for interfaces implemented
		//add a case where there may be no interfaces implemented
		System.out.println("This class implements the following interface(s):");
		int counter = 0;
		for (Class i :implmnts) {
			counter++;
			System.out.println("Interface "+(counter)+": "+i.getName());
		}
		
		//Section for class' direct superclass
		
		Class overClass = obj.getClass().getSuperclass();
		System.out.println("This class' direct superClass is: "+ overClass.getName());
		
		//Section for delving into the methods of the object
		
		Method[] methods = obj.getClass().getMethods();
		counter = 0;
		for (Method mthd :methods) {
			counter++;
			System.out.println("\n The "+counter+"th method is:"+mthd.getName());
			System.out.println("\n Its parameter types are:");
			Class[] parameterTypes = mthd.getParameterTypes();
			for (Class paramType :parameterTypes) {
				System.out.println(paramType.getName()+"   ||");
			}
			Class returnType = mthd.getReturnType();
			System.out.println("The return type is "+ returnType.getName());
		}
		//Class[] exceptionTypes = mthd.getExceptionTypes();
		//for (Class excptnType: exceptionTypes)
		
	}
	
}
