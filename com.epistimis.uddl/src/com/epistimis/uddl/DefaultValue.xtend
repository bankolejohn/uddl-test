/**
 * 
 */
package com.epistimis.uddl

import java.text.MessageFormat
import java.util.List
import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.QualifiedName

import com.epistimis.uddl.uddl.PlatformLongDouble
import com.epistimis.uddl.uddl.LogicalEnumerated
import com.epistimis.uddl.uddl.PlatformDouble
import com.epistimis.uddl.uddl.PlatformLongLong
import com.epistimis.uddl.uddl.PlatformULongLong
import com.epistimis.uddl.uddl.PlatformChar
import com.epistimis.uddl.uddl.PlatformSequence
import com.epistimis.uddl.uddl.PlatformOctet
import com.epistimis.uddl.uddl.PlatformFloat
import com.epistimis.uddl.uddl.PlatformStruct
import com.epistimis.uddl.uddl.PlatformEnumeration
import com.epistimis.uddl.uddl.PlatformULong
import com.epistimis.uddl.uddl.PlatformBoundedString
import com.epistimis.uddl.uddl.PlatformCharArray
import com.epistimis.uddl.uddl.PlatformShort
import com.epistimis.uddl.uddl.PlatformUShort
import com.epistimis.uddl.uddl.PlatformString
import com.epistimis.uddl.uddl.PlatformLong
import com.epistimis.uddl.uddl.PlatformBoolean
import com.epistimis.uddl.uddl.PlatformArray
import com.epistimis.uddl.uddl.LogicalValueTypeUnit
import com.epistimis.uddl.uddl.LogicalEnumeratedBase
import com.epistimis.uddl.util.ModelFilters

/**
 * We can extract landmarks from the Measurement system associated with the measurement and use those for default values, if 
 * there are any. For enumerations, just take the enumeration name.
 * 
 * We can also look at the Measurement system for things that don't have landmarks. Is there a general solution we can use that will provide 
 * defaults where possible, but fall back to hardcoded values here if no others are available?  This could include a model containing just default values.
 */
class DefaultValue {
	@Inject UddlQNP qnp;
	@Inject ModelFilters modelFilters;

	def dispatch Boolean 		getDefaultValue( PlatformBoolean 	value, EObject ctx) 		{return Boolean.FALSE;}
	def dispatch Character 		getDefaultValue( PlatformChar 		value, EObject ctx) 		{return Character.valueOf('*');}
	def dispatch Character 		getDefaultValue( PlatformOctet 		value, EObject ctx) 		{return Character.valueOf('*');}
	def dispatch Float			getDefaultValue( PlatformFloat 		value, EObject ctx) 		{return Float.valueOf(0);}
	def dispatch Double    		getDefaultValue( PlatformDouble 	value, EObject ctx) 		{return Double.valueOf(0);}
	def dispatch Double 		getDefaultValue( PlatformLongDouble value, EObject ctx) 		{return Double.valueOf(0);}
	def dispatch Short	 		getDefaultValue( PlatformShort 		value, EObject ctx) 		{return Short.valueOf( 0 as short);}
	def dispatch Long	 		getDefaultValue( PlatformLong 		value, EObject ctx) 		{return Long.valueOf(0);}
	def dispatch Long	 		getDefaultValue( PlatformLongLong 	value, EObject ctx) 		{return Long.valueOf(0);}
	def dispatch Short	 		getDefaultValue( PlatformUShort 	value, EObject ctx) 		{return Short.valueOf( 0 as short);}
	def dispatch Long	 		getDefaultValue( PlatformULong 		value, EObject ctx) 		{return Long.valueOf(0);}
	def dispatch Long	 		getDefaultValue( PlatformULongLong 	value, EObject ctx) 		{return Long.valueOf(0);}
	def dispatch String 		getDefaultValue( PlatformString 	value, EObject ctx) 		{return String.valueOf("*");}
	def dispatch String 		getDefaultValue( PlatformBoundedString 	value, EObject ctx) 	{return String.valueOf("*");}
	def dispatch Character 		getDefaultValue( PlatformCharArray 	value, EObject ctx) 		{return Character.valueOf('*');}

	def dispatch Character 		getDefaultValue( PlatformSequence 	value, EObject ctx) 		{return Character.valueOf('*');}
	def dispatch Character 		getDefaultValue( PlatformArray 		value, EObject ctx) 		{return Character.valueOf('*');}

	def dispatch QualifiedName	getDefaultValue( PlatformEnumeration value, EObject ctx) 	
	{
		val LogicalEnumeratedBase leb = getEnumValue(value);
		return (leb  === null) ?  QualifiedName.EMPTY : qnp.minimalReferenceQN(leb,ctx);
	}

