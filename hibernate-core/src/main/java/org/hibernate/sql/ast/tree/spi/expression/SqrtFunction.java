/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.tree.spi.expression;

import org.hibernate.metamodel.model.domain.spi.AllowableFunctionReturnType;
import org.hibernate.sql.ast.consume.spi.SqlAstWalker;
import org.hibernate.sql.ast.produce.metamodel.spi.ExpressableType;

/**
 * @author Steve Ebersole
 */
public class SqrtFunction extends AbstractStandardFunction implements StandardFunction {
	private final Expression argument;
	private final AllowableFunctionReturnType type;

	public SqrtFunction(Expression argument) {
		this( argument, (AllowableFunctionReturnType) argument.getType() );
	}

	public SqrtFunction(Expression argument, AllowableFunctionReturnType type) {
		this.argument = argument;
		this.type = type;
	}

	public Expression getArgument() {
		return argument;
	}

	@Override
	public void accept(SqlAstWalker walker) {
		walker.visitSqrtFunction( this );
	}

	@Override
	public ExpressableType getType() {
		return type;
	}
}