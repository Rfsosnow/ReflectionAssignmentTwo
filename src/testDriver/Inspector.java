package testDriver;
import java.lang.reflect.*;
import java.lang.Class.*;

public class Inspector {
	
	boolean rec = false;
	public Object globalObject;
	public Inspector() {
	}
	
	public void inspect(Object obj, boolean recursive) {
		rec = recursive;
		globalObject = obj;
		
		//Section for declaring class
		
		//branching if object is an array
		if(isArray(obj)) {
			System.out.println("ARRAY HANDLING PLACEHOLDER");
			//do something else here
		}else {
			System.out.println("The Object name is: "+obj.getClass().getSimpleName());
		}
		
		
		//Section for interfaces implemented
			
//		printImplementedInterfaces(obj);
		
		//Section for class' direct superclass

		
		if(rec && !obj.getClass().getSuperclass().equals(Object.class)) {
			System.out.println("RECURSION PLACEHOLDER SUPERCLASS.");
			//add the superclass to the list of recursion objects when done
		}
		
		System.out.println("This class' direct superClass is: "+ obj.getClass().getSuperclass().getName() +" \n");
		
		
		
		//Section for delving into the methods of the object
//		printObjectMethods(obj);

		//Section for printing out the constructors involved in the object		
//		printConstructors(obj);
		
		
		//Section for printing out the fields of the object
		
		printObjectFields(obj);
		
		
	}
	
	private Field[] fieldAccessibility(Field[] fieldArray) {
		for (int i=0; i<fieldArray.length;i++) {
			fieldArray[i].setAccessible(true);
		}
		return fieldArray;
	}
	
	private void printObjectFields(Object obj) {
		Field[] objectFields = obj.getClass().getDeclaredFields();
		if(objectFields.length == 0) {
			System.out.println("The object has no declared fields");
		}else {
			objectFields = fieldAccessibility(objectFields);
			System.out.println("The object has the declared fields: \n"+objectFieldDetailsToString(objectFields));
		}
	}

	private String objectFieldDetailsToString(Field[] objectFields) {
		String fieldDetailString="";
		
		for(int i = 0; i<objectFields.length;i++) {
			fieldDetailString += ("    Name: "+objectFields[i].getName()+" \n");
			fieldDetailString += ("    The field has the modifier: "+Modifier.toString(objectFields[i].getClass().getModifiers())+" \n");
			fieldDetailString += fieldValueToString(objectFields[i]);
		}
		
		fieldDetailString += " \n";
		return fieldDetailString;
	}

	private String fieldValueToString(Field field) {
		String fieldValue = "";
		if(!field.getType().isPrimitive()) {
			fieldValue += ("getName: "+field.getName()+ " \n");
			fieldValue += ("just field: "+field.getClass()+ " \n");
			fieldValue += ("the hashcode is: "+field.getClass().hashCode()+" \n");
			fieldValue += "\n The value is not a primitive.";
				if(rec) {
					//add the object in field to list to recurse on if it doesn't exist in it already
				}
		}else {
			Object val;
			try {
				val = field.get(globalObject);
				fieldValue += ("The field type is: "+field.getType().getSimpleName()+" \n");
				fieldValue += ("The field value is: "+val+" \n");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		fieldValue += "\n";
		return fieldValue;
	}

	private void printObjectMethods(Object obj) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		if(methods.length == 0) {
			System.out.println("        The object does not have any declared Methods.");
		} else {
			System.out.println("        The object has the methods: \n"+objectMethodsToString(methods));
		}
	}

	private String objectMethodsToString(Method[] methods) {
		String methodReturns="";
		
		for (int i=0; i<methods.length;i++) {
			methodReturns += ("    "+methods[i].getName());
			methodReturns += " \n";
			methodReturns += methodDetailsToString(methods[i]);
			methodReturns += " \n";
		}
		
		return methodReturns;
	}

	private String methodDetailsToString(Method method) {
		String detailReturns = "";
		
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0) {
			detailReturns += "The method has no parameters. \n";
		}else {
			detailReturns += "The method has the following parameters: \n";
			for (int i = 0; i<parameterTypes.length; i++) {
				detailReturns += ("    "+parameterTypes[i].getName());
				if((i+1)<parameterTypes.length) {
					detailReturns += ", ";
				}
			}
			detailReturns += " \n";
		}
		
		detailReturns += ("The method has the return Type: "+method.getReturnType().getName()+"\n");
		
		Class[] exceptionTypes = method.getExceptionTypes();
		if (exceptionTypes.length ==0) {
			detailReturns += "The method does not have any Exceptions thrown \n";
		}else {
			detailReturns += "The method has the following parameters: \n";
			for (int i = 0;i<exceptionTypes.length;i++) {
				detailReturns += ("    "+exceptionTypes[i].getName());
				if((i+1)<exceptionTypes.length) {
					detailReturns += ", ";
				}
			}
			detailReturns += " \n";
		}
		
		detailReturns += ("The method has the following modifiers: "+Modifier.toString(method.getModifiers())+" \n");
		
		return detailReturns;
	}


	
	private void printImplementedInterfaces(Object obj) {
		Class[] implemented = obj.getClass().getInterfaces();
		if (implemented.length ==0) {
			System.out.println("The class does not implement anything.");
		} else {
			String interfacesSoFar = "";
			for (int i = 0; i<implemented.length;i++) {
				interfacesSoFar+=implemented[i].getName();
				if((i+1)<implemented.length) {
					interfacesSoFar+=", ";
				} else {
					interfacesSoFar+="\n";
				}
			}
			System.out.println("The class implements these interfaces: " + interfacesSoFar);
		}
	}
	
	/*
	 * Function which encapsulates the process of piecing through an objects Constructors, printing to the system 
	 * in a compact format
	 *
	 */
	
	public void printConstructors(Object obj){
		Constructor[] objectConstructors = obj.getClass().getDeclaredConstructors();
		
		for (Constructor c:objectConstructors){
			System.out.println("The object has the constructor: "+constructorModToString(c)+" "+c.getName()+"("+constructorParamToString(c)+")");
		}
	}
	
	/*
	 * Function which will take in a constructor, returning the Parameters in a sectioned list separated by 
	 * commas. 
	 */
	private String constructorParamToString(Constructor c){
		String returnString = "";
		Class[] parameters = c.getParameterTypes();
		for (int i = 0;i<parameters.length;i++){
			returnString+=(parameters[i].getName());
			if((i+1)<parameters.length){
				returnString+=", ";
			}
		}
		return returnString;
	}
	
	/*
	 * Function which will take in a constructor, and return its Modifiers as a String
	 */
	private String constructorModToString(Constructor c){
		int modifier = c.getModifiers();
		return Modifier.toString(modifier);
	}
	
	
	
	
	public boolean isArray(Object obj){
		boolean itIsArray = false;
		if(obj.getClass().isArray()){
			itIsArray = true;
		}
		return itIsArray;
	}

		
}
	
