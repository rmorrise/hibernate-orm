/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import org.hibernate.EntityMode;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeRepresentationStrategy;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeRepresentationResolver;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Define a tuplizer for an entity or a component.
 *
 * @author Emmanuel Bernard
 *
 * @deprecated as of 6.0 with no replacement.  See
 * {@link org.hibernate.metamodel.model.domain.RepresentationMode},
 * {@link ManagedTypeRepresentationStrategy} and
 * {@link ManagedTypeRepresentationResolver}
 * for further details
 */
@java.lang.annotation.Target( {TYPE, FIELD, METHOD} )
@Retention( RUNTIME )
@Repeatable(Tuplizers.class)
@Deprecated
public @interface Tuplizer {
	/**
	 * Tuplizer implementation.
	 */
	Class impl();

	/**
	 * either pojo, dynamic-map or dom4j.
	 * @deprecated should use #entityModeType instead
	 */
	@Deprecated
	String entityMode() default "pojo";

	/**
	 * The entity mode.
	 */
	EntityMode entityModeType() default EntityMode.POJO;
}
