package com.net.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/config/jdbc.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
public class AbstractContextTest extends AbstractTransactionalJUnit4SpringContextTests {

}
