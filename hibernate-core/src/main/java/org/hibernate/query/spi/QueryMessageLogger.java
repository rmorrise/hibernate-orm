/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.spi;

import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.log.DeprecationLogger;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

import static org.jboss.logging.Logger.Level.ERROR;

/**
 * Consolidated message-logging regarding query processing.
 *
 * @author Steve Ebersole
 */
@MessageLogger( projectCode = "HHH" )
@ValidIdRange( min = 90000001, max = 90001000 )
public interface QueryMessageLogger {
	QueryMessageLogger QUERY_LOGGER = Logger.getMessageLogger(
			QueryMessageLogger.class,
			"org.hibernate.orm.query"
	);


	@LogMessage(level = ERROR)
	@Message(value = "Error in named query: %s", id = 177)
	void namedQueryError(String queryName, @Cause HibernateException e);
}
