/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.NotYetImplementedFor6Exception;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.spi.Loader;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.sql.results.spi.ResultSetMapping;

/**
 * Standard ScrollableResults implementation.
 *
 * @author Gavin King
 */
public class ScrollableResultsImpl extends AbstractScrollableResults implements ScrollableResults {
	private Object[] currentRow;

	/**
	 * Constructs a ScrollableResultsImpl using the specified information.
	 */
	public ScrollableResultsImpl(
			ResultSet rs,
			PreparedStatement ps,
			SharedSessionContractImplementor sess,
			Loader loader,
			QueryOptions queryOptions,
			ResultSetMapping resultSetMapping) {
		super( rs, ps, sess, loader, queryOptions, resultSetMapping );
	}

	@Override
	protected Object[] getCurrentRow() {
		return currentRow;
	}

	@Override
	public boolean scroll(int i) {
		try {
			final boolean result = getResultSet().relative( i );
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using scroll()" );
		}
	}

	protected JDBCException convert(SQLException sqle, String message) {
		return getSession().getFactory().getSQLExceptionHelper().convert( sqle, message );
	}

	@Override
	public boolean first() {
		try {
			final boolean result = getResultSet().first();
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using first()" );
		}
	}

	@Override
	public boolean last() {
		try {
			final boolean result = getResultSet().last();
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using last()" );
		}
	}

	@Override
	public boolean next() {
		try {
			final boolean result = getResultSet().next();
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using next()" );
		}
	}

	@Override
	public boolean previous() {
		try {
			final boolean result = getResultSet().previous();
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using previous()" );
		}
	}

	@Override
	public void afterLast() {
		try {
			getResultSet().afterLast();
		}
		catch (SQLException sqle) {
			throw convert( sqle, "exception calling afterLast()" );
		}
	}

	@Override
	public void beforeFirst() {
		try {
			getResultSet().beforeFirst();
		}
		catch (SQLException sqle) {
			throw convert( sqle, "exception calling beforeFirst()" );
		}
	}

	@Override
	public boolean isFirst() {
		try {
			return getResultSet().isFirst();
		}
		catch (SQLException sqle) {
			throw convert( sqle, "exception calling isFirst()" );
		}
	}

	@Override
	public boolean isLast() {
		try {
			return getResultSet().isLast();
		}
		catch (SQLException sqle) {
			throw convert( sqle, "exception calling isLast()" );
		}
	}

	@Override
	public int getRowNumber() throws HibernateException {
		try {
			return getResultSet().getRow() - 1;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "exception calling getRow()" );
		}
	}

	@Override
	public boolean setRowNumber(int rowNumber) throws HibernateException {
		if ( rowNumber >= 0 ) {
			rowNumber++;
		}

		try {
			final boolean result = getResultSet().absolute( rowNumber );
			prepareCurrentRow( result );
			return result;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not advance using absolute()" );
		}
	}

	private void prepareCurrentRow(boolean underlyingScrollSuccessful) {
//		if ( !underlyingScrollSuccessful ) {
//			currentRow = null;
//			return;
//		}
//
//		final Object result = getLoader().loadSingleRow(
//				getResultSet(),
//				getSession(),
//				getQueryParameters(),
//				false
//		);
//		if ( result != null && result.getClass().isArray() ) {
//			currentRow = (Object[]) result;
//		}
//		else {
//			currentRow = new Object[] {result};
//		}
//
//		if ( getHolderInstantiator() != null ) {
//			currentRow = new Object[] {getHolderInstantiator().instantiate( currentRow )};
//		}
//
//		afterScrollOperation();
		throw new NotYetImplementedFor6Exception(  );
	}

}