	/**
	 * Getting an enum value staring from a PlatformEnumeration requires backtracking to find the related LogicalEnumerated. 
	 * We just need a value, so pick the first one.
	 */
	def LogicalEnumeratedBase getEnumValue(PlatformEnumeration value)	 {
		/**
		 * Get the actual enumeration being used and pick a value from that.
		 */
		val List<LogicalValueTypeUnit> vtus = modelFilters.getValueTypeUnit( value);
		if (!vtus.isEmpty()) {
			try {
				val LogicalValueTypeUnit vtu = vtus.get(0);
				// Get the first value in the list
				val LogicalEnumerated le = vtu.getValueType() as LogicalEnumerated;
				return le.label.get(0);
			}
			catch (Exception excp) {
				return null;
			}
		}
		return null;
		
	}
	/**
	 * TODO: Implement this. It should collect default values for all StructMembers. This could be recursive.
	 * The returned object must have a toString() method that generates a string in the appropriate for the grammar.
	 * @param value
	 * @return
	 */
	def dispatch  Object	getDefaultValue( PlatformStruct value, EObject ctx) 	
	{
		/**
		 * Go through all the structure members and get default values for each one. This may be recursive.
		 */
		return "";
	}
	
	/**
	 * getDefaultValueAsString provides fine grained control of the format of othe returned string. The 'toString()' method 
	 * may not provide quoted strings or ...  
	 */
	static final String QUOTED_STRING_FMT = "\"{0}\"";
	static final String QUOTED_CHAR_FMT = "\'{0}\'";
	//static final String QUOTED_STRING_LIST_FMT = "[ \"{0}\" ]";
	static final String QUOTED_CHAR_LIST_FMT = "[ \'{0}\' ]";
	static final String EXPLAINED_VALUE_FMT = "{0} /* {1} */"; // In case we want to explain where the default came from (e.g. Landmark name, NAICS code description
	
	def dispatch String getDefaultValueAsString( PlatformBoolean 	value, EObject ctx) 		{return Boolean.FALSE.toString();}
	def dispatch String getDefaultValueAsString( PlatformChar 		value, EObject ctx) 		{return MessageFormat.format(QUOTED_CHAR_FMT,"*");}
	def dispatch String getDefaultValueAsString( PlatformOctet 		value, EObject ctx) 		{return MessageFormat.format(QUOTED_CHAR_FMT,"*");}
	def dispatch String	getDefaultValueAsString( PlatformFloat 		value, EObject ctx) 		{return Float.valueOf(0).toString();}
	def dispatch String getDefaultValueAsString( PlatformDouble 	value, EObject ctx) 		{return Double.valueOf(0).toString();}
	def dispatch String getDefaultValueAsString( PlatformLongDouble value, EObject ctx) 		{return Double.valueOf(0).toString();}
	def dispatch String	getDefaultValueAsString( PlatformShort 		value, EObject ctx) 		{return Short.valueOf( 0 as short).toString();}
	def dispatch String	getDefaultValueAsString( PlatformLong 		value, EObject ctx) 		{return Long.valueOf(0).toString();}
	def dispatch String	getDefaultValueAsString( PlatformLongLong 	value, EObject ctx) 		{return Long.valueOf(0).toString();}
	def dispatch String	getDefaultValueAsString( PlatformUShort 	value, EObject ctx) 		{return Short.valueOf( 0 as short).toString();}
	def dispatch String	getDefaultValueAsString( PlatformULong 		value, EObject ctx) 		{return Long.valueOf(0).toString();}
	def dispatch String	getDefaultValueAsString( PlatformULongLong 	value, EObject ctx) 		{return Long.valueOf(0).toString();}
	def dispatch String getDefaultValueAsString( PlatformString 	value, EObject ctx) 		{return MessageFormat.format(QUOTED_STRING_FMT,"*");}
	def dispatch String getDefaultValueAsString( PlatformBoundedString 	value, EObject ctx) 	{return MessageFormat.format(QUOTED_STRING_FMT,"*");}
	def dispatch String getDefaultValueAsString( PlatformCharArray 	value, EObject ctx) 		{return MessageFormat.format(QUOTED_CHAR_LIST_FMT,"*");}

	def dispatch String getDefaultValueAsString( PlatformSequence 	value, EObject ctx) 		{return MessageFormat.format(QUOTED_CHAR_LIST_FMT,"*");}
	def dispatch String getDefaultValueAsString( PlatformArray 		value, EObject ctx) 		{return MessageFormat.format(QUOTED_CHAR_LIST_FMT,"*");}

	def dispatch String	getDefaultValueAsString( PlatformEnumeration value, EObject ctx) 	
	{
		val LogicalEnumeratedBase leb = getEnumValue(value);
		return  (leb === null) ? QualifiedName.EMPTY.toString():  MessageFormat.format(EXPLAINED_VALUE_FMT,qnp.minimalReferenceString(leb,ctx),leb.description !== null? leb.description: "");
	}
	
}