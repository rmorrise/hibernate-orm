/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.NotYetImplementedFor6Exception;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.spi.Loader;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.sql.results.spi.ResultSetMapping;
import org.hibernate.type.Type;
import org.hibernate.type.spi.StandardSpiBasicTypes;

/**
 * Base implementation of the ScrollableResults interface.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractScrollableResults implements ScrollableResultsImplementor {
	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractScrollableResults.class );

	private final ResultSet resultSet;
	private final PreparedStatement ps;
	private final SharedSessionContractImplementor session;
	private final Loader loader;
	private final QueryOptions queryOptions;
	private final ResultSetMapping resultSetMapping;
	private boolean closed;

	protected AbstractScrollableResults(
			ResultSet rs,
			PreparedStatement ps,
			SharedSessionContractImplementor sess,
			Loader loader,
			QueryOptions queryOptions,
			ResultSetMapping resultSetMapping) {
		this.resultSet = rs;
		this.ps = ps;
		this.session = sess;
		this.loader = loader;
		this.queryOptions = queryOptions;
		this.resultSetMapping = resultSetMapping;
	}

	protected abstract Object[] getCurrentRow();

	protected ResultSet getResultSet() {
		return resultSet;
	}

	protected PreparedStatement getPs() {
		return ps;
	}

	protected SharedSessionContractImplementor getSession() {
		return session;
	}

	protected Loader getLoader() {
		return loader;
	}

	@Override
	public final void close() {
		if ( this.closed ) {
			// noop if already closed
			return;
		}

		// not absolutely necessary, but does help with aggressive release
		//session.getJDBCContext().getConnectionManager().closeQueryStatement( ps, resultSet );
		session.getJdbcCoordinator().getResourceRegistry().release( ps );
		session.getJdbcCoordinator().afterStatementExecution();
		try {
			session.getPersistenceContext().getLoadContexts().cleanup( resultSet );
		}
		catch (Throwable ignore) {
			// ignore this error for now
			if ( LOG.isTraceEnabled() ) {
				LOG.tracev( "Exception trying to cleanup load context : {0}", ignore.getMessage() );
			}
		}

		this.closed = true;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public int getNumberOfTypes() {
		return resultSetMapping.getQueryResults().size();
	}

	@Override
	public final Object[] get() throws HibernateException {
		if ( closed ) {
			throw new IllegalStateException( "ScrollableResults is closed" );
		}
		return getCurrentRow();
	}

	@Override
	public final Object get(int col) throws HibernateException {
		if ( closed ) {
			throw new IllegalStateException( "ScrollableResults is closed" );
		}
		return getCurrentRow()[col];
	}

	/**
	 * Check that the requested type is compatible with the result type, and
	 * return the column value.  This version makes sure the the classes
	 * are identical.
	 *
	 * @param col the column
	 * @param returnType a "final" type
	 */
	protected final Object getFinal(int col, Type returnType) throws HibernateException {
		if ( closed ) {
			throw new IllegalStateException( "ScrollableResults is closed" );
		}

//		if ( holderInstantiator != null ) {
//			throw new HibernateException( "query specifies a holder class" );
//		}
//
//		if ( returnType.getJavaTypeDescriptor().getJavaType() == types[col].getJavaTypeDescriptor().getJavaType() ) {
//			return get( col );
//		}
//		else {
//			return throwInvalidColumnTypeException( col, types[col], returnType );
//		}
		throw new NotYetImplementedFor6Exception(  );
	}

	/**
	 * Check that the requested type is compatible with the result type, and
	 * return the column value.  This version makes sure the the classes
	 * are "assignable".
	 *
	 * @param col the column
	 * @param returnType any type
	 */
	protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
		if ( closed ) {
			throw new IllegalStateException( "ScrollableResults is closed" );
		}

//		if ( holderInstantiator != null ) {
//			throw new HibernateException( "query specifies a holder class" );
//		}
//
//		if ( returnType.getJavaTypeDescriptor().getJavaType().isAssignableFrom( types[col].getJavaTypeDescriptor().getJavaType() ) ) {
//			return get( col );
//		}
//		else {
//			return throwInvalidColumnTypeException( col, types[col], returnType );
//		}
		throw new NotYetImplementedFor6Exception(  );
	}

	@Override
	public final BigDecimal getBigDecimal(int col) throws HibernateException {
		return (BigDecimal) getFinal( col, StandardSpiBasicTypes.BIG_DECIMAL );
	}

	@Override
	public final BigInteger getBigInteger(int col) throws HibernateException {
		return (BigInteger) getFinal( col, StandardSpiBasicTypes.BIG_INTEGER );
	}

	@Override
	public final byte[] getBinary(int col) throws HibernateException {
		return (byte[]) getFinal( col, StandardSpiBasicTypes.BINARY );
	}

	@Override
	public final String getText(int col) throws HibernateException {
		return (String) getFinal( col, StandardSpiBasicTypes.TEXT );
	}

	@Override
	public final Blob getBlob(int col) throws HibernateException {
		return (Blob) getNonFinal( col, StandardSpiBasicTypes.BLOB );
	}

	@Override
	public final Clob getClob(int col) throws HibernateException {
		return (Clob) getNonFinal( col, StandardSpiBasicTypes.CLOB );
	}

	@Override
	public final Boolean getBoolean(int col) throws HibernateException {
		return (Boolean) getFinal( col, StandardSpiBasicTypes.BOOLEAN );
	}

	@Override
	public final Byte getByte(int col) throws HibernateException {
		return (Byte) getFinal( col, StandardSpiBasicTypes.BYTE );
	}

	@Override
	public final Character getCharacter(int col) throws HibernateException {
		return (Character) getFinal( col, StandardSpiBasicTypes.CHARACTER );
	}

	@Override
	public final Date getDate(int col) throws HibernateException {
		return (Date) getNonFinal( col, StandardSpiBasicTypes.TIMESTAMP );
	}

	@Override
	public final Calendar getCalendar(int col) throws HibernateException {
		return (Calendar) getNonFinal( col, StandardSpiBasicTypes.CALENDAR );
	}

	@Override
	public final Double getDouble(int col) throws HibernateException {
		return (Double) getFinal( col, StandardSpiBasicTypes.DOUBLE );
	}

	@Override
	public final Float getFloat(int col) throws HibernateException {
		return (Float) getFinal( col, StandardSpiBasicTypes.FLOAT );
	}

	@Override
	public final Integer getInteger(int col) throws HibernateException {
		return (Integer) getFinal( col, StandardSpiBasicTypes.INTEGER );
	}

	@Override
	public final Long getLong(int col) throws HibernateException {
		return (Long) getFinal( col, StandardSpiBasicTypes.LONG );
	}

	@Override
	public final Short getShort(int col) throws HibernateException {
		return (Short) getFinal( col, StandardSpiBasicTypes.SHORT );
	}

	@Override
	public final String getString(int col) throws HibernateException {
		return (String) getFinal( col, StandardSpiBasicTypes.STRING );
	}

	@Override
	public final Locale getLocale(int col) throws HibernateException {
		return (Locale) getFinal( col, StandardSpiBasicTypes.LOCALE );
	}

	@Override
	public final TimeZone getTimeZone(int col) throws HibernateException {
		return (TimeZone) getNonFinal( col, StandardSpiBasicTypes.TIMEZONE );
	}

	@Override
	public final Type getType(int i) {
		//return types[i];
		throw new org.hibernate.cfg.NotYetImplementedException(  );
	}

	private Object throwInvalidColumnTypeException(
			int i,
			Type type,
			Type returnType) throws HibernateException {
		throw new HibernateException(
				"incompatible column types: " +
						type.getJavaTypeDescriptor().getTypeName() +
						", " +
						returnType.getJavaTypeDescriptor().getTypeName()
		);
	}

	protected void afterScrollOperation() {
		session.afterScrollOperation();
	}
}
