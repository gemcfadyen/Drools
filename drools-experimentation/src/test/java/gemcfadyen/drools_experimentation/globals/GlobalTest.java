package gemcfadyen.drools_experimentation.globals;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//Using globals on RHS and LHS of the rules
public class GlobalTest {
	private StatefulKnowledgeSession workingMemory;

	@Before
	public void setup() {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		ClassPathResource resource = new ClassPathResource("../globals.drl",
				this.getClass());
		knowledgeBuilder.add(resource, ResourceType.DRL);

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

		knowledgeBase.addKnowledgePackages(knowledgeBuilder
				.getKnowledgePackages());
		workingMemory = knowledgeBase.newStatefulKnowledgeSession();
	}

	@After
	public void tearDown() {
		if (workingMemory != null) {
			workingMemory.dispose();
		}
	}

	@Test
	public void shouldSuccessfullyChangeAGlobalsValueInTheDrl() {
		GlobalResultObject result = new GlobalResultObject();
		ArrayList globalList = new ArrayList();
		workingMemory.setGlobal("result", result);
		workingMemory.setGlobal("list", globalList);
		workingMemory.fireAllRules();

		assertThat(result.getMessage(), is("Global message has been set"));
		assertThat(globalList.size(), is(0));

	}

	@Test
	public void shouldSetTheMessageOnTheGlobalIndicatingAFlagHasBeenSet() {
		GlobalResultObject result = new GlobalResultObject();
		result.setIsSet(10);

		ArrayList<GlobalResultObject> globalList = new ArrayList<GlobalResultObject>();

		workingMemory.setGlobal("result", result);
		workingMemory.setGlobal("list", globalList);
		workingMemory.insert(result);
		workingMemory.fireAllRules();

		assertThat(result.getMessage(), is("Global has a flag set so no message is returned"));
		assertThat(globalList.size(), is(1));
	}

}
