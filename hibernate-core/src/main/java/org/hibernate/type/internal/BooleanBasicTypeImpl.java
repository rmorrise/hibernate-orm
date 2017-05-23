/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.type.internal;

import org.hibernate.type.descriptor.java.spi.BasicJavaDescriptor;
import org.hibernate.type.descriptor.sql.spi.SqlTypeDescriptor;
import org.hibernate.type.spi.BooleanBasicType;
import org.hibernate.type.spi.ColumnDescriptor;

/**
 * @author Steve Ebersole
 */
public class BooleanBasicTypeImpl<U> extends BasicTypeImpl<Boolean> implements BooleanBasicType {
	private final U trueValue;
	private final U falseValue;

	public BooleanBasicTypeImpl(
			BasicJavaDescriptor javaDescriptor,
			ColumnDescriptor columnMapping,
			U trueValue,
			U falseValue) {
		super( javaDescriptor, columnMapping );
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	public BooleanBasicTypeImpl(
			BasicJavaDescriptor javaDescriptor,
			SqlTypeDescriptor sqlTypeDescriptor,
			U trueValue,
			U falseValue) {
		super( javaDescriptor, sqlTypeDescriptor );
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	@Override
	public Object getTrueValue() {
		return trueValue;
	}

	@Override
	public Object getFalseValue() {
		return falseValue;
	}
}
