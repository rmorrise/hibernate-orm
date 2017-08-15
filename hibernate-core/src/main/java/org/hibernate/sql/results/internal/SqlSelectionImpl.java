/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.sql.results.internal;

import org.hibernate.sql.ast.consume.spi.SqlAstWalker;
import org.hibernate.sql.ast.produce.spi.SqlExpressable;
import org.hibernate.sql.results.spi.SqlSelection;

/**
 * @author Steve Ebersole
 */
public class SqlSelectionImpl implements SqlSelection {
	private final SqlExpressable sqlSelectable;
	private final int position;

	public SqlSelectionImpl(SqlExpressable sqlSelectable, int position) {
		this.sqlSelectable = sqlSelectable;
		this.position = position;
	}

	@Override
	public SqlExpressable getSqlSelectable() {
		return sqlSelectable;
	}

	@Override
	public int getValuesArrayPosition() {
		return position;
	}

	@Override
	public void accept(SqlAstWalker interpreter) {
		interpreter.visitSqlSelection( this );
	}
}