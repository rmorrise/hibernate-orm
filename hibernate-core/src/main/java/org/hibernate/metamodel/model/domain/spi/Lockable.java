/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.metamodel.model.domain.spi;

/**
 * Contract for things that can be locked via a {@link org.hibernate.dialect.lock.LockingStrategy}.
 * <p/>
 * Currently only the root table gets locked, except for the case of HQL and Criteria queries
 * against dialects which do not support either (1) FOR UPDATE OF or (2) support hint locking
 * (in which case *all* queried tables would be locked).
 *
 * @author Steve Ebersole
 * @since 3.2
 */
public interface Lockable<T> extends EntityDescriptor<T> {
	/**
	 * Locks are always applied to the "root table".
	 *
	 * @return The root table name
	 */
	String getRootTableName();

	/**
	 * Get the names of columns on the root table used to persist the identifier.
	 *
	 * @return The root table identifier column names.
	 */
	String[] getRootTableIdentifierColumnNames();

	/**
	 * For versioned entities, get the name of the column (again, expected on the
	 * root table) used to store the version values.
	 *
	 * @return The version column name.
	 */
	String getVersionColumnName();
}
