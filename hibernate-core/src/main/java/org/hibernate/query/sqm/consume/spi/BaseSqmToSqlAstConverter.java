/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.consume.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.internal.util.collections.Stack;
import org.hibernate.metamodel.model.domain.spi.AllowableParameterType;
import org.hibernate.metamodel.model.domain.spi.EntityDescriptor;
import org.hibernate.query.NavigablePath;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.query.sqm.tree.SqmQuerySpec;
import org.hibernate.query.sqm.tree.expression.BinaryArithmeticSqmExpression;
import org.hibernate.query.sqm.tree.expression.CaseSearchedSqmExpression;
import org.hibernate.query.sqm.tree.expression.CaseSimpleSqmExpression;
import org.hibernate.query.sqm.tree.expression.ConcatSqmExpression;
import org.hibernate.query.sqm.tree.expression.ConstantEnumSqmExpression;
import org.hibernate.query.sqm.tree.expression.ConstantFieldSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralBigDecimalSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralBigIntegerSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralCharacterSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralDoubleSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralFalseSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralFloatSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralIntegerSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralLongSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralNullSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralStringSqmExpression;
import org.hibernate.query.sqm.tree.expression.LiteralTrueSqmExpression;
import org.hibernate.query.sqm.tree.expression.NamedParameterSqmExpression;
import org.hibernate.query.sqm.tree.expression.PositionalParameterSqmExpression;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.query.sqm.tree.expression.UnaryOperationSqmExpression;
import org.hibernate.query.sqm.tree.expression.domain.SqmEntityReference;
import org.hibernate.query.sqm.tree.expression.domain.SqmPluralAttributeReference;
import org.hibernate.query.sqm.tree.expression.domain.SqmSingularAttributeReference;
import org.hibernate.query.sqm.tree.expression.domain.SqmSingularAttributeReferenceAny;
import org.hibernate.query.sqm.tree.expression.domain.SqmSingularAttributeReferenceBasic;
import org.hibernate.query.sqm.tree.expression.domain.SqmSingularAttributeReferenceEmbedded;
import org.hibernate.query.sqm.tree.expression.domain.SqmSingularAttributeReferenceEntity;
import org.hibernate.query.sqm.tree.expression.function.SqmAbsFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmAvgFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmBitLengthFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCastFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCoalesceFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmConcatFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCountFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCountStarFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCurrentDateFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCurrentTimeFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmCurrentTimestampFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmExtractFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmGenericFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmLengthFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmLocateFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmLowerFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmMaxFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmMinFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmModFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmNullifFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmSumFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmTrimFunction;
import org.hibernate.query.sqm.tree.expression.function.SqmUpperFunction;
import org.hibernate.query.sqm.tree.from.SqmAttributeJoin;
import org.hibernate.query.sqm.tree.from.SqmCrossJoin;
import org.hibernate.query.sqm.tree.from.SqmEntityJoin;
import org.hibernate.query.sqm.tree.from.SqmFromClause;
import org.hibernate.query.sqm.tree.from.SqmFromElementSpace;
import org.hibernate.query.sqm.tree.from.SqmJoin;
import org.hibernate.query.sqm.tree.from.SqmRoot;
import org.hibernate.query.sqm.tree.order.SqmOrderByClause;
import org.hibernate.query.sqm.tree.order.SqmSortSpecification;
import org.hibernate.query.sqm.tree.paging.SqmLimitOffsetClause;
import org.hibernate.query.sqm.tree.predicate.AndSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.BetweenSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.GroupedSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.InListSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.InSubQuerySqmPredicate;
import org.hibernate.query.sqm.tree.predicate.LikeSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.NegatedSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.NullnessSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.OrSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.RelationalPredicateOperator;
import org.hibernate.query.sqm.tree.predicate.RelationalSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmWhereClause;
import org.hibernate.query.sqm.tree.select.SqmSelectClause;
import org.hibernate.query.sqm.tree.select.SqmSelection;
import org.hibernate.sql.NotYetImplementedException;
import org.hibernate.sql.ast.JoinType;
import org.hibernate.sql.ast.produce.metamodel.spi.BasicValuedExpressableType;
import org.hibernate.sql.ast.produce.metamodel.spi.SqlAliasBaseGenerator;
import org.hibernate.sql.ast.produce.spi.FromClauseIndex;
import org.hibernate.sql.ast.produce.spi.JoinedTableGroupContext;
import org.hibernate.sql.ast.produce.spi.NonQualifiableSqlExpressable;
import org.hibernate.sql.ast.produce.spi.QualifiableSqlExpressable;
import org.hibernate.sql.ast.produce.spi.RootTableGroupContext;
import org.hibernate.sql.ast.produce.spi.SqlAliasBaseManager;
import org.hibernate.sql.ast.produce.spi.SqlAstBuildingContext;
import org.hibernate.sql.ast.produce.spi.SqlAstFunctionProducer;
import org.hibernate.sql.ast.produce.spi.SqlExpressable;
import org.hibernate.sql.ast.produce.spi.SqlExpressionQualifier;
import org.hibernate.sql.ast.produce.spi.SqlExpressionResolver;
import org.hibernate.sql.ast.produce.spi.SqlSelectionExpression;
import org.hibernate.sql.ast.produce.spi.TableGroupJoinProducer;
import org.hibernate.sql.ast.produce.sqm.spi.SqmSelectToSqlAstConverter;
import org.hibernate.sql.ast.produce.sqm.spi.SqmToSqlAstConverter;
import org.hibernate.sql.ast.tree.spi.Clause;
import org.hibernate.sql.ast.tree.spi.QuerySpec;
import org.hibernate.sql.ast.tree.spi.expression.AbsFunction;
import org.hibernate.sql.ast.tree.spi.expression.AvgFunction;
import org.hibernate.sql.ast.tree.spi.expression.BinaryArithmeticExpression;
import org.hibernate.sql.ast.tree.spi.expression.BitLengthFunction;
import org.hibernate.sql.ast.tree.spi.expression.CaseSearchedExpression;
import org.hibernate.sql.ast.tree.spi.expression.CaseSimpleExpression;
import org.hibernate.sql.ast.tree.spi.expression.CastFunction;
import org.hibernate.sql.ast.tree.spi.expression.CoalesceFunction;
import org.hibernate.sql.ast.tree.spi.expression.ConcatFunction;
import org.hibernate.sql.ast.tree.spi.expression.CountFunction;
import org.hibernate.sql.ast.tree.spi.expression.CountStarFunction;
import org.hibernate.sql.ast.tree.spi.expression.CurrentDateFunction;
import org.hibernate.sql.ast.tree.spi.expression.CurrentTimeFunction;
import org.hibernate.sql.ast.tree.spi.expression.CurrentTimestampFunction;
import org.hibernate.sql.ast.tree.spi.expression.Expression;
import org.hibernate.sql.ast.tree.spi.expression.ExtractFunction;
import org.hibernate.sql.ast.tree.spi.expression.LengthFunction;
import org.hibernate.sql.ast.tree.spi.expression.LocateFunction;
import org.hibernate.sql.ast.tree.spi.expression.LowerFunction;
import org.hibernate.sql.ast.tree.spi.expression.MaxFunction;
import org.hibernate.sql.ast.tree.spi.expression.MinFunction;
import org.hibernate.sql.ast.tree.spi.expression.ModFunction;
import org.hibernate.sql.ast.tree.spi.expression.NamedParameter;
import org.hibernate.sql.ast.tree.spi.expression.NonStandardFunction;
import org.hibernate.sql.ast.tree.spi.expression.NullifFunction;
import org.hibernate.sql.ast.tree.spi.expression.PositionalParameter;
import org.hibernate.sql.ast.tree.spi.expression.QueryLiteral;
import org.hibernate.sql.ast.tree.spi.expression.SumFunction;
import org.hibernate.sql.ast.tree.spi.expression.TrimFunction;
import org.hibernate.sql.ast.tree.spi.expression.UnaryOperation;
import org.hibernate.sql.ast.tree.spi.expression.UpperFunction;
import org.hibernate.sql.ast.tree.spi.expression.domain.ColumnReferenceSource;
import org.hibernate.sql.ast.tree.spi.expression.domain.NavigableContainerReference;
import org.hibernate.sql.ast.tree.spi.expression.domain.NavigableReference;
import org.hibernate.sql.ast.tree.spi.expression.domain.PluralAttributeReference;
import org.hibernate.sql.ast.tree.spi.expression.domain.SingularAttributeReference;
import org.hibernate.sql.ast.tree.spi.from.EntityTableGroup;
import org.hibernate.sql.ast.tree.spi.from.FromClause;
import org.hibernate.sql.ast.tree.spi.from.TableGroup;
import org.hibernate.sql.ast.tree.spi.from.TableGroupJoin;
import org.hibernate.sql.ast.tree.spi.from.TableSpace;
import org.hibernate.sql.ast.tree.spi.predicate.BetweenPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.GroupedPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.InListPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.InSubQueryPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.Junction;
import org.hibernate.sql.ast.tree.spi.predicate.LikePredicate;
import org.hibernate.sql.ast.tree.spi.predicate.NegatedPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.NullnessPredicate;
import org.hibernate.sql.ast.tree.spi.predicate.Predicate;
import org.hibernate.sql.ast.tree.spi.predicate.RelationalPredicate;
import org.hibernate.sql.ast.tree.spi.select.SelectClause;
import org.hibernate.sql.ast.tree.spi.sort.SortSpecification;
import org.hibernate.sql.results.spi.SqlSelection;
import org.hibernate.type.spi.BasicType;
import org.hibernate.type.spi.StandardSpiBasicTypes;

