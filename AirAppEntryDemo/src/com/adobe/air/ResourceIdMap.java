package com.adobe.air;

import java.lang.reflect.Field;
import java.util.Hashtable;

import android.content.res.Resources;

public class ResourceIdMap {
	private static Hashtable<String, Hashtable<String, Integer>> s_resourceMap;
	private Hashtable<String, Integer> m_resourceIds;

	public ResourceIdMap(String resourceClass) {
		try {
			init(Class.forName(resourceClass));
		} catch (ClassNotFoundException e) {
			System.out.format("Class not found:  %s%n%n",
					new Object[] { resourceClass });
		}
	}

	public ResourceIdMap(Class<?> resourceClass) {
		init(resourceClass);
	}

	private void init(Class<?> resourceClass) {
		if (s_resourceMap == null) {
			s_resourceMap = new Hashtable<String, Hashtable<String, Integer>>();
		}
		if (!s_resourceMap.contains(resourceClass)) {
			this.m_resourceIds = new Hashtable<String, Integer>();
			gatherResourceInfo(resourceClass, this.m_resourceIds);
			s_resourceMap.put(resourceClass.getName(), this.m_resourceIds);
		} else {
			this.m_resourceIds = ((Hashtable<String, Integer>) s_resourceMap.get(resourceClass));
		}
	}

	private static void gatherResourceInfo(Class<?> resourceClass,
			Hashtable<String, Integer> resourceIds) {
		Class<?>[] clss = resourceClass.getClasses();
		for (Class<?> cls : clss) {
			String clsName = cls.getName();
			int indexOfDollarSign = clsName.lastIndexOf('$') + 1;
			if (indexOfDollarSign > 0) {
				clsName = clsName.substring(indexOfDollarSign);
			}
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				try {
					String rscKey = String.format("%s.%s",
							new Object[] { clsName, f.getName() }).intern();
					if (resourceIds.containsKey(rscKey)) {
						System.out.format(
								"Did not add duplicate resource key %s",
								new Object[] { rscKey });
					} else {
						resourceIds.put(rscKey, Integer.valueOf(f.getInt(cls)));
					}
				} catch (IllegalArgumentException e) {
					System.out.format("IllegalArgumentException", new Object[0]);
				} catch (IllegalAccessException e) {
					System.out.format("IllegalAccessException", new Object[0]);
				}
			}
		}
	}

	public int getId(String resourceString) throws IllegalArgumentException,
			Resources.NotFoundException {
		if (resourceString == null) {
			throw new IllegalArgumentException();
		}
		String internedResourceString = resourceString.intern();
		if (!this.m_resourceIds.containsKey(internedResourceString)) {
			throw new Resources.NotFoundException(internedResourceString);
		}
		return ((Integer) this.m_resourceIds.get(internedResourceString))
				.intValue();
	}
}