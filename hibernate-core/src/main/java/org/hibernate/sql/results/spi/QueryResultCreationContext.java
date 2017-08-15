/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.spi;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.NavigablePath;
import org.hibernate.sql.ast.produce.spi.SqlExpressionResolver;
import org.hibernate.sql.ast.tree.spi.expression.Expression;
import org.hibernate.sql.ast.tree.spi.expression.domain.ColumnReferenceSource;

/**
 * Contextual information useful when creating a QueryResult.
 *
 * @see org.hibernate.sql.ast.tree.spi.select.Selection#createQueryResult
 *
 * @author Steve Ebersole
 */
public interface QueryResultCreationContext {
	SessionFactoryImplementor getSessionFactory();

	SqlExpressionResolver getSqlSelectionResolver();

	ColumnReferenceSource currentColumnReferenceSource();
	NavigablePath currentNavigablePath();

	boolean shouldCreateShallowEntityResult();
}