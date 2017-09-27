///*
// * Hibernate, Relational Persistence for Idiomatic Java
// *
// * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
// * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
// */
//package org.hibernate.test.schemaupdate.inheritance;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.util.EnumSet;
//
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.boot.spi.MetadataImplementor;
//import org.hibernate.cfg.Environment;
//import org.hibernate.tool.hbm2ddl.SchemaUpdate;
//import org.hibernate.tool.schema.TargetType;
//
//import org.hibernate.testing.TestForIssue;
//import org.hibernate.testing.junit4.BaseUnitTestCase;
//import org.junit.Test;
//
//import static org.hamcrest.core.Is.is;
//import static org.junit.Assert.assertThat;
//
///**
// * @author Andrea Boriero
// */
//public class ForeignKeyNameTest extends BaseUnitTestCase {
//
//	@Test
//	@TestForIssue(jiraKey = "HHH-10169")
//	public void testJoinedSubclassForeignKeyNameIsNotAutoGeneratedWhenProvided() throws Exception {
//		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
//				.applySetting( Environment.HBM2DDL_AUTO, "none" )
//				.build();
//		try {
//			File output = File.createTempFile( "update_script", ".sql" );
//			output.deleteOnExit();
//
//			final MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
//					.addResource( "org/hibernate/test/schemaupdate/inheritance/Employee.hbm.xml" )
//					.addResource( "org/hibernate/test/schemaupdate/inheritance/Person.hbm.xml" )
//					.addResource( "org/hibernate/test/schemaupdate/inheritance/Manager.hbm.xml" )
//					.addResource( "org/hibernate/test/schemaupdate/inheritance/Payment.hbm.xml" )
//					.buildMetadata();
//			metadata.validate();
//
//			new SchemaUpdate()
//					.setHaltOnError( true )
//					.setOutputFile( output.getAbsolutePath() )
//					.setDelimiter( ";" )
//					.setFormat( true )
//					.execute( EnumSet.of( TargetType.SCRIPT ), metadata );
//
//			String fileContent = new String( Files.readAllBytes( output.toPath() ) );
//			assertThat( fileContent.toLowerCase().contains( "fk_emp_per" ), is( true ) );
//		}
//		finally {
//			StandardServiceRegistryBuilder.destroy( ssr );
//		}
//	}
//
//	@Test
//	public void testSubclassForeignKeyNameIsNotAutoGeneratedWhenProvided() throws Exception {
//		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
//				.applySetting( Environment.HBM2DDL_AUTO, "none" )
//				.build();
//		try {
//			File output = File.createTempFile( "update_script", ".sql" );
//			output.deleteOnExit();
//
//			final MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
//					.addResource( "org/hibernate/test/schemaupdate/inheritance/Payment.hbm.xml" )
//					.buildMetadata();
//			metadata.validate();
//
//			new SchemaUpdate()
//					.setHaltOnError( true )
//					.setOutputFile( output.getAbsolutePath() )
//					.setDelimiter( ";" )
//					.setFormat( true )
//					.execute( EnumSet.of( TargetType.SCRIPT ), metadata );
//
//			String fileContent = new String( Files.readAllBytes( output.toPath() ) );
//			assertThat( fileContent.toLowerCase().contains( "fk_cc_pay" ), is( true ) );
//		}
//		finally {
//			StandardServiceRegistryBuilder.destroy( ssr );
//		}
//	}
//}
