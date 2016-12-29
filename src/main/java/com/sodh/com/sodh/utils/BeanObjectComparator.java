package com.sodh.com.sodh.utils;

import com.cetera.cp.domain.IpBlacklist;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by dhruti on 29/12/16.
 */

public class BeanObjectComparator {

	private static Logger logger = LoggerFactory.getLogger(BeanObjectComparator.class);

	public static void compareObjects(Object oldObject,
									  Object newObject,
									  Map<String, String> oldValues,
									  Map<String, String> newValues)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		if (oldValues == null || newValues == null) {
			/*
            * Important:
            * The calling function needs to provide non-null maps for getting the original
            * and modified values of the object properties.
             */
			return;
		}

		BeanMap map = null;

		if (newObject != null) {
            /*
            * Short-coming:
            * The properties present in the new object are taken as reference for comparison.
            * This is done with the assumption and most common observation that the modifying object
            * would usually have more properties set compared to the previously saved object.
             */
			map = new BeanMap(newObject);
		} else {
            /*
            * Since the new object is null, we're not going to get the bean properties which form
            * reference for the comparison done further down.
             */
			return;
		}

		PropertyUtilsBean propUtils = new PropertyUtilsBean();

		for (Object propNameObject : map.keySet()) {

			String propertyName = (String) propNameObject;
			Object property1 = null;
			Object property2 = null;

			if (oldObject != null)
				property1 = propUtils.getProperty(oldObject, propertyName);
			if (newObject != null)
				property2 = propUtils.getProperty(newObject, propertyName);

			Boolean prop1IsNull = property1 == null;
			Boolean prop2IsNull = property2 == null;

			if (!prop1IsNull && !prop2IsNull) {

                    /*
                    * Since both properties are non-null values, a comparison can be made.
                     */
				if (!property1.equals(property2)) {

					oldValues.put(propertyName, property1.toString());
					newValues.put(propertyName, property2.toString());

				}
			} else if (prop1IsNull && !prop2IsNull) {

				oldValues.put(propertyName, null);
				newValues.put(propertyName, property2.toString());

			} else if (!prop1IsNull && prop2IsNull) {

				oldValues.put(propertyName, property1.toString());
				newValues.put(propertyName, null);

			}

		}

	}

	public static Object getObjectId(Object beanObject)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		PropertyUtilsBean propUtils = new PropertyUtilsBean();

        /*
        * The id field for the IpBlackList object is called ip instead.
        * Added special handling.
         */
		if (beanObject instanceof IpBlacklist) {
			return propUtils.getProperty(beanObject, "ip");
		} else {
			return propUtils.getProperty(beanObject, "id");
		}
	}
}



