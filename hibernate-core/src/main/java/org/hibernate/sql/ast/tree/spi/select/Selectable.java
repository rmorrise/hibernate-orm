/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.tree.spi.select;

import org.hibernate.sql.ast.tree.spi.expression.Expression;
import org.hibernate.sql.exec.results.spi.SqlSelectable;

/**
 * Represents something that is selectable at the domain level.  This is
 * distinctly different from {@link SqlSelectable} which represents something
 * selectable at the SQL/JDBC level.
 *
 * @author Steve Ebersole
 */
public interface Selectable {
	Selection createSelection(Expression selectedExpression, String resultVariable);
}