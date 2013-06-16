package gemcfadyen.drools_experimentation.dsl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class CheeseDslTest {
	private StatefulKnowledgeSession knowledgeSession;

	@Before
	public void setup() {

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource dslResource = ResourceFactory.newClassPathResource("../cheese-rules.dsl", this.getClass());
		knowledgeBuilder.add(dslResource, ResourceType.DSL);
		Resource dslrResource = ResourceFactory.newClassPathResource("../cheese-rules.dslr", this.getClass());
		knowledgeBuilder.add(dslrResource, ResourceType.DSLR);

		if (knowledgeBuilder.hasErrors()) {
			System.out.println(knowledgeBuilder.getErrors());
		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder
				.getKnowledgePackages());

		knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
	}

	@Test
	public void shouldMatchStiltonRule() {
		Cheese stilton = new Cheese();
		stilton.setAge(2);
		stilton.setType("Stilton");
		stilton.setCountry("England");
		knowledgeSession.insert(stilton);
		knowledgeSession.fireAllRules();

		assertThat(stilton.getName(), is("Fresh Stilton"));

	}

}
