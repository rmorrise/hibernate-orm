/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.sql.results.internal.JdbcValuesSourceProcessingStateStandardImpl;
import org.hibernate.sql.results.internal.RowProcessingStateStandardImpl;
import org.hibernate.sql.results.internal.values.JdbcValuesSource;
import org.hibernate.sql.results.spi.JdbcValuesSourceProcessingOptions;
import org.hibernate.sql.results.spi.ResultSetMapping;
import org.hibernate.sql.results.spi.RowReader;

/**
 * Base implementation of the ScrollableResults interface.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractScrollableResults<R>
		extends AbstractScrollableResultsDeprecations
		implements ScrollableResultsImplementor<R> {
	private final JdbcValuesSource jdbcValuesSource;
	private final JdbcValuesSourceProcessingOptions processingOptions;
	private final JdbcValuesSourceProcessingStateStandardImpl jdbcValuesSourceProcessingState;
	private final RowProcessingStateStandardImpl rowProcessingState;
	private final RowReader<R> rowReader;
	private final SharedSessionContractImplementor persistenceContext;

	private boolean closed;

	public AbstractScrollableResults(
			JdbcValuesSource jdbcValuesSource,
			JdbcValuesSourceProcessingOptions processingOptions,
			JdbcValuesSourceProcessingStateStandardImpl jdbcValuesSourceProcessingState,
			RowProcessingStateStandardImpl rowProcessingState,
			RowReader<R> rowReader,
			SharedSessionContractImplementor persistenceContext) {
		this.jdbcValuesSource = jdbcValuesSource;
		this.processingOptions = processingOptions;
		this.jdbcValuesSourceProcessingState = jdbcValuesSourceProcessingState;
		this.rowProcessingState = rowProcessingState;
		this.rowReader = rowReader;
		this.persistenceContext = persistenceContext;
	}

	// todo (6.0) : re-look at the arguments passed here.  What is really needed?
	//		and relatedly, what is exposed


	@Override
	protected ResultSetMapping getResultSetMapping() {
		return jdbcValuesSource.getResultSetMapping();
	}

	protected abstract R getCurrentRow();

	protected JdbcValuesSource getJdbcValuesSource() {
		return jdbcValuesSource;
	}

	protected JdbcValuesSourceProcessingOptions getProcessingOptions() {
		return processingOptions;
	}

	protected JdbcValuesSourceProcessingStateStandardImpl getJdbcValuesSourceProcessingState() {
		return jdbcValuesSourceProcessingState;
	}

	protected RowProcessingStateStandardImpl getRowProcessingState() {
		return rowProcessingState;
	}

	protected RowReader<R> getRowReader() {
		return rowReader;
	}

	protected SharedSessionContractImplementor getPersistenceContext() {
		return persistenceContext;
	}

	protected void afterScrollOperation() {
		getPersistenceContext().afterScrollOperation();
	}

	@Override
	public final void close() {
		if ( this.closed ) {
			// noop if already closed
			return;
		}

		getJdbcValuesSource().finishUp();
		getPersistenceContext().getJdbcCoordinator().afterStatementExecution();

		this.closed = true;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public int getNumberOfTypes() {
		return rowReader.getNumberOfResults();
	}

	@Override
	public final R get() throws HibernateException {
		if ( closed ) {
			throw new IllegalStateException( "ScrollableResults is closed" );
		}
		return getCurrentRow();
	}
}
