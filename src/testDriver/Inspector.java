package testDriver;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.lang.System;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Class.*;

public class Inspector {
	
	//switch between output to console or file
	private boolean fileOutputOn = true;
	//recursive in this program works in such that whenever something is 
	//in a position to be added to the list of things to do yet, it checks the flag
	//and if the flag is not set, it will not add that object to the list
	private boolean rec = false;
	
	//Pair of dynamic lists to control what classes we've seen, and haven't seen
	//ReflectedClassHashes contains each parallel indexed reflectedClassList object's hashCode to weed out non-unique class objects
	//ReflectedClassList contains the found objects, as well as a boolean paired to each that will tell if its been read yet or not
	private ArrayList<Integer> reflectedClassHashes = new ArrayList<Integer>();
	private ArrayList<ClassHashTuple> reflectedClassList = new ArrayList<ClassHashTuple>();
	
	//default Constructor
	public Inspector() {
	}
	
	/*
	 * InspectRecursive-
	 * Takes in an index, to be used in the above ArrayLists to get an object to reflect upon
	 */
	
	public void inspectRecursive(int index) {
		
		//GET THE OBJECT IN THE TUPLELIST
		Object recursedObject = reflectedClassList.get(index).getTupleObject();
		
		//GET THE NAME OF THE OBJECT, IF IT ISNT AN ARRAY
		if(isArray(recursedObject)) {			
			System.out.println("The Object is an Array: ");
			classArrayDetails(recursedObject);
			System.out.println("Its component type is: "+recursedObject.getClass().getComponentType().getName());
			
		}else {
			System.out.println("The Object name is: "+recursedObject.getClass().getSimpleName());
		}
		
		//GET THE INTERFACES THE OBJECT IMPLEMENTS
		printImplementedInterfaces(recursedObject);
		
		//GET THE NAME OF THE SUPERCLASS AND ADD TO THE LIST IF RECURSE ACTIVE
		//If it is the top class, it will not attempt to do so. 
		if(rec) {
			System.out.println("RECURSION PLACEHOLDER SUPERCLASS.");
			if(!recursedObject.getClass().equals(Object.class)) {

				System.out.println("This class' direct superClass is: "+ recursedObject.getClass().getSuperclass().getName() +" \n");
				addToTupleList(recursedObject.getClass().getSuperclass());
			}else {
				System.out.println("This class is top of the Ladder. \n");
			}
		} else {
			System.out.println("This class' direct SuperClass is: "+ recursedObject.getClass().getSuperclass().getName() + " \n");
		}
		
		//GET AND PRINT THE CONSTRUCTORS FOR THE OBJECT	
		printConstructors(recursedObject);
		
		
		//GET AND PRINT THE OBJECT METHODS AND THEIR SPECIFIC DETAILS
		printObjectMethods(recursedObject);
		
		
		//GET AND PRINT THE OBJECTS FIELDS
		printObjectFields(recursedObject,index);
		
	}
	
	
	/*
	 * Inspect-
	 * Takes in the object to be reflected on, and whether to do so recursively
	 */
	public void inspect(Object obj, boolean recursive) {
		
		
		//Make small change if run to output to file
		if(fileOutputOn) {
			try {
				FileOutputStream f = new FileOutputStream("file.txt");
			    System.setOut(new PrintStream(f));
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//save necessary values to private version
		rec = recursive;
		
		//Even if recursive isn't set, add the first object to the list
		addToTupleList(obj);
		//And then get the index of that file (should always just be zero)
		int indexOfClassToRecurseOn = getUnreadTupleFromList();

		//Recurse on that object. If recurse is set to true, the list will become more populated, and the while loop
		//will continue, until the function returns the value that flags that all the elements have been read
		System.out.println("Starting recursion on the object: "+reflectedClassList.get(indexOfClassToRecurseOn).getTupleObject()+" \n With Hash Value: "+reflectedClassHashes.get(indexOfClassToRecurseOn));
		while(indexOfClassToRecurseOn != -1) {
			inspectRecursive(indexOfClassToRecurseOn);
			indexOfClassToRecurseOn = getUnreadTupleFromList();
			if(indexOfClassToRecurseOn!= -1) {
				System.out.println("\n \n \n ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				System.out.println("Starting recursion on the object: "+reflectedClassList.get(indexOfClassToRecurseOn).getTupleObject()+" \n With Hash Value: "+reflectedClassHashes.get(indexOfClassToRecurseOn));
			}
		}
		System.out.println("Nothing else to Recurse on. We are done here. \n");
		
	}
	
	
	//simple function that takes in a field of the type array, and returns its size/dimensions
	private int arrayDimensionToString(Field field) {
		Class fieldArrayClass = field.getType();
		int depth =0;
		while(fieldArrayClass.isArray()) {
			depth++;
			fieldArrayClass = fieldArrayClass.getComponentType();
		}
		return depth;
	}
	
	//simple function to do similar to the above, but it only prints to .out. the values found, and adds
	//the object to the list
	public void classArrayDetails(Object array) {
		Class arrayClass = array.getClass();
		int depth = 0;
		while(arrayClass.isArray()) {
			depth++;
			arrayClass = arrayClass.getComponentType();
		}
		addToTupleList(arrayClass);
		System.out.println("The array is of the dimension: "+depth+" and its name is "+array.toString());
	}
	
	
	//
	//This function will create a new Tuple of an object
	//and checks to see if the object's  hashcode has already been entered into the list
	//if not, it adds it, and its hashcode to respective lists
	private void addToTupleList(Object obj) {
		ClassHashTuple newTuple = new ClassHashTuple(obj);
		if(!reflectedClassHashes.contains(obj.hashCode())) {
			reflectedClassHashes.add(obj.hashCode());
			reflectedClassList.add(newTuple);
		}
	}
	
	//
	//Iterate through the reflectedClassList, stopping and returning the index at 
	//which an element can be found that has not yet been read, and flipping its read value
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
	
	//
	//Function iterates through a list of Fields and sets them to accessible, in 
	//case they are private
	private Field[] fieldAccessibility(Field[] fieldArray) {
		for (int i=0; i<fieldArray.length;i++) {
			fieldArray[i].setAccessible(true);
		}
		return fieldArray;
	}
	
	//
	//print the details about an Objects fields
	private void printObjectFields(Object obj,int index) {
		Field[] objectFields = obj.getClass().getDeclaredFields();
		if(objectFields.length == 0) {
			System.out.println("The object has no declared fields \n");
		}else {
			objectFields = fieldAccessibility(objectFields);
			
			System.out.println("The object has the declared fields: \n"+objectFieldDetailsToString(objectFields,index));
		}
	}

	//
	//delegated function from printObjectsFields. Takes in a fieldArray of an object, and creates a string
	//about its Modifiers, type... ect.
	//delegates the Field value to another function
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

	//
	//delegated function from objectFieldDetailsToString. Takes in a single field and returns a string of its name, its type, and
	//if its easily done, its value. Else if it is an object, or an array, it handles those differently depending on whether recursion 
	//is set
	private String fieldValueToString(Field field, int index) {
		String fieldValue = "";
		if(!field.getType().isPrimitive()) {
			fieldValue += ("getName: "+field.getName()+ " \n");
			fieldValue += ("Field Type: "+field.getType()+ " \n");
			
				if(rec) {
					if(field.getType().isArray()) {
						addToTupleList(field.getType());
						fieldValue += ("the field is an array with dimension: "+arrayDimensionToString(field)+" \n");
								
						fieldValue += ("the hashcode is: "+field.getType().hashCode()+" \n");
					}else {
						addToTupleList(field);
						fieldValue += ("the hashcode is: "+field.getType().hashCode()+" \n");
					}
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


	//
	//Function which will print out the list of methods of an object
	//delegates the method details to objectMethodsToString
	private void printObjectMethods(Object obj) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		if(methods.length == 0) {
			System.out.println("The object does not have any declared Methods.");
		} else {
			System.out.println("The object has the methods: \n"+objectMethodsToString(methods));
		}
	}

	//
	//Function delegated from printObjectMethods
	//Will take in a list of methods, and iterate through them
	//returning relevant details as a string
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

	//
	//Delegated from objectMethodsToString
	//deals with a single methods details, returning them as a whitespace oriented block
	//
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

					addToTupleList(exceptionTypes[i]);
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


	//
	//Function to iterate through the list of implemented interfaces and prints
	//relevant details
	//if recursion is set, attempting to add them to the list
	//
	private void printImplementedInterfaces(Object obj) {
		Class[] implemented = obj.getClass().getInterfaces();
		if (implemented.length ==0) {
			System.out.println("The class does not implement anything.");
		} else {
			String interfacesSoFar = "";
			for (int i = 0; i<implemented.length;i++) {
				if(rec) {
					addToTupleList(implemented[i]);
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
	
	
	//
	//Made function since the standard library .isArray() does not work in every instance
	//
	public boolean isArray(Object obj){
		boolean itIsArray = false;
		if(obj.getClass().isArray()){
			itIsArray = true;
		}
		return itIsArray;
	}

		
}
	
