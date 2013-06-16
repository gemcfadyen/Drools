package gemcfadyen.drools_experimentation.dsl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

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

public class DomainSpecificLanguageTest {
	// The DSL is like a translator - translates sentences to rules 
	// The DSLR  contains the sentences

	// Note: The translation process happens in memory and no .drl file is
	// physically stored
	// The knowledgeBuilder does the translation and the
	// generated DRL is then used as normal

	private StatefulKnowledgeSession knowledgeSession;

	@Before
	public void setup() {

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource dslResource = ResourceFactory.newClassPathResource("../musician-rules.dsl", this.getClass());
		knowledgeBuilder.add(dslResource, ResourceType.DSL);
		Resource dslrResource = ResourceFactory.newClassPathResource("../musician-rules.dslr", this.getClass());
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
	public void shouldPrintOutViolinMessage() {
		Musician violinist = new Musician();
		violinist.setInstrument("violin");
		violinist.setAge(22);
		violinist.setLocation("Scotland");

		List<Musician> availableMusicians = new ArrayList<Musician>();

		knowledgeSession.setGlobal("availableMusicians", availableMusicians);
		knowledgeSession.insert(violinist);
		knowledgeSession.fireAllRules();

		assertThat(availableMusicians.size(), is(1));
	}

	@Test
	public void shouldPrintOutTromboneMessage() {
		Musician trombonist = new Musician();
		trombonist.setInstrument("Trombone");
		trombonist.setAge(33);
		trombonist.setLocation("York");

		Musician triangle = new Musician();
		triangle.setInstrument("Triangle");
		triangle.setAge(22);
		triangle.setLocation("Turkey");

		List<Musician> availableMusicians = new ArrayList<Musician>();

		knowledgeSession.setGlobal("availableMusicians", availableMusicians);
		knowledgeSession.insert(trombonist);
		knowledgeSession.insert(triangle);
		knowledgeSession.fireAllRules();

		assertThat(availableMusicians.size(), is(1));
	}

	@Test
	public void shouldResultInNoAvailableMusicians() {
		Musician triangle = new Musician();
		triangle.setInstrument("Triangle");
		triangle.setAge(22);
		triangle.setLocation("Turkey");

		List<Musician> availableMusicians = new ArrayList<Musician>();

		knowledgeSession.setGlobal("availableMusicians", availableMusicians);
		knowledgeSession.insert(triangle);
		knowledgeSession.fireAllRules();

		assertThat(availableMusicians.size(), is(0));
	}
}
