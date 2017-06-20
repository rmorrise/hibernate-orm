/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.internal.entities;

import org.hibernate.internal.util.compare.EqualsHelper;

/**
 * Holds information on a property that is audited.
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class PropertyData {
	private final String name;
	/**
	 * Name of the property in the bean.
	 */
	private final String beanName;
	private final String accessType;
	private final boolean usingModifiedFlag;
	private final String modifiedFlagName;

	/**
	 * Copies the given property data, except the name.
	 *
	 * @param newName New name.
	 * @param propertyData Property data to copy the rest of properties from.
	 */
	public PropertyData(String newName, PropertyData propertyData) {
		this ( newName, propertyData.beanName, propertyData.accessType );
	}

	/**
	 * @param name Name of the property.
	 * @param beanName Name of the property in the bean.
	 * @param accessType Accessor type for this property.
	 */
	public PropertyData(String name, String beanName, String accessType) {
		this( name, beanName, accessType, false, null );
	}

	/**
	 * @param name Name of the property.
	 * @param beanName Name of the property in the bean.
	 * @param accessType Accessor type for this property.
	 * @param store How this property should be stored.
	 * @param usingModifiedFlag Defines if field changes should be tracked
	 */
	public PropertyData(
			String name,
			String beanName,
			String accessType,
			boolean usingModifiedFlag,
			String modifiedFlagName) {
		this.name = name;
		this.beanName = beanName;
		this.accessType = accessType;
		this.usingModifiedFlag = usingModifiedFlag;
		this.modifiedFlagName = modifiedFlagName;
	}

	public String getName() {
		return name;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getAccessType() {
		return accessType;
	}

	public boolean isUsingModifiedFlag() {
		return usingModifiedFlag;
	}

	public String getModifiedFlagPropertyName() {
		return modifiedFlagName;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		final PropertyData that = (PropertyData) o;
		return usingModifiedFlag == that.usingModifiedFlag
				&& EqualsHelper.equals( accessType, that.accessType )
				&& EqualsHelper.equals( beanName, that.beanName )
				&& EqualsHelper.equals( name, that.name );
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
		result = 31 * result + (accessType != null ? accessType.hashCode() : 0);
		result = 31 * result + (usingModifiedFlag ? 1 : 0);
		return result;
	}
}