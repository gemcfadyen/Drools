package gemcfadyen.drools_experimentation.helloworld;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class HelloWorldTest {
	private StatefulKnowledgeSession statefulSession;
	private KnowledgeRuntimeLogger logger;

	@Before
	public void setupDroolsKnowledgeBase() {
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("hello-world.drl");
		logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulSession, "C:/Users/Georgina/Documents/GitHub/Drools/log/helloworld");
	}

	
	@Test
	public void shouldPrintHelloWorld() {
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		
		statefulSession.insert(message);
		statefulSession.fireAllRules();
		logger.close();
		statefulSession.dispose();
		
		assertThat(message.getMessage(), is("Goodbye"));
		assertThat(message.getStatus(), is(1));
	}

}
