/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.exec.results.internal.instantiation;

import java.sql.SQLException;

import org.hibernate.sql.exec.results.spi.JdbcValuesSourceProcessingOptions;
import org.hibernate.sql.exec.results.spi.RowProcessingState;
import org.hibernate.sql.exec.results.spi.QueryResultAssembler;

/**
 * @author Steve Ebersole
 */
public class ArgumentReader implements QueryResultAssembler {
	private final QueryResultAssembler returnAssembler;
	private final String alias;

	public ArgumentReader(QueryResultAssembler returnAssembler, String alias) {
		this.returnAssembler = returnAssembler;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public Object assemble(RowProcessingState rowProcessingState, JdbcValuesSourceProcessingOptions options) throws SQLException {
		return returnAssembler.assemble( rowProcessingState, options );
	}

	public Class getReturnedJavaType() {
		return returnAssembler.getReturnedJavaType();
	}
}