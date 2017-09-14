/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.domain.spi;

import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.query.sqm.tree.expression.domain.SqmNavigableContainerReference;
import org.hibernate.query.sqm.tree.expression.domain.SqmNavigableReference;
import org.hibernate.query.sqm.tree.from.SqmFrom;
import org.hibernate.sql.NotYetImplementedException;
import org.hibernate.sql.ast.produce.spi.SqlExpressionQualifier;
import org.hibernate.sql.ast.tree.spi.expression.domain.NavigableReference;
import org.hibernate.sql.results.spi.QueryResult;
import org.hibernate.sql.results.spi.QueryResultCreationContext;
import org.hibernate.sql.results.spi.SqlSelectionGroup;
import org.hibernate.sql.results.spi.SqlSelectionGroupResolutionContext;

/**
 * Models a "piece" of the application's domain model that can be navigated
 * as part of a query or the {@link NavigableVisitationStrategy} contract.
 *
 * @author Steve Ebersole
 */
public interface Navigable<T> extends DomainType<T> {
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


	// todo (6.0) : Use (pass in) Selection instead of expression+alias
	// todo (6.0) : ^^ Actually get rid of Selection :)
	// todo (6.0) : another option (thinking this is the best one) is to ask Navigable to generate an SqmExpression (SqmNavigableReference) given some form of `SqmFrom` and a QueryResult given a `SqmNavigableReference`
	//		- in addition to the creation of QueryResults, we'd also need a method for "non-root" Navigables
	// 			to be able to generate Fetches (`Fetchable`?)
	//		the overall flow would be:
	//			1) Navigable -> SqmNavigableReference
	//			2) ( (QueryResultProducer) SqmNavigableReference ) -> QueryResult
	//			3) ( (Fetchable) Navigable ) -> Fetch(FetchParent)
	//
	// see NavigableBindingHelper.createNavigableBinding
	//
	//		something like:
	default SqmNavigableReference createSqmExpression(
			SqmFrom sourceSqmFrom,
			SqmNavigableContainerReference containerReference,
			SqmReferenceCreationContext creationContext) {
		throw new NotYetImplementedException(  );
	}

	interface SqmReferenceCreationContext {
		// org.hibernate.query.sqm.produce.spi.ParsingContext?
		// org.hibernate.query.sqm.produce.spi.ResolutionContext?
		// a new `SqmNodeCreationContext`?
	}

	default QueryResult createQueryResult(
			NavigableReference navigableReference,
			String resultVariable,
			QueryResultCreationContext creationContext) {
		throw new NotYetImplementedException(  );
	}

	SqlSelectionGroup resolveSqlSelectionGroup(
			SqlExpressionQualifier qualifier,
			SqlSelectionGroupResolutionContext resolutionContext);

	/**
	 * Obtain a loggable representation.
	 *
	 * @return The loggable representation of this reference
	 */
	String asLoggableText();
}
