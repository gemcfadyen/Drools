package gemcfadyen.drools_experimentation.helloworld;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RightHandSideTest {
	private StatefulKnowledgeSession statefulSession;

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("right-hand-side.drl",
						this.getClass());
	}

	@Test
	public void shouldReEvaluateTheRulesAsModifyIsUsed() {
		List results = new ArrayList();
		Person child = new Person();
		child.setAge(10);
		child.setName("Billy");
		statefulSession.setGlobal("people", results);
		statefulSession.insert(child);
		statefulSession.fireAllRules();

		assertThat(child.getName(), is("Baby"));
		assertThat(results.size(), is(1));
	}

	@Test
	public void shouldNotReEvaluateTheRulesAsJavaSetterIsUsed() {
		List results = new ArrayList();
		Person oldie = new Person();
		oldie.setAge(65);
		oldie.setName("Bertie");
		statefulSession.setGlobal("people", results);
		statefulSession.insert(oldie);
		statefulSession.fireAllRules();

		assertThat(oldie.getName(), is("Older Generation"));
		assertThat(results.size(), is(0));
	}

	@Test
	public void shouldQueryForPeopleUnderTheAgeOf5() {
		List results = new ArrayList();
		Person jenny = new Person();
		jenny.setAge(4);
		jenny.setName("Jenny");
		Person oldie = new Person();
		oldie.setAge(65);
		oldie.setName("Bertie");

		statefulSession.setGlobal("people", results);
		statefulSession.insert(jenny);
		statefulSession.insert(oldie);
		QueryResults allPeopleUnderTheAgeOf5 = statefulSession
				.getQueryResults("people under the age of 5");
		assertThat(allPeopleUnderTheAgeOf5.size(), is(1));

		for (QueryResultsRow littleOne : allPeopleUnderTheAgeOf5) {
			Person p = (Person) littleOne.get("p");
			assertThat(p.getName(), is("Jenny"));
		}
	}

}
