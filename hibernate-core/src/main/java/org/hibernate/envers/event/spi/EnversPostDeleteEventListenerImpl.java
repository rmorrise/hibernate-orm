/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.event.spi;

import org.hibernate.envers.boot.AuditService;
import org.hibernate.envers.internal.synchronization.AuditProcess;
import org.hibernate.envers.internal.synchronization.work.AuditWorkUnit;
import org.hibernate.envers.internal.synchronization.work.DelWorkUnit;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.metamodel.model.domain.spi.EntityDescriptor;

/**
 * Envers-specific entity (post) deletion event listener
 *
 * @author Adam Warski (adam at warski dot org)
 * @author HernпїЅn Chanfreau
 * @author Steve Ebersole
 * @author Chris Cranford
 */
public class EnversPostDeleteEventListenerImpl extends BaseEnversEventListener implements PostDeleteEventListener {
	public EnversPostDeleteEventListenerImpl(AuditService auditService) {
		super( auditService );
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		final String entityName = event.getPersister().getEntityName();

		if ( getAuditService().getEntityBindings().isVersioned( entityName ) ) {
			checkIfTransactionInProgress( event.getSession() );

			final AuditProcess auditProcess = getAuditService().getAuditProcess( event.getSession() );

			final AuditWorkUnit workUnit = new DelWorkUnit(
					event.getSession(),
					event.getPersister().getEntityName(),
					getAuditService(),
					event.getId(),
					event.getPersister(),
					event.getDeletedState()
			);
			auditProcess.addWorkUnit( workUnit );

			if ( workUnit.containsWork() ) {
				generateBidirectionalCollectionChangeWorkUnits(
						auditProcess,
						event.getPersister(),
						entityName,
						null,
						event.getDeletedState(),
						event.getSession()
				);
			}
		}
	}

	@Override
	public boolean requiresPostCommitHanding(EntityDescriptor persister) {
		return getAuditService().getEntityBindings().isVersioned( persister.getEntityName() );
	}
}
