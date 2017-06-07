/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.metamodel.model.relational.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.metamodel.model.relational.spi.DatabaseModel;
import org.hibernate.metamodel.model.relational.spi.Namespace;

/**
 * @author Steve Ebersole
 */
public class DatabaseModelImpl implements DatabaseModel {
	private final List<Namespace> namespaces = new ArrayList<>();
	private Namespace defautlNamespace;

	private final JdbcEnvironment getJdbcEnvironment;
	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<>(  );

	public DatabaseModelImpl(JdbcEnvironment getJdbcEnvironment) {
		this.getJdbcEnvironment = getJdbcEnvironment;
	}

	@Override
	public Collection<Namespace> getNamespaces() {
		return namespaces;
	}

	@Override
	public Namespace getDefaultNamespace() {
		return defautlNamespace;
	}

	@Override
	public JdbcEnvironment getJdbcEnvironment() {
		return getJdbcEnvironment;
	}

	@Override
	public Collection<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
		return auxiliaryDatabaseObjects;
	}

	public void addNamespace(Namespace namespace) {
		namespaces.add( namespace );
	}

	public void setDefaultNamespace(Namespace namespace){
		defautlNamespace = namespace;
	}

	public void setAuxiliaryDatabaseObjects(List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects){
		this.auxiliaryDatabaseObjects = auxiliaryDatabaseObjects;
	}
}