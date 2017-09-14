/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.sql.ast.tree.spi.expression;

import java.util.Locale;

import org.hibernate.metamodel.model.relational.spi.Column;
import org.hibernate.sql.NotYetImplementedException;
import org.hibernate.sql.ast.consume.spi.SqlAstWalker;
import org.hibernate.sql.ast.produce.metamodel.spi.ExpressableType;
import org.hibernate.sql.ast.produce.spi.ColumnReferenceQualifier;
import org.hibernate.sql.results.internal.SqlSelectionImpl;
import org.hibernate.sql.results.internal.SqlSelectionReaderImpl;
import org.hibernate.sql.results.spi.SqlSelection;

/**
 * @author Steve Ebersole
 */
public class ColumnReference implements Expression {
	private final ColumnReferenceQualifier qualifier;
	private final Column column;

	public ColumnReference(ColumnReferenceQualifier qualifier, Column column) {
		this.qualifier = qualifier;
		this.column = column;
	}

	@Override
	public SqlSelection createSqlSelection(int jdbcPosition) {
		return new SqlSelectionImpl(
				new SqlSelectionReaderImpl( column.getSqlTypeDescriptor().getJdbcTypeCode() ),
				jdbcPosition
		);
	}

	public ColumnReferenceQualifier getQualifier() {
		return qualifier;
	}

	public Column getColumn() {
		return column;
	}

	@Override
	public void accept(SqlAstWalker  interpreter) {
		interpreter.visitColumnReference( this );
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		final ColumnReference that = (ColumnReference) o;
		return qualifier.equals( that.qualifier )
				&& getColumn().equals( that.getColumn() );
	}

	@Override
	public int hashCode() {
		int result = qualifier.hashCode();
		result = 31 * result + getColumn().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"ColumnBinding(%s.%s)",
				qualifier,
				column.getExpression()
		);
	}

	@Override
	public ExpressableType getType() {
		// n/a
		return null;
	}


	public String renderSqlFragment() {
		// todo (6.0) : ultimately we need to be able to "render" this column ref and append to SqlAppender
		//		this means we need to be able to:
		//			1) resolve corresponding TableReference, currently defined
		//				on ColumnReferenceSource - a *subtype* of SqlExpressionQualifier -
		//			2) use TableReference to render qualified SQL fragment
		throw new NotYetImplementedException(  );
	}
}
