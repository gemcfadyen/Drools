package gemcfadyen.drools_experimentation.declare;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class AccessADeclaredTypeFromJavaTest {
	private StatefulKnowledgeSession statefulSession;
	private Object personObjectBuiltUpUsingTypeDefinedInDrl;
	private FactType personFactType;

	@Before
	public void setup() throws InstantiationException, IllegalAccessException {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource drlFile = ResourceFactory.newClassPathResource("../declare-type-use-in-java.drl", this.getClass());
		knowledgeBuilder.add(drlFile, ResourceType.DRL);

		// you must have the knowledgeBase complete with its knowledgePackages
		// before attempting to access the factTypes, otherwise you get null ptr
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Errors found in knowledge builder " + knowledgeBuilder.getErrors().toString());
			throw new RuntimeException("Can't compile " + drlFile);
		}

		personFactType = knowledgeBase.getFactType("gemcfadyen.drools_experimentation.declare", "Person");
		personObjectBuiltUpUsingTypeDefinedInDrl = personFactType.newInstance();

		Calendar dateOfBirthCalendar = Calendar.getInstance();
		dateOfBirthCalendar.set(1982, 5, 29);

		// you update the fact-handle and build up an object with the attributes
		// and values you want to set
		personFactType.set(personObjectBuiltUpUsingTypeDefinedInDrl, "name", "Georgina");
		personFactType.set(personObjectBuiltUpUsingTypeDefinedInDrl, "age", 31);
		personFactType.set(personObjectBuiltUpUsingTypeDefinedInDrl, "dob",
				dateOfBirthCalendar.getTime());

		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
	}

	@Test
	public void shouldFireRulesAgainstInsertedFactUsingADrlDefinedObject() {
		List<String> results = new ArrayList<String>();
		statefulSession.setGlobal("results", results);
		statefulSession.insert(personObjectBuiltUpUsingTypeDefinedInDrl);
		statefulSession.fireAllRules();
		assertThat(results.size(), is(1));
		assertThat(results.get(0), is("Georgina was found"));

		// we can even get the updated attributes of the defined type
		assertThat((Integer) personFactType.get(
				personObjectBuiltUpUsingTypeDefinedInDrl, "age"),
				is(new Integer(18)));
	}
}
