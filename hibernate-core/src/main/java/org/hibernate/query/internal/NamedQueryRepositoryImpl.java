/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Incubating;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.query.spi.QueryEngine;
import org.hibernate.query.spi.ResultSetMappingDefinition;

import org.jboss.logging.Logger;

/**
 * @author Steve Ebersole
 */
@Incubating
public class NamedQueryRepositoryImpl implements NamedQueryRepository {
	private static final Logger log = Logger.getLogger( NamedQueryRepository.class );

	private final Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap;

	private volatile Map<String, NamedQueryDefinition> namedQueryDefinitionMap;
	private volatile Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap;
	private volatile Map<String, ProcedureCallMemento> procedureCallMementoMap;

	public NamedQueryRepositoryImpl(
			Iterable<NamedQueryDefinition> namedQueryDefinitions,
			Iterable<NamedSQLQueryDefinition> namedSqlQueryDefinitions,
			Iterable<ResultSetMappingDefinition> namedSqlResultSetMappings,
			Map<String, ProcedureCallMemento> namedProcedureCalls) {
		final HashMap<String, NamedQueryDefinition> namedQueryDefinitionMap = new HashMap<>();
		for ( NamedQueryDefinition namedQueryDefinition : namedQueryDefinitions ) {
			namedQueryDefinitionMap.put( namedQueryDefinition.getName(), namedQueryDefinition );
		}
		this.namedQueryDefinitionMap = Collections.unmodifiableMap( namedQueryDefinitionMap );


		final HashMap<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap = new HashMap<>();
		for ( NamedSQLQueryDefinition namedSqlQueryDefinition : namedSqlQueryDefinitions ) {
			namedSqlQueryDefinitionMap.put( namedSqlQueryDefinition.getName(), namedSqlQueryDefinition );
		}
		this.namedSqlQueryDefinitionMap = Collections.unmodifiableMap( namedSqlQueryDefinitionMap );

		final HashMap<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap = new HashMap<>();
		for ( ResultSetMappingDefinition resultSetMappingDefinition : namedSqlResultSetMappings ) {
			namedSqlResultSetMappingMap.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
		}
		this.namedSqlResultSetMappingMap = Collections.unmodifiableMap( namedSqlResultSetMappingMap );
		this.procedureCallMementoMap = Collections.unmodifiableMap( namedProcedureCalls );
	}

	public NamedQueryRepositoryImpl(
			Map<String,NamedQueryDefinition> namedQueryDefinitionMap,
			Map<String,NamedSQLQueryDefinition> namedSqlQueryDefinitionMap,
			Map<String,ResultSetMappingDefinition> namedSqlResultSetMappingMap,
			Map<String, ProcedureCallMemento> namedProcedureCallMap) {
		this.namedQueryDefinitionMap = Collections.unmodifiableMap( namedQueryDefinitionMap );
		this.namedSqlQueryDefinitionMap = Collections.unmodifiableMap( namedSqlQueryDefinitionMap );
		this.namedSqlResultSetMappingMap = Collections.unmodifiableMap( namedSqlResultSetMappingMap );
		this.procedureCallMementoMap = Collections.unmodifiableMap( namedProcedureCallMap );
	}


	@Override public NamedQueryDefinition getNamedQueryDefinition(String queryName) {
		return namedQueryDefinitionMap.get( queryName );
	}

	@Override public NamedSQLQueryDefinition getNamedSQLQueryDefinition(String queryName) {
		return namedSqlQueryDefinitionMap.get( queryName );
	}

	@Override public ProcedureCallMemento getNamedProcedureCallMemento(String name) {
		return procedureCallMementoMap.get( name );
	}

	@Override public ResultSetMappingDefinition getResultSetMappingDefinition(String mappingName) {
		return namedSqlResultSetMappingMap.get( mappingName );
	}

	@Override public synchronized void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
		if ( NamedSQLQueryDefinition.class.isInstance( definition ) ) {
			throw new IllegalArgumentException( "NamedSQLQueryDefinition instance incorrectly passed to registerNamedQueryDefinition" );
		}

		if ( ! name.equals( definition.getName() ) ) {
			definition = definition.makeCopy( name );
		}

