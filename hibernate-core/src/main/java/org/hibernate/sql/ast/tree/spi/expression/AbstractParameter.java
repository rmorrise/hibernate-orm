/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.sql.ast.tree.spi.expression;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.sql.NotYetImplementedException;
import org.hibernate.sql.exec.results.internal.SqlSelectionReaderImpl;
import org.hibernate.sql.exec.results.spi.SqlSelectionReader;
import org.hibernate.sql.exec.spi.JdbcParameterBinder;
import org.hibernate.sql.exec.spi.ParameterBindingContext;
import org.hibernate.sql.ast.produce.metamodel.spi.BasicValuedExpressableType;
import org.hibernate.sql.ast.produce.metamodel.spi.ExpressableType;
import org.hibernate.sql.ast.tree.internal.BasicValuedNonNavigableSelection;
import org.hibernate.sql.ast.tree.spi.select.Selectable;
import org.hibernate.sql.ast.tree.spi.select.Selection;

import org.jboss.logging.Logger;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractParameter implements GenericParameter {
	private static final Logger log = Logger.getLogger( AbstractParameter.class );

	private final ExpressableType inferredType;

	public AbstractParameter(ExpressableType inferredType) {
		this.inferredType = inferredType;
	}

	public ExpressableType getInferredType() {
		return inferredType;
	}

	@Override
	public ExpressableType getType() {
		return getInferredType();
	}

	@Override
	public Selectable getSelectable() {
		return this;
	}

	@Override
	public Selection createSelection(Expression selectedExpression, String resultVariable) {
		return new BasicValuedNonNavigableSelection( selectedExpression, resultVariable, this );
	}

	@Override
	public JdbcParameterBinder getParameterBinder() {
		return this;
	}

	@Override
	public SqlSelectionReader getSqlSelectionReader() {
		// todo (6.0) : this limits parameter bindings to just basic (single column) types.

		return new SqlSelectionReaderImpl( ( BasicValuedExpressableType) getType() );
	}

	@Override
	public int bindParameterValue(
			PreparedStatement statement,
			int startPosition,
			ParameterBindingContext bindingContext) throws SQLException {
		final ExpressableType bindType;
		final Object bindValue;

		final QueryParameterBinding valueBinding = resolveBinding( bindingContext );
		if ( valueBinding == null ) {
			warnNoBinding();
			bindType = valueBinding.getBindType();
			bindValue = null;
		}
		else {
			if ( valueBinding.getBindType() == null ) {
				bindType = inferredType;
			}
			else {
				bindType = valueBinding.getBindType();
			}
			bindValue = valueBinding.getBindValue();
		}

		if ( bindType == null ) {
			unresolvedType();
		}
		assert bindType != null;
		if ( bindValue == null ) {
			warnNullBindValue();
		}

		throw new NotYetImplementedException(  );
//		bindType.nullSafeSet( statement, bindValue, startPosition, session );
//		return bindType.getColumnSpan();
	}

	protected abstract void warnNoBinding();

	protected abstract void unresolvedType();

	protected abstract void warnNullBindValue();
}