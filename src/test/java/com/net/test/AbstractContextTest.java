package com.net.test;

import com.net.util.DataGenerationUtil;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:mainConfig/spring-common.xml" })
@Transactional(transactionManager = "txManager")
@Rollback
public class AbstractContextTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	protected static DataGenerationUtil dbDataGenerationUtil;

}