		final Map<String, NamedQueryDefinition> copy = CollectionHelper.makeCopy( namedQueryDefinitionMap );
		final NamedQueryDefinition previous = copy.put( name, definition );
		if ( previous != null ) {
			log.debugf(
					"registering named query definition [%s] overriding previously registered definition [%s]",
					name,
					previous
			);
		}

		this.namedQueryDefinitionMap = Collections.unmodifiableMap( copy );
	}

	@Override public synchronized void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
		if ( ! name.equals( definition.getName() ) ) {
			definition = definition.makeCopy( name );
		}

		final Map<String, NamedSQLQueryDefinition> copy = CollectionHelper.makeCopy( namedSqlQueryDefinitionMap );
		final NamedQueryDefinition previous = copy.put( name, definition );
		if ( previous != null ) {
			log.debugf(
					"registering named SQL query definition [%s] overriding previously registered definition [%s]",
					name,
					previous
			);
		}

		this.namedSqlQueryDefinitionMap = Collections.unmodifiableMap( copy );
	}

	@Override public synchronized void registerNamedProcedureCallMemento(String name, ProcedureCallMemento memento) {
		final Map<String, ProcedureCallMemento> copy = CollectionHelper.makeCopy( procedureCallMementoMap );
		final ProcedureCallMemento previous = copy.put( name, memento );
		if ( previous != null ) {
			log.debugf(
					"registering named procedure call definition [%s] overriding previously registered definition [%s]",
					name,
					previous
			);
		}

		this.procedureCallMementoMap = Collections.unmodifiableMap( copy );
	}

	@Override
	public Map<String,HibernateException> checkNamedQueries(QueryEngine queryPlanCache) {
		Map<String,HibernateException> errors = new HashMap<>();

		// Check named HQL queries
		log.debugf( "Checking %s named HQL queries", namedQueryDefinitionMap.size() );
		for ( NamedQueryDefinition namedQueryDefinition : namedQueryDefinitionMap.values() ) {
			//		1) build QueryOptions reference from `namedQueryDefinition`
			//		2) build `ParsingContext` from the QueryEngine's SessionFactory
			//		3) need to resolve the `SelectQueryPlan` - roughly the logic in `NativeQueryImpl#resolveSelectQueryPlan`
			//		4)
			// this will throw an error if there's something wrong.
			try {
				log.debugf( "Checking named query: %s", namedQueryDefinition.getName() );
				//TODO: BUG! this currently fails for named queries for non-POJO entities
				queryPlanCache.getHQLQueryPlan( namedQueryDefinition.getQueryString(), false, Collections.EMPTY_MAP );
			}
			catch ( HibernateException e ) {
				errors.put( namedQueryDefinition.getName(), e );
			}


		}

		// Check native-sql queries
		log.debugf( "Checking %s named SQL queries", namedSqlQueryDefinitionMap.size() );
		for ( NamedSQLQueryDefinition namedSQLQueryDefinition : namedSqlQueryDefinitionMap.values() ) {
			// this will throw an error if there's something wrong.
			try {
				log.debugf( "Checking named SQL query: %s", namedSQLQueryDefinition.getName() );
				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
				// currently not doable though because of the resultset-ref stuff...
				NativeSQLQuerySpecification spec;
				if ( namedSQLQueryDefinition.getResultSetRef() != null ) {
					ResultSetMappingDefinition definition = getResultSetMappingDefinition( namedSQLQueryDefinition.getResultSetRef() );
					if ( definition == null ) {
						throw new MappingException( "Unable to find resultset-ref definition: " + namedSQLQueryDefinition.getResultSetRef() );
					}
					spec = new NativeSQLQuerySpecification(
							namedSQLQueryDefinition.getQueryString(),
							definition.getQueryReturns(),
							namedSQLQueryDefinition.getQuerySpaces()
					);
				}
				else {
					spec =  new NativeSQLQuerySpecification(
							namedSQLQueryDefinition.getQueryString(),
							namedSQLQueryDefinition.getQueryResultBuilders(),
							namedSQLQueryDefinition.getQuerySpaces()
					);
				}
				queryPlanCache.getNativeSQLQueryPlan( spec );
			}
			catch ( HibernateException e ) {
				errors.put( namedSQLQueryDefinition.getName(), e );
			}
		}

		return errors;
	}
}