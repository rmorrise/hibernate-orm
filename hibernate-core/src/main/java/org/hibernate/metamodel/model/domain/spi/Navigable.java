/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.domain.spi;

import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.sql.exec.results.spi.QueryResult;
import org.hibernate.sql.exec.results.spi.QueryResultCreationContext;
import org.hibernate.sql.exec.results.spi.SqlSelectionResolver;
import org.hibernate.sql.ast.tree.internal.NavigableSelection;
import org.hibernate.sql.ast.tree.spi.expression.Expression;
import org.hibernate.sql.ast.tree.spi.expression.domain.NavigableReference;
import org.hibernate.sql.ast.tree.spi.select.Selectable;
import org.hibernate.sql.ast.tree.spi.select.Selection;

/**
 * Models a "piece" of the application's domain model that can be navigated
 * as part of a query or the {@link NavigableVisitationStrategy} contract.
 *
 * @author Steve Ebersole
 */
public interface Navigable<T> extends DomainType<T>, Selectable {
	/**
	 * The NavigableContainer which contains this Navigable.
	 */
	NavigableContainer getContainer();

	/**
	 * The role for this Navigable which is unique across all
	 * Navigables in the given TypeConfiguration.
	 */
	NavigableRole getNavigableRole();

	default String getNavigableName() {
		return getNavigableRole().getNavigableName();
	}

	/**
	 * Visitation (walking) contract
	 *
	 * @param visitor The "visitor" responsibility in the Visitor pattern
	 */
	void visitNavigable(NavigableVisitationStrategy visitor);


	@Override
	default Selection createSelection(Expression selectedExpression, String resultVariable) {
		assert selectedExpression instanceof NavigableReference;
		return new NavigableSelection( (NavigableReference) selectedExpression, resultVariable );
	}

	// todo (6.0) : Use (pass in) Selection instead of expression+alias

	QueryResult generateQueryResult(
			NavigableReference selectedExpression,
			String resultVariable,
			SqlSelectionResolver sqlSelectionResolver,
			QueryResultCreationContext creationContext);

	/**
	 * Obtain a loggable representation.
	 *
	 * @return The loggable representation of this reference
	 */
	String asLoggableText();
}