package gemcfadyen.drools_experimentation.functions;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class CallAFunctionInDrlTest {
	private StatefulKnowledgeSession statefulSession;
	
	@Before
	public void setup(){
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("function.drl", this.getClass());
	}
	
	@Test
	public void shouldCallTheHelloWorldFunctionDefinedInTheDrl() {
	    FunctionResult message = new FunctionResult();
	    statefulSession.setGlobal("message", message);
	    statefulSession.fireAllRules();

	    assertThat(message.getMessage(), is("Hello GeorginaisNameJoJo is true"));
	}  

}
