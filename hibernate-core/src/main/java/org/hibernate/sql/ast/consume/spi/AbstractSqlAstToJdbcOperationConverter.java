/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.consume.spi;

import java.util.Collection;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.sql.ast.consume.SyntaxException;
import org.hibernate.sql.ast.tree.spi.assign.Assignment;

/**
 * @author Steve Ebersole
 */
public class AbstractSqlAstToJdbcOperationConverter
		extends AbstractSqlAstWalker
		implements SqlAstToJdbcOperationConverter {

	// pre-req state
	private final SharedSessionContractImplementor persistenceContext;
	private final QueryParameterBindings parameterBindings;
	private final java.util.Collection<?> loadIdentifiers;

	protected AbstractSqlAstToJdbcOperationConverter(
			SharedSessionContractImplementor persistenceContext,
			QueryParameterBindings parameterBindings,
			Collection<?> loadIdentifiers) {
		this.persistenceContext = persistenceContext;
		this.parameterBindings = parameterBindings;
		this.loadIdentifiers = loadIdentifiers;
	}

	@Override
	public void visitAssignment(Assignment assignment) {
		throw new SyntaxException( "Encountered unexpected assignment clause" );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ParameterBindingContext


	@Override
	protected ConversionContext getConversionContext() {
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getLoadIdentifiers() {
		return (Collection<T>) loadIdentifiers;
	}

	@Override
	public QueryParameterBindings getQueryParameterBindings() {
		return parameterBindings;
	}

	@Override
	public SharedSessionContractImplementor getSession() {
		return persistenceContext;
	}
}
