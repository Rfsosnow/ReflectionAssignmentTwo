package testDriver;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.lang.System;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Class.*;

public class Inspector {
	
	private boolean rec = false;
	public Object globalObject;
	
	private ArrayList<Integer> reflectedClassHashes = new ArrayList<Integer>();
	private ArrayList<ClassHashTuple> reflectedClassList = new ArrayList<ClassHashTuple>();
	
	public Inspector() {
	}
	
	public void inspectRecursive(int index) {
		
		System.out.println("The amount of objects in the list BEGINNING RECURSION: "+reflectedClassList.size()+" \n");
		
		//GET THE OBJECT IN THE TUPLELIST
		Object recursedObject = reflectedClassList.get(index).getTupleObject();
		
		//GET THE NAME OF THE OBJECT, IF IT ISNT AN ARRAY
		if(isArray(recursedObject)) {
			System.out.println("ARRAY HANDLING PLACEHOLDER");
			//do something else here
		}else {
			System.out.println("The Object name is: "+recursedObject.getClass().getSimpleName());
		}
		
		//GET THE INTERFACES THE OBJECT IMPLEMENTS
		//May have to recurse here
		printImplementedInterfaces(recursedObject);
		
		//GET THE NAME OF THE SUPERCLASS AND ADD TO THE LIST IF RECURSE ACTIVE
		//recursion case handled here
		if(rec) {
			System.out.println("RECURSION PLACEHOLDER SUPERCLASS.");
			if(!recursedObject.getClass().equals(Object.class)) {
				System.out.println("The amount of objects in the list: BEFORE INSIDE SUPERCLASS: "+reflectedClassList.size()+" \n");

				System.out.println("This class' direct superClass is: "+ recursedObject.getClass().getSuperclass().getName() +" \n");
				//addToTupleList(recursedObject.getClass().getSuperclass());
			}else {
				System.out.println("This class is top of the Ladder. \n");
			}
		} else {
			System.out.println("This class' direct SuperClass is: "+ recursedObject.getClass().getSuperclass().getName() + " \n");
		}
		System.out.println("The amount of objects in the list: AFTER SUPERCLASS: "+reflectedClassList.size()+" \n");
		
		//GET AND PRINT THE CONSTRUCTORS FOR THE OBJECT	
		printConstructors(recursedObject);
		
		System.out.println("The amount of objects in the list: AFTER CONSTRUCTORS "+reflectedClassList.size()+" \n");
		
		//GET AND PRINT THE OBJECT METHODS AND THEIR SPECIFIC DETAILS
		printObjectMethods(recursedObject);
		
		System.out.println("The amount of objects in the list: AFTER METHODS "+reflectedClassList.size()+" \n");
		
		//GET AND PRINT THE OBJECTS FIELDS
		printObjectFields(recursedObject,index);
		
		System.out.println("The amount of objects in the list: AFTER FIELDS "+reflectedClassList.size()+" \n");
	}
	
