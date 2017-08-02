/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.tree.internal;

import org.hibernate.sql.exec.results.spi.QueryResult;
import org.hibernate.sql.exec.results.spi.QueryResultCreationContext;
import org.hibernate.sql.exec.results.spi.SqlSelectionResolver;
import org.hibernate.sql.ast.tree.spi.expression.domain.NavigableReference;
import org.hibernate.sql.ast.tree.spi.select.Selection;

/**
 * @author Steve Ebersole
 */
public class NavigableSelection implements Selection {
	private final NavigableReference selectedExpression;
	private final String resultVariable;

	public NavigableSelection(NavigableReference selectedExpression, String resultVariable) {
		this.selectedExpression = selectedExpression;
		this.resultVariable = resultVariable;
	}

	public NavigableReference getSelectedExpression() {
		return selectedExpression;
	}

	@Override
	public String getResultVariable() {
		return resultVariable;
	}

	@Override
	public QueryResult createQueryResult(
			SqlSelectionResolver sqlSelectionResolver,
			QueryResultCreationContext creationContext) {
		// todo (6.0) : look at removing `selectedExpression` from this contract as well...
		//		would then need:
		//			1) NavigablePath

		return getSelectedExpression().getNavigable().generateQueryResult(
				getSelectedExpression(),
				getResultVariable(),
				sqlSelectionResolver,
				creationContext
		);
	}
}