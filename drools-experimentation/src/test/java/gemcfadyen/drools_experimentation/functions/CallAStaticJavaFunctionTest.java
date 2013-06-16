package gemcfadyen.drools_experimentation.functions;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CallAStaticJavaFunctionTest {

	private StatefulKnowledgeSession statefulSession;

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile(
						"function-from-java.drl", this.getClass());
	}

	public void shouldCallTheHelloWorldFunctionDefiniedIndDrl() {
		FunctionResult message = new FunctionResult();
		statefulSession.setGlobal("message", message);
		statefulSession.fireAllRules();
		assertThat(message.getMessage(), is("isNameJoJo is trueHello Georgina"));

	}


	@Test
	public void shouldCallTheHelloWorldFunctionDefinedInJavaHelperClass() {
		FunctionResult message = new FunctionResult();
		statefulSession.setGlobal("message", message);
		statefulSession.fireAllRules();
		assertThat(message.getMessage(), is("Have such a cheery day Georgina"));
	}

}