	public void inspect(Object obj, boolean recursive) {
		
		
		try {
			FileOutputStream f = new FileOutputStream("file.txt");
		    System.setOut(new PrintStream(f));
		}catch (IOException e) {
			e.printStackTrace();
		}
		rec = recursive;
		globalObject = obj;		
		
		addToTupleList(obj);
		
		int indexOfClassToRecurseOn = getUnreadTupleFromList();
		/*		
		if(indexOfClassToRecurseOn == -1) {
			//No more objects to recurse on
			System.out.println("All done here.");
		} else {
			inspectRecursive(indexOfClassToRecurseOn);
		}
		*/
		System.out.println("Starting recursion on the object: "+reflectedClassList.get(indexOfClassToRecurseOn).getTupleObject()+" \n With Hash Value: "+reflectedClassHashes.get(indexOfClassToRecurseOn));
		while(indexOfClassToRecurseOn != -1) {
			inspectRecursive(indexOfClassToRecurseOn);
			indexOfClassToRecurseOn = getUnreadTupleFromList();
			if(indexOfClassToRecurseOn!= -1) {
				System.out.println("\n \n \n ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				System.out.println("Starting recursion on the object: "+reflectedClassList.get(indexOfClassToRecurseOn).getTupleObject()+" \n With Hash Value: "+reflectedClassHashes.get(indexOfClassToRecurseOn));
			}
		}
		
	}
	
	
	private void addToTupleList(Object obj) {
		ClassHashTuple newTuple = new ClassHashTuple(obj);
		if(!reflectedClassHashes.contains(obj.hashCode())) {
			reflectedClassHashes.add(obj.hashCode());
			reflectedClassList.add(newTuple);
		}
	}
	
	private int getUnreadTupleFromList() {
		for (int i =0; i< reflectedClassList.size(); i++) {
			ClassHashTuple prospect = reflectedClassList.get(i);
			if (!prospect.classReadYet()) {
				prospect.setReadYet(true);
				return i;
			}
		}
		return -1; 
	}
	
	private Field[] fieldAccessibility(Field[] fieldArray) {
		for (int i=0; i<fieldArray.length;i++) {
			fieldArray[i].setAccessible(true);
		}
		return fieldArray;
	}
	
	private void printObjectFields(Object obj,int index) {
		Field[] objectFields = obj.getClass().getDeclaredFields();
		if(objectFields.length == 0) {
			System.out.println("The object has no declared fields \n");
		}else {
			objectFields = fieldAccessibility(objectFields);
			System.out.println("The object has the declared fields: \n"+objectFieldDetailsToString(objectFields,index));
		}
	}

	private String objectFieldDetailsToString(Field[] objectFields, int index) {
		String fieldDetailString="";
		
		for(int i = 0; i<objectFields.length;i++) {
			fieldDetailString += ("    Name: "+objectFields[i].getName()+" \n");
			fieldDetailString += ("    The field has the modifier: "+Modifier.toString(objectFields[i].getClass().getModifiers())+" \n");
			fieldDetailString += fieldValueToString(objectFields[i],index);
		}
		
		fieldDetailString += " \n";
		return fieldDetailString;
	}

	private String fieldValueToString(Field field, int index) {
		String fieldValue = "";
		if(!field.getType().isPrimitive()) {
			fieldValue += ("getName: "+field.getName()+ " \n");
			fieldValue += ("Field Type: "+field.getType()+ " \n");
			fieldValue += ("the hashcode is: "+field.getClass().hashCode()+" \n");
			fieldValue += "\n The value is not a primitive.";
				if(rec) {
					//addToTupleList(field);
					//add the object in field to list to recurse on if it doesn't exist in it already
				}
		}else {
			Object val;
			try {
				val = field.get(reflectedClassList.get(index).getTupleObject());
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
			System.out.println("The object does not have any declared Methods.");
		} else {
			System.out.println("The object has the methods: \n"+objectMethodsToString(methods));
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
			detailReturns += "        The method has no parameters. \n";
		}else {
			detailReturns += "        The method has the following parameters: \n";
			for (int i = 0; i<parameterTypes.length; i++) {
				detailReturns += ("            "+parameterTypes[i].getName());
				if((i+1)<parameterTypes.length) {
					detailReturns += ", ";
				}
			}
			detailReturns += " \n";
		}
		
		detailReturns += ("        The method has the return Type: "+method.getReturnType().getName()+"\n");
		
		Class[] exceptionTypes = method.getExceptionTypes();
		if (exceptionTypes.length ==0) {
			detailReturns += "        The method does not have any Exceptions thrown \n";
		}else {
			detailReturns += "        The method throws the following Exceptions: \n";
			for (int i = 0;i<exceptionTypes.length;i++) {
				if(rec) {
					//System.out.println("The amount of objects in the list: INSIDE EXCEPTIONS"+reflectedClassList.size()+" \n");

					//addToTupleList(exceptionTypes[i]);
				}
				detailReturns += ("            "+exceptionTypes[i].getName());
				if((i+1)<exceptionTypes.length) {
					detailReturns += ", ";
				}
			}
			detailReturns += " \n";
		}
		
		detailReturns += ("        The method has the following modifiers: "+Modifier.toString(method.getModifiers())+" \n");
		
		return detailReturns;
	}


	
	private void printImplementedInterfaces(Object obj) {
		Class[] implemented = obj.getClass().getInterfaces();
		if (implemented.length ==0) {
			System.out.println("The class does not implement anything.");
		} else {
			String interfacesSoFar = "";
			for (int i = 0; i<implemented.length;i++) {
				if(rec) {
					//addToTupleList(implemented[i]);
				}
				interfacesSoFar+=("    "+implemented[i].getName());
				if((i+1)<implemented.length) {
					interfacesSoFar+=",\n";
				} else {
					interfacesSoFar+="\n";
				}
			}
			System.out.println("The class implements these interfaces: \n" + interfacesSoFar);
		}
	}
	
	/*
	 * Function which encapsulates the process of piecing through an objects Constructors, printing to the system 
	 * in a compact format
	 *
	 */
	
	public void printConstructors(Object obj){
		Constructor[] objectConstructors = obj.getClass().getDeclaredConstructors();
		System.out.println("The Object constructors as as follows:");
		for (Constructor c:objectConstructors){
			System.out.println("    The object has the constructor: "+constructorModToString(c)+" "+c.getName()+"("+constructorParamToString(c)+")");
		}
		System.out.println("\n");
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
	
