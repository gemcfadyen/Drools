package gemcfadyen.drools_experimentation.helloworld;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.ArrayList;
import java.util.List;

import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelloWorldTest {
	private StatefulKnowledgeSession statefulSession;
	private KnowledgeRuntimeLogger logger;

	@Before
	public void setupDroolsKnowledgeBase() {
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("hello-world.drl", this.getClass());
		logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulSession, "C:/Users/Georgina/Documents/GitHub/Drools/log/helloworld");
	}

	@After
	public void tearDown(){
		logger.close();
		statefulSession.dispose();
	}
	
	@Test
	public void shouldPrintHelloWorld() {
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		List<String> globals = new ArrayList<String>();
		statefulSession.setGlobal("list", globals);
		statefulSession.insert(message);
		statefulSession.fireAllRules();
				
		assertThat(message.getMessage(), is("Goodbye"));
		assertThat(message.getStatus(), is(1));
		assertThat(globals.size(), is(1));
		assertThat(globals.get(0).toString(), is("hi global"));
	}

}
