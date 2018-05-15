/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.procedure.internal;

import javax.persistence.ParameterMode;
import org.hibernate.metamodel.model.domain.spi.AllowableParameterType;
import org.hibernate.procedure.ParameterBind;
import org.hibernate.procedure.spi.ParameterBindImplementor;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.QueryParameterBindingImpl;
import org.hibernate.query.spi.QueryParameterBindingTypeResolver;

/**
 * Implementation of the {@link ParameterBind} contract.
 *
 * @author Steve Ebersole
 */
public class ParameterBindImpl<T> extends QueryParameterBindingImpl<T> implements ParameterBindImplementor<T> {
	public ParameterBindImpl(
			QueryParameter<T> queryParameter,
			QueryParameterBindingTypeResolver typeResolver) {
		super( queryParameter, typeResolver );
	}

	public ParameterBindImpl(
			AllowableParameterType bindType,
			QueryParameter<T> queryParameter,
			QueryParameterBindingTypeResolver typeResolver) {
		super( bindType, queryParameter, typeResolver );
	}

	@Override
	public T getValue() {
		return super.getBindValue();
	}

	@Override
	public AllowableParameterType<T> getBindType() {
		return super.getBindType();
	}
}