import org.jboss.logging.Logger;

/**
 * @author Steve Ebersole
 */
public abstract class BaseSqmToSqlAstConverter
		extends BaseSemanticQueryWalker
		implements SqmToSqlAstConverter, SqlExpressionResolver {

	private static final Logger log = Logger.getLogger( BaseSqmToSqlAstConverter.class );

	protected enum Shallowness {
		NONE,
		CTOR,
		FUNCTION,
		SUBQUERY
	}

	private final SqlAstBuildingContext sqlAstBuildingContext;
	private final QueryOptions queryOptions;

	private final SqlAliasBaseManager sqlAliasBaseManager = new SqlAliasBaseManager();

	private final Map<QuerySpec,Map<SqlExpressable,SqlSelection>> sqlSelectionMapByQuerySpec = new HashMap<>();

	private final FromClauseIndex fromClauseIndex = new FromClauseIndex();

	private final Stack<Clause> currentClauseStack = new Stack<>();
	private final Stack<QuerySpec> querySpecStack = new Stack<>();
	private final Stack<TableGroup> tableGroupStack = new Stack<>();
	private final Stack<SqmSelectToSqlAstConverter.Shallowness> shallownessStack = new Stack<>( SqmSelectToSqlAstConverter.Shallowness.NONE );
	private final Stack<NavigableReference> navigableReferenceStack = new Stack<>();


	public BaseSqmToSqlAstConverter(
			SqlAstBuildingContext sqlAstBuildingContext,
			QueryOptions queryOptions) {
		this.sqlAstBuildingContext = sqlAstBuildingContext;
		this.queryOptions = queryOptions;
	}

	protected QuerySpec currentQuerySpec() {
		return querySpecStack.getCurrent();
	}

	public SqlAstBuildingContext getSqlAstBuildingContext() {
		return sqlAstBuildingContext;
	}

	public QueryOptions getQueryOptions() {
		return queryOptions;
	}

	public SqlAliasBaseManager getSqlAliasBaseManager() {
		return sqlAliasBaseManager;
	}

	public FromClauseIndex getFromClauseIndex() {
		return fromClauseIndex;
	}

	public Stack<Clause> getCurrentClauseStack() {
		return currentClauseStack;
	}

	public Stack<QuerySpec> getQuerySpecStack() {
		return querySpecStack;
	}

	public Stack<TableGroup> getTableGroupStack() {
		return tableGroupStack;
	}

	public Stack<Shallowness> getShallownessStack() {
		return shallownessStack;
	}

	public Stack<NavigableReference> getNavigableReferenceStack() {
		return navigableReferenceStack;
	}

	public Stack<FromClause> getFromClauseStack() {
		return fromClauseStack;
	}

	protected void primeQuerySpecStack(QuerySpec querySpec) {
		primeStack( querySpecStack, querySpec );
	}

	protected <T> void primeStack(Stack<T> stack, T initialValue) {
		verifyCanBePrimed( stack );
		stack.push( initialValue );
	}

	private static void verifyCanBePrimed(Stack stack) {
		if ( !stack.isEmpty() ) {
			throw new IllegalStateException( "Cannot prime an already populated Stack" );
		}
	}

	protected void primeNavigableReferenceStack(NavigableReference initial) {
		primeStack( navigableReferenceStack, initial );
	}

	// NOTE : assumes that Expression impls "properly" implement equals/hashCode which may not be the
	//		best option.

	// todo (6.0) : decide if we want to do the caching/unique-ing of Expressions here.
	//		its really only needed for top-level select clauses, not sub-queries.
	//		its "ok" to do it for sub-queries as well - just wondering about the overhead.
	private Map<QuerySpec,Map<Expression, SqlSelection>> sqlExpressionToSqlSelectionMapByQuerySpec;

	private Map<QuerySpec,Map<SqlExpressionQualifier,Map<QualifiableSqlExpressable,SqlSelection>>> qualifiedSExpressionToSqlSelectionMapByQuerySpec;

	// todo (6.0) : is there ever a time when resolving a sqlSelectable relative to a qualifier ought to return different expressions for multiple references?

	@Override
	public Expression resolveSqlExpression(SqlExpressionQualifier qualifier, QualifiableSqlExpressable sqlSelectable) {
		return normalizeSqlExpression( qualifier.qualify( sqlSelectable ) );
	}

	private Expression normalizeSqlExpression(Expression expression) {
		if ( getCurrentClauseStack().getCurrent() == Clause.ORDER
				|| getCurrentClauseStack().getCurrent() == Clause.GROUP
				|| getCurrentClauseStack().getCurrent() == Clause.HAVING ) {
			// see if this (Sql)Expression is used as a selection, and if so
			// wrap the (Sql)Expression in a special wrapper with access to both
			// the (Sql)Expression and the SqlSelection.
			//
			// This is used for databases which prefer to use the position of a
			// selected expression (within the select-clause) as the
			// order-by, group-by or having expression
			if ( sqlExpressionToSqlSelectionMapByQuerySpec != null ) {
				final Map<Expression, SqlSelection> sqlExpressionToSqlSelectionMap =
						sqlExpressionToSqlSelectionMapByQuerySpec.get( currentQuerySpec() );
				if ( sqlExpressionToSqlSelectionMap != null ) {
					final SqlSelection selection = sqlExpressionToSqlSelectionMap.get( expression );
					if ( selection != null ) {
						return new SqlSelectionExpression( selection, expression );
					}
				}
			}
		}

		return expression;
	}

	@Override
	public Expression resolveSqlExpression(NonQualifiableSqlExpressable sqlSelectable) {
		return normalizeSqlExpression( sqlSelectable.createExpression() );
	}


//	@Override
//	public SqlSelection resolveSqlSelection(SqlExpressable sqlSelectable) {
//		// todo (6.0) : this needs to be relative to some notion of a particular TableGroup
//		//		SqlSelectable e.g. would be a ColumnReference
//
//		final Map<SqlExpressable,SqlSelection> sqlSelectionMap = sqlSelectionMapByQuerySpec.computeIfAbsent(
//				currentQuerySpec(),
//				k -> new HashMap<>()
//		);
//
//		final SqlSelection existing = sqlSelectionMap.get( sqlSelectable );
//		if ( existing != null ) {
//			return existing;
//		}
//
//		final SqlSelection sqlSelection = new SqlSelectionImpl( sqlSelectable, sqlSelectionMap.size() );
//		currentQuerySpec().getSelectClause().addSqlSelection( sqlSelection );
//		sqlSelectionMap.put( sqlSelectable, sqlSelection );
//
//		return sqlSelection;
//	}


	@Override
	public Void visitOrderByClause(SqmOrderByClause orderByClause) {
		super.visitOrderByClause( orderByClause );
		return null;
	}

	@Override
	public SortSpecification visitSortSpecification(SqmSortSpecification sortSpecification) {
		return new SortSpecification(
				(Expression) sortSpecification.getSortExpression().accept( this ),
				sortSpecification.getCollation(),
				sortSpecification.getSortOrder()
		);
	}

	@Override
	public QuerySpec visitQuerySpec(SqmQuerySpec querySpec) {
		final QuerySpec astQuerySpec = new QuerySpec( querySpecStack.isEmpty() );
		querySpecStack.push( astQuerySpec );
		fromClauseStack.push( astQuerySpec.getFromClause() );

		try {
			// we want to visit the from-clause first
			visitFromClause( querySpec.getFromClause() );

			final SqmSelectClause selectClause = querySpec.getSelectClause();
			if ( selectClause != null ) {
				visitSelectClause( selectClause );
			}

			final SqmWhereClause whereClause = querySpec.getWhereClause();
			if ( whereClause != null ) {
				currentClauseStack.push( Clause.WHERE );
				try {
					astQuerySpec.setWhereClauseRestrictions(
							(Predicate) whereClause.getPredicate().accept( this )
					);
				}
				finally {
					currentClauseStack.pop();
				}
			}

			// todo : group-by
			// todo : having

			if ( querySpec.getOrderByClause() != null ) {
				currentClauseStack.push( Clause.ORDER );
				try {
					for ( SqmSortSpecification sortSpecification : querySpec.getOrderByClause().getSortSpecifications() ) {
						astQuerySpec.addSortSpecification( visitSortSpecification( sortSpecification ) );
					}
				}
				finally {
					currentClauseStack.pop();
				}
			}

			final SqmLimitOffsetClause limitOffsetClause = querySpec.getLimitOffsetClause();
			if ( limitOffsetClause != null ) {
				currentClauseStack.push( Clause.LIMIT );
				try {
					if ( limitOffsetClause.getLimitExpression() != null ) {
						astQuerySpec.setLimitClauseExpression(
								(Expression) limitOffsetClause.getLimitExpression().accept( this )
						);
					}
					if ( limitOffsetClause.getOffsetExpression() != null ) {
						astQuerySpec.setOffsetClauseExpression(
								(Expression) limitOffsetClause.getOffsetExpression().accept( this )
						);
					}
				}
				finally {
					currentClauseStack.pop();
				}
			}

			return astQuerySpec;
		}
		finally {
			assert querySpecStack.pop() == astQuerySpec;
			assert fromClauseStack.pop() == astQuerySpec.getFromClause();
		}
	}

	@Override
	public Void visitFromClause(SqmFromClause fromClause) {
		currentClauseStack.push( Clause.FROM );
		try {
			fromClause.getFromElementSpaces().forEach( this::visitFromElementSpace );
		}
		finally {
			currentClauseStack.pop();
		}
		return null;
	}

	final Stack<FromClause> fromClauseStack = new Stack<>();
	private TableSpace tableSpace;

	@Override
	public TableSpace visitFromElementSpace(SqmFromElementSpace fromElementSpace) {
		tableSpace = fromClauseStack.getCurrent().makeTableSpace();
		try {
			visitRootEntityFromElement( fromElementSpace.getRoot() );
			for ( SqmJoin sqmJoin : fromElementSpace.getJoins() ) {
				tableSpace.addJoinedTableGroup( (TableGroupJoin) sqmJoin.accept( this ) );
			}
			return tableSpace;
		}
		finally {
			tableSpace = null;
		}
	}

	@Override
	public Object visitRootEntityFromElement(SqmRoot sqmRoot) {
		log.tracef( "Starting resolution of SqmRoot [%s] to TableGroup", sqmRoot );

		if ( fromClauseIndex.isResolved( sqmRoot ) ) {
			final TableGroup resolvedTableGroup = fromClauseIndex.findResolvedTableGroup( sqmRoot );
			log.tracef( "SqmRoot [%s] resolved to existing TableGroup [%s]", sqmRoot, resolvedTableGroup );
			return resolvedTableGroup;
		}

		final SqmEntityReference binding = sqmRoot.getNavigableReference();
		final EntityDescriptor entityMetadata = (EntityDescriptor) binding.getReferencedNavigable();
		final EntityTableGroup group = entityMetadata.createRootTableGroup(
				sqmRoot,
				new RootTableGroupContext() {
					@Override
					public QuerySpec getQuerySpec() {
						return currentQuerySpec();
					}

					@Override
					public TableSpace getTableSpace() {
						return tableSpace;
					}

					@Override
					public void addRestriction(Predicate predicate) {
						currentQuerySpec().addRestriction( predicate );
					}

					@Override
					public SqlAliasBaseGenerator getSqlAliasBaseGenerator() {
						return BaseSqmToSqlAstConverter.this.getSqlAliasBaseManager();
					}

					@Override
					public JoinType getTableReferenceJoinType() {
						// TableReferences within the TableGroup can be
						// inner-joined (unless they are optional, which is handled
						// inside the producers)
						return JoinType.INNER;
					}
				}
		);
		tableSpace.setRootTableGroup( group );
		tableGroupStack.push( group );
		fromClauseIndex.crossReference( sqmRoot, group );

		navigableReferenceStack.push( group.asExpression() );

		log.tracef( "Resolved SqmRoot [%s] to new TableGroup [%s]", sqmRoot, group );

		return group;
	}

	@Override
	public Object visitQualifiedAttributeJoinFromElement(SqmAttributeJoin joinedFromElement) {
		final TableGroup existing = fromClauseIndex.findResolvedTableGroup( joinedFromElement );
		if ( existing != null ) {
			return existing;
		}

		final QuerySpec querySpec = currentQuerySpec();
		final TableGroupJoinProducer joinableAttribute = (TableGroupJoinProducer) joinedFromElement.getAttributeReference()
				.getReferencedNavigable();

		final TableGroup lhsTableGroup = fromClauseIndex.findResolvedTableGroup( joinedFromElement.getLhs() );



		final TableGroupJoin tableGroupJoin = joinableAttribute.createTableGroupJoin(
				joinedFromElement,
				joinedFromElement.getJoinType().getCorrespondingSqlJoinType(),
				new JoinedTableGroupContext() {
					@Override
					public TableGroup getLhs() {
						return lhsTableGroup;
					}

					@Override
					public ColumnReferenceSource getColumnReferenceSource() {
						return getLhs();
					}

					@Override
					public NavigablePath getNavigablePath() {
						return joinedFromElement.getNavigableReference().getNavigablePath();
					}

					@Override
					public QuerySpec getQuerySpec() {
						return querySpec;
					}

					@Override
					public TableSpace getTableSpace() {
						return tableSpace;
					}

					@Override
					public JoinType getTableReferenceJoinType() {
						// TableReferences within the joined TableGroup can be
						// inner-joined (unless they are optional, which is handled
						// inside the producers)
						return JoinType.INNER;
					}

					@Override
					public SqlAliasBaseGenerator getSqlAliasBaseGenerator() {
						return BaseSqmToSqlAstConverter.this.getSqlAliasBaseManager();
					}
				}
		);

		// add any additional join restrictions
		if ( joinedFromElement.getOnClausePredicate() != null ) {
			currentQuerySpec().addRestriction(
					(Predicate) joinedFromElement.getOnClausePredicate().accept( this )
			);
		}

		tableGroupStack.push( tableGroupJoin.getJoinedGroup() );
		fromClauseIndex.crossReference( joinedFromElement, tableGroupJoin.getJoinedGroup() );

		return tableGroupJoin;
	}

	@Override
	public TableGroupJoin visitCrossJoinedFromElement(SqmCrossJoin joinedFromElement) {
		final QuerySpec querySpec = currentQuerySpec();
		final EntityDescriptor entityPersister = joinedFromElement.getIntrinsicSubclassEntityMetadata();
		final EntityTableGroup group = entityPersister.createRootTableGroup(
				joinedFromElement,
				new RootTableGroupContext() {
					@Override
					public QuerySpec getQuerySpec() {
						return querySpec;
					}

					@Override
					public TableSpace getTableSpace() {
						return tableSpace;
					}

					@Override
					public SqlAliasBaseGenerator getSqlAliasBaseGenerator() {
						return BaseSqmToSqlAstConverter.this.getSqlAliasBaseManager();
					}

					@Override
					public JoinType getTableReferenceJoinType() {
						// TableReferences within the cross-joined TableGroup can be
						// inner-joined (unless they are optional, which is handled
						// inside the producers)
						return JoinType.INNER;
					}

					@Override
					public void addRestriction(Predicate predicate) {
						log.debugf(
								"Adding restriction [%s] to where-clause for cross-join [%s]",
								predicate,
								joinedFromElement.getNavigableReference()
						);
						querySpec.addRestriction( predicate );
					}
				}
		);

		tableGroupStack.push( group );
		fromClauseIndex.crossReference( joinedFromElement, group );

		return new TableGroupJoin( JoinType.CROSS, group, null );
	}

	@Override
	public Object visitQualifiedEntityJoinFromElement(SqmEntityJoin joinedFromElement) {
		throw new NotYetImplementedException();
	}



	@Override
	public SelectClause visitSelectClause(SqmSelectClause selectClause) {
		currentClauseStack.push( Clause.SELECT );
		shallownessStack.push( SqmSelectToSqlAstConverter.Shallowness.SUBQUERY );
		try {
			super.visitSelectClause( selectClause );
			for ( SqmSelection sqmSelection : selectClause.getSelections() ) {
				final Object sqlExpression = sqmSelection.getExpression().accept( this );
				processSelectedExpression( (Expression) sqlExpression, sqmSelection.getAlias() );
			}

			currentQuerySpec().getSelectClause().makeDistinct( selectClause.isDistinct() );
			return currentQuerySpec().getSelectClause();
		}
		finally {
			shallownessStack.pop();
			currentClauseStack.pop();
		}
	}

//	@Override
//	@SuppressWarnings("unchecked")
//	public Void visitSelection(SqmSelection sqmSelection) {
//		final Expression expression = (Expression) sqmSelection.getExpression().accept( this );
//		processSelectedExpression( expression, sqmSelection.getAlias() );
//
//		if ( shouldCreateQueryResults() ) {
//
//		}
//		if ( querySpecStack.depth() == 1 ) {
//			// todo (6.0) : `== 1` works for SELECT queries, but not mutation queries
//		}
//		expression.createQueryResult( ... );
//		final Selection selection = expression.getSelectable().createSelection(
//				expression,
//				sqmSelection.getAlias()
//		);
////		currentQuerySpec().getSelectClause().selection( selection );
//
//		return selection;
//	}

	protected void processSelectedExpression(Expression expression, String resultVariable) {
		// in the base converter we have nothing to do
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Expressions

	@Override
	public SingularAttributeReference visitBasicValuedSingularAttribute(SqmSingularAttributeReferenceBasic sqmAttributeReference) {
		return buildSingularAttributeReference( sqmAttributeReference );
	}

	protected SingularAttributeReference buildSingularAttributeReference(SqmSingularAttributeReference sqmAttributeReference) {
		final NavigableContainerReference containerReference = (NavigableContainerReference) navigableReferenceStack.getCurrent();
		return new SingularAttributeReference(
				containerReference,
				sqmAttributeReference.getReferencedNavigable(),
				sqmAttributeReference.getNavigablePath()
		);
	}

	@Override
	public SingularAttributeReference visitEntityValuedSingularAttribute(SqmSingularAttributeReferenceEntity sqmAttributeReference) {
		return buildSingularAttributeReference( sqmAttributeReference );
	}

	@Override
	public SingularAttributeReference visitEmbeddableValuedSingularAttribute(SqmSingularAttributeReferenceEmbedded sqmAttributeReference) {
		return buildSingularAttributeReference( sqmAttributeReference );
	}

	@Override
	public SingularAttributeReference visitAnyValuedSingularAttribute(SqmSingularAttributeReferenceAny sqmAttributeReference) {
		return buildSingularAttributeReference( sqmAttributeReference );
	}

	@Override
	public PluralAttributeReference visitPluralAttribute(SqmPluralAttributeReference reference) {
		final NavigableContainerReference containerReference = (NavigableContainerReference) getNavigableReferenceStack().getCurrent();
		final PluralAttributeReference attributeReference = new PluralAttributeReference(
				containerReference,
				reference.getReferencedNavigable(),
				containerReference.getNavigablePath().append( reference.getReferencedNavigable().getNavigableName() )
		);

		navigableReferenceStack.push( attributeReference );

		return attributeReference;
	}

	@Override
	public QueryLiteral visitLiteralStringExpression(LiteralStringSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.STRING ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	protected BasicValuedExpressableType resolveType(
			BasicValuedExpressableType expressionType,
			BasicType defaultType) {
		return expressionType != null
				? expressionType
				: defaultType;
	}

	@Override
	public QueryLiteral visitLiteralCharacterExpression(LiteralCharacterSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.CHARACTER ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralDoubleExpression(LiteralDoubleSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.DOUBLE ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralIntegerExpression(LiteralIntegerSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.INTEGER ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralBigIntegerExpression(LiteralBigIntegerSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.BIG_INTEGER ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralBigDecimalExpression(LiteralBigDecimalSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.BIG_DECIMAL ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralFloatExpression(LiteralFloatSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.FLOAT ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralLongExpression(LiteralLongSqmExpression expression) {
		return new QueryLiteral(
				expression.getLiteralValue(),
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.LONG ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralTrueExpression(LiteralTrueSqmExpression expression) {
		return new QueryLiteral(
				Boolean.TRUE,
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.BOOLEAN ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralFalseExpression(LiteralFalseSqmExpression expression) {
		return new QueryLiteral(
				Boolean.FALSE,
				resolveType( expression.getExpressionType(), StandardSpiBasicTypes.BOOLEAN ),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public QueryLiteral visitLiteralNullExpression(LiteralNullSqmExpression expression) {
		return new QueryLiteral(
				null,
				expression.getExpressionType(),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public Object visitConstantEnumExpression(ConstantEnumSqmExpression expression) {
		return new QueryLiteral(
				expression.getValue(),
				expression.getExpressionType(),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public Object visitConstantFieldExpression(ConstantFieldSqmExpression expression) {
		return new QueryLiteral(
				expression.getValue(),
				expression.getExpressionType(),
				getCurrentClauseStack().getCurrent() == Clause.SELECT
		);
	}

	@Override
	public NamedParameter visitNamedParameterExpression(NamedParameterSqmExpression expression) {
		return new NamedParameter(
				expression.getName(),
				(AllowableParameterType) expression.getExpressionType()
		);
	}

	@Override
	public PositionalParameter visitPositionalParameterExpression(PositionalParameterSqmExpression expression) {
		return new PositionalParameter(
				expression.getPosition(),
				(AllowableParameterType) expression.getExpressionType()
		);
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// non-standard functions

	@Override
	public Object visitGenericFunction(SqmGenericFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );
		try {
			return new NonStandardFunction(
					expression.getFunctionName(),
					expression.getExpressionType(),
					visitArguments( expression.getArguments() )
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	private List<Expression> visitArguments(List<SqmExpression> sqmArguments) {
		if ( sqmArguments == null || sqmArguments.isEmpty() ) {
			return Collections.emptyList();
		}

		final ArrayList<Expression> sqlAstArguments = new ArrayList<>();
		for ( SqmExpression sqmArgument : sqmArguments ) {
			sqlAstArguments.add( (Expression) sqmArgument.accept( this ) );
		}

		return sqlAstArguments;
	}

	@Override
	public Object visitSqlAstFunctionProducer(SqlAstFunctionProducer sqlAstFunctionProducer) {
		shallownessStack.push( Shallowness.FUNCTION );
		try {
			return sqlAstFunctionProducer.convertToSqlAst( this );
		}
		finally {
			shallownessStack.pop();
		}
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// standard functions

	@Override
	public Object visitAbsFunction(SqmAbsFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new AbsFunction( (Expression) function.getArgument().accept( this ) );
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public AvgFunction visitAvgFunction(SqmAvgFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new AvgFunction(
					(Expression) expression.getArgument().accept( this ),
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public Object visitBitLengthFunction(SqmBitLengthFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new BitLengthFunction(
					(Expression) function.getArgument().accept( this ),
					function.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public Object visitCastFunction(SqmCastFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new CastFunction(
					(Expression) expression.getExpressionToCast().accept( this ),
					(BasicValuedExpressableType) expression.getExpressionType(),
					expression.getExplicitSqlCastTarget()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public CountFunction visitCountFunction(SqmCountFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new CountFunction(
					(Expression) expression.getArgument().accept( this ),
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public ConcatFunction visitConcatFunction(SqmConcatFunction function) {
		return new ConcatFunction(
				collectionExpressions( function.getExpressions() ),
				(BasicValuedExpressableType) function.getExpressionType()
		);
	}

	private List<Expression> collectionExpressions(List<SqmExpression> sqmExpressions) {
		if ( sqmExpressions == null || sqmExpressions.isEmpty() ) {
			return Collections.emptyList();
		}

		if ( sqmExpressions.size() == 1 ) {
			return Collections.singletonList( (Expression) sqmExpressions.get( 0 ).accept( this ) );
		}

		return sqmExpressions.stream()
				.map( sqmExpression -> (Expression) sqmExpression.accept( this ) )
				.collect( Collectors.toList() );
	}

	@Override
	public CurrentDateFunction visitCurrentDateFunction(SqmCurrentDateFunction function) {
		return new CurrentDateFunction( function.getExpressionType() );
	}

	@Override
	public CurrentTimeFunction visitCurrentTimeFunction(SqmCurrentTimeFunction function) {
		return new CurrentTimeFunction( function.getExpressionType() );
	}

	@Override
	public CurrentTimestampFunction visitCurrentTimestampFunction(SqmCurrentTimestampFunction function) {
		return new CurrentTimestampFunction( function.getExpressionType() );
	}

	@Override
	public ExtractFunction visitExtractFunction(SqmExtractFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new ExtractFunction(
					(Expression) function.getUnitToExtract().accept( this ),
					(Expression) function.getExtractionSource().accept( this ),
					function.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public CountStarFunction visitCountStarFunction(SqmCountStarFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new CountStarFunction(
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public LengthFunction visitLengthFunction(SqmLengthFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new LengthFunction(
					(Expression) function.getArgument().accept( this ),
					function.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public LocateFunction visitLocateFunction(SqmLocateFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new LocateFunction(
					(Expression) function.getPatternString().accept( this ),
					(Expression) function.getStringToSearch().accept( this ),
					function.getStartPosition() == null
							? null
							: (Expression) function.getStartPosition().accept( this )
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public Object visitLowerFunction(SqmLowerFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new LowerFunction(
					(Expression) function.getArgument().accept( this ),
					function.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public MaxFunction visitMaxFunction(SqmMaxFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new MaxFunction(
					(Expression) expression.getArgument().accept( this ),
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public MinFunction visitMinFunction(SqmMinFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new MinFunction(
					(Expression) expression.getArgument().accept( this ),
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public Object visitModFunction(SqmModFunction function) {
		shallownessStack.push( Shallowness.FUNCTION );

		final Expression dividend = (Expression) function.getDividend().accept( this );
		final Expression divisor = (Expression) function.getDivisor().accept( this );
		try {
			return new ModFunction(
					dividend,
					divisor,
					function.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public SumFunction visitSumFunction(SqmSumFunction expression) {
		shallownessStack.push( Shallowness.FUNCTION );

		try {
			return new SumFunction(
					(Expression) expression.getArgument().accept( this ),
					expression.isDistinct(),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	@Override
	public Object visitUnaryOperationExpression(UnaryOperationSqmExpression expression) {
		shallownessStack.push( Shallowness.NONE );

		try {
			return new UnaryOperation(
					interpret( expression.getOperation() ),
					(Expression) expression.getOperand().accept( this ),
					expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	private UnaryOperation.Operator interpret(UnaryOperationSqmExpression.Operation operation) {
		switch ( operation ) {
			case PLUS: {
				return UnaryOperation.Operator.PLUS;
			}
			case MINUS: {
				return UnaryOperation.Operator.MINUS;
			}
		}

		throw new IllegalStateException( "Unexpected UnaryOperationExpression Operation : " + operation );
	}

	@Override
	public Expression visitBinaryArithmeticExpression(BinaryArithmeticSqmExpression expression) {
		shallownessStack.push( Shallowness.NONE );

		try {
			if ( expression.getOperation() == BinaryArithmeticSqmExpression.Operation.MODULO ) {
				return new NonStandardFunction(
						"mod",
						null, //(BasicType) extractOrmType( expression.getExpressionType() ),
						(Expression) expression.getLeftHandOperand().accept( this ),
						(Expression) expression.getRightHandOperand().accept( this )
				);
			}
			return new BinaryArithmeticExpression(
					interpret( expression.getOperation() ),
					(Expression) expression.getLeftHandOperand().accept( this ),
					(Expression) expression.getRightHandOperand().accept( this ),
					(BasicValuedExpressableType) expression.getExpressionType()
			);
		}
		finally {
			shallownessStack.pop();
		}
	}

	private BinaryArithmeticExpression.Operation interpret(BinaryArithmeticSqmExpression.Operation operation) {
		switch ( operation ) {
			case ADD: {
				return BinaryArithmeticExpression.Operation.ADD;
			}
			case SUBTRACT: {
				return BinaryArithmeticExpression.Operation.SUBTRACT;
			}
			case MULTIPLY: {
				return BinaryArithmeticExpression.Operation.MULTIPLY;
			}
			case DIVIDE: {
				return BinaryArithmeticExpression.Operation.DIVIDE;
			}
			case QUOT: {
				return BinaryArithmeticExpression.Operation.QUOT;
			}
		}

		throw new IllegalStateException( "Unexpected BinaryArithmeticExpression Operation : " + operation );
	}

	@Override
	public CoalesceFunction visitCoalesceFunction(SqmCoalesceFunction expression) {
		final CoalesceFunction result = new CoalesceFunction();
		for ( SqmExpression value : expression.getArguments() ) {
			result.value( (Expression) value.accept( this ) );
		}

		return result;
	}

	@Override
	public CaseSimpleExpression visitSimpleCaseExpression(CaseSimpleSqmExpression expression) {
		final CaseSimpleExpression result = new CaseSimpleExpression(
				expression.getExpressionType(),
				(Expression) expression.getFixture().accept( this )
		);

		for ( CaseSimpleSqmExpression.WhenFragment whenFragment : expression.getWhenFragments() ) {
			result.when(
					(Expression) whenFragment.getCheckValue().accept( this ),
					(Expression) whenFragment.getResult().accept( this )
			);
		}

		result.otherwise( (Expression) expression.getOtherwise().accept( this ) );

		return result;
	}

	@Override
	public CaseSearchedExpression visitSearchedCaseExpression(CaseSearchedSqmExpression expression) {
		final CaseSearchedExpression result = new CaseSearchedExpression( expression.getExpressionType() );

		for ( CaseSearchedSqmExpression.WhenFragment whenFragment : expression.getWhenFragments() ) {
			result.when(
					(Predicate) whenFragment.getPredicate().accept( this ),
					(Expression) whenFragment.getResult().accept( this )
			);
		}

		result.otherwise( (Expression) expression.getOtherwise().accept( this ) );

		return result;
	}

	@Override
	public NullifFunction visitNullifFunction(SqmNullifFunction expression) {
		return new NullifFunction(
				(Expression) expression.getFirstArgument().accept( this ),
				(Expression) expression.getSecondArgument().accept( this ),
				expression.getExpressionType()
		);
	}

	@Override
	public Object visitTrimFunction(SqmTrimFunction expression) {
		return new TrimFunction(
				expression.getSpecification(),
				(Expression) expression.getTrimCharacter().accept( this ),
				(Expression) expression.getSource().accept( this )
		);
	}

	@Override
	public Object visitUpperFunction(SqmUpperFunction sqmFunction) {
		return new UpperFunction(
				(Expression) sqmFunction.getArgument().accept( this ),
				sqmFunction.getExpressionType()
		);

	}

	@Override
	public ConcatFunction visitConcatExpression(ConcatSqmExpression expression) {
		return new ConcatFunction(
				Arrays.asList(
						(Expression)expression.getLeftHandOperand().accept( this ),
						(Expression) expression.getRightHandOperand().accept( this )
				),
				expression.getExpressionType()
		);
	}

//	@Override
//	public Object visitPluralAttributeElementBinding(PluralAttributeElementBinding binding) {
//		final TableGroup resolvedTableGroup = fromClauseIndex.findResolvedTableGroup( binding.getFromElement() );
//
//		return getCurrentDomainReferenceExpressionBuilder().buildPluralAttributeElementReferenceExpression(
//				binding,
//				resolvedTableGroup,
//				PersisterHelper.convert( binding.getNavigablePath() )
//		);
//	}



	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Predicates


	@Override
	public GroupedPredicate visitGroupedPredicate(GroupedSqmPredicate predicate) {
		return new GroupedPredicate ( (Predicate ) predicate.getSubPredicate().accept( this ) );
	}

	@Override
	public Junction visitAndPredicate(AndSqmPredicate predicate) {
		final Junction conjunction = new Junction( Junction.Nature.CONJUNCTION );
		conjunction.add( (Predicate) predicate.getLeftHandPredicate().accept( this ) );
		conjunction.add( (Predicate) predicate.getRightHandPredicate().accept( this ) );
		return conjunction;
	}

	@Override
	public Junction visitOrPredicate(OrSqmPredicate predicate) {
		final Junction disjunction = new Junction( Junction.Nature.DISJUNCTION );
		disjunction.add( (Predicate) predicate.getLeftHandPredicate().accept( this ) );
		disjunction.add( (Predicate) predicate.getRightHandPredicate().accept( this ) );
		return disjunction;
	}

	@Override
	public NegatedPredicate visitNegatedPredicate(NegatedSqmPredicate predicate) {
		return new NegatedPredicate(
				(Predicate) predicate.getWrappedPredicate().accept( this )
		);
	}

	@Override
	public RelationalPredicate visitRelationalPredicate(RelationalSqmPredicate predicate) {
		return new RelationalPredicate(
				interpret( predicate.getOperator() ),
				(Expression) predicate.getLeftHandExpression().accept( this ),
				(Expression) predicate.getRightHandExpression().accept( this )
		);
	}

	private RelationalPredicate.Operator interpret(RelationalPredicateOperator operator) {
		switch ( operator ) {
			case EQUAL: {
				return RelationalPredicate.Operator.EQUAL;
			}
			case NOT_EQUAL: {
				return RelationalPredicate.Operator.NOT_EQUAL;
			}
			case GREATER_THAN_OR_EQUAL: {
				return RelationalPredicate.Operator.GE;
			}
			case GREATER_THAN: {
				return RelationalPredicate.Operator.GT;
			}
			case LESS_THAN_OR_EQUAL: {
				return RelationalPredicate.Operator.LE;
			}
			case LESS_THAN: {
				return RelationalPredicate.Operator.LT;
			}
		}

		throw new IllegalStateException( "Unexpected RelationalPredicate Type : " + operator );
	}

	@Override
	public BetweenPredicate visitBetweenPredicate(BetweenSqmPredicate predicate) {
		return new BetweenPredicate(
				(Expression) predicate.getExpression().accept( this ),
				(Expression) predicate.getLowerBound().accept( this ),
				(Expression) predicate.getUpperBound().accept( this ),
				predicate.isNegated()
		);
	}

	@Override
	public LikePredicate visitLikePredicate(LikeSqmPredicate predicate) {
		final Expression escapeExpression = predicate.getEscapeCharacter() == null
				? null
				: (Expression) predicate.getEscapeCharacter().accept( this );

		return new LikePredicate(
				(Expression) predicate.getMatchExpression().accept( this ),
				(Expression) predicate.getPattern().accept( this ),
				escapeExpression,
				predicate.isNegated()
		);
	}

	@Override
	public NullnessPredicate visitIsNullPredicate(NullnessSqmPredicate predicate) {
		return new NullnessPredicate(
				(Expression) predicate.getExpression().accept( this ),
				predicate.isNegated()
		);
	}

	@Override
	public InListPredicate visitInListPredicate(InListSqmPredicate predicate) {
		final InListPredicate inPredicate = new InListPredicate(
				(Expression) predicate.getTestExpression().accept( this ),
				predicate.isNegated()
		);
		for ( SqmExpression expression : predicate.getListExpressions() ) {
			inPredicate.addExpression( (Expression) expression.accept( this ) );
		}
		return inPredicate;
	}

	@Override
	public InSubQueryPredicate visitInSubQueryPredicate(InSubQuerySqmPredicate predicate) {
		return new InSubQueryPredicate(
				(Expression) predicate.getTestExpression().accept( this ),
				(QuerySpec) predicate.getSubQueryExpression().accept( this ),
				predicate.isNegated()
		);
	}
}