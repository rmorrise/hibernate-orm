/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.internal;

import java.io.Serializable;
import java.util.ArrayList;

import org.hibernate.collection.internal.PersistentIdentifierBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.spi.CollectionPersister;

/**
 * @author Andrea Boriero
 */
public class IdentifierBagType extends AbstractCollectionType {
	public IdentifierBagType(String roleName) {
		super( roleName );
	}

	@Override
	public PersistentCollection instantiate(
			SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
		return new PersistentIdentifierBag( session );
	}

	@Override
	public Object instantiate(int anticipatedSize) {
		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
	}

	@Override
	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
		return new PersistentIdentifierBag( session, (java.util.Collection) collection );
	}

	@Override
	public Class getReturnedClass() {
		return java.util.Collection.class;
	}
}