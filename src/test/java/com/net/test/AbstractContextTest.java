package com.net.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.net.util.DataGenerationUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mainConfig/Spring-common.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
public class AbstractContextTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	protected static DataGenerationUtil dbDataGenerationUtil;

}
