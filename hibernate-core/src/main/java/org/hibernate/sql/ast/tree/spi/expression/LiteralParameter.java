/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.tree.spi.expression;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.TemporalType;

import org.hibernate.metamodel.model.domain.spi.AllowableParameterType;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.sql.ast.consume.spi.ParameterBindingResolutionContext;
import org.hibernate.sql.ast.consume.spi.SqlAstWalker;
import org.hibernate.sql.ast.produce.metamodel.spi.ExpressableType;
import org.hibernate.sql.exec.spi.ParameterBindingContext;
import org.hibernate.sql.results.spi.SqlSelection;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * @author Steve Ebersole
 */
public class LiteralParameter implements GenericParameter, QueryParameterBinding {
	private final Object value;
	private final AllowableParameterType type;

	public LiteralParameter(Object value, AllowableParameterType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public QueryParameterBinding resolveBinding(ParameterBindingResolutionContext context) {
		return this;
	}

	@Override
	public ExpressableType getType() {
		return type;
	}

	@Override
	public SqlSelection createSqlSelection(int jdbcPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(SqlAstWalker sqlTreeWalker) {
		sqlTreeWalker.visitGenericParameter( this );
	}

	@Override
	public boolean isBound() {
		return true;
	}

	@Override
	public boolean allowsMultiValued() {
		return false;
	}

	@Override
	public boolean isMultiValued() {
		return false;
	}

	@Override
	public AllowableParameterType getBindType() {
		return type;
	}

	@Override
	public void setBindValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBindValue(Object value, AllowableParameterType clarifiedType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBindValue(Object value, TemporalType temporalTypePrecision) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getBindValue() {
		return value;
	}

	@Override
	public void setBindValues(Collection values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBindValues(Collection values, AllowableParameterType clarifiedType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBindValues(
			Collection values,
			TemporalType temporalTypePrecision,
			TypeConfiguration typeConfiguration) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection getBindValues() {
		return null;
	}

	@Override
	public int bindParameterValue(
			PreparedStatement statement,
			int startPosition,
			ParameterBindingContext context) throws SQLException {
		type.getValueBinder().bind( statement, value, startPosition, context.getSession() );
		return 1;
	}
}