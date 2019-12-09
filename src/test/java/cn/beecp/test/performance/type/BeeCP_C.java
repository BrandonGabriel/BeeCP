package cn.beecp.test.performance.type;

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import cn.beecp.test.Config;

/**
 *  compete mode for BeeCP
 * 
 */
public class BeeCP_C {
	
	public static BeeDataSource  createDataSource() throws Exception{
		BeeDataSourceConfig config =new  BeeDataSourceConfig();
		config.setJdbcUrl(Config.JDBC_URL);
		config.setDriverClassName(Config.JDBC_DRIVER);
		config.setUsername(Config.JDBC_USER);
		config.setPassword(Config.JDBC_PASSWORD);
		config.setMaxActive(Config.POOL_MAX_ACTIVE);
		config.setInitialSize(Config.POOL_INIT_SIZE);
		config.setMaxWait(Config.REQUEST_TIMEOUT);
	
 		config.setConnectionTestSQL("select 1 from dual");
		config.setFairMode(false);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(false);
		
	    return new BeeDataSource(config);
	}  		 
}
