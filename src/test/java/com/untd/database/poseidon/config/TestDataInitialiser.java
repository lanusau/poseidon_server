package com.untd.database.poseidon.config;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.Resource;

/**
 * 
 * This class, when put into service will load test data using DBUnit
 *
 */
public class TestDataInitialiser  {
	
	private DataSource dataSource;		
	private Resource testDataResource;
	
	@PostConstruct
	public void load() throws SQLException, DatabaseUnitException, IOException {
		DatabaseDataSourceConnection connection = new DatabaseDataSourceConnection(dataSource);
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
	    IDataSet dataSet = new FlatXmlDataSetBuilder().build(testDataResource.getFile());
	    ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
	    
	    // Replace relative dates with current values
	    rDataSet.addReplacementObject("[past_timestamp]", (System.currentTimeMillis()/1000)-3600);
	    rDataSet.addReplacementObject("[future_timestamp]", (System.currentTimeMillis()/1000)+3600);
	    DatabaseOperation.CLEAN_INSERT.execute(connection, rDataSet);
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setTestDataResource(final Resource testDataResource) {
	    this.testDataResource = testDataResource;
	}
}