package gemcfadyen.drools_experimentation.helloworld;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class LeftHandSideTest {
	private StatefulKnowledgeSession statefulSession;

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile("left-hand-side.drl",
						this.getClass());
	}

	// Conditions on the LHS should always be READ ONLY.
	// In this example, the RHS is actually changing the state of the fact which
	// is not recommended.
	// This test was just to prove if it was possible or not - and proves it is
	// possible.
	@Test
	public void shouldExecuteLeftJandSideWhichIsReadWrite() {
		Person gem = new Person();
		gem.setAge(9);

		statefulSession.insert(gem);
		statefulSession.fireAllRules();

		assertThat(gem.getAge(), is(10));
	}

	// This test is using a rule which has an equality operator. In drools the
	// equality operator has null security.
	// This test proves this as it doesnt throw any exceptions.

	@Test
	public void shouldCheckForEqualityUsingNullValue() {
		Person john = new Person();
		john.setName(null);
		statefulSession.insert(john);
		statefulSession.fireAllRules();

		assertNull(john.getName());
	}

	@Test
	public void shouldCheckForEqualityUsingNonNullValue() {
		Person jenny = new Person();
		jenny.setName("Jenny");
		statefulSession.insert(jenny);
		statefulSession.fireAllRules();

		assertThat(jenny.getName(), is("Jenny"));
	}

	@Test
	public void shouldPerofrmCalculationsOnAge() {
		Person gem = new Person();
		gem.setAge(11);

		statefulSession.insert(gem);
		statefulSession.fireAllRules();

		assertThat(gem.getAge(), is(12));
	}

	@Test
	public void shouldPerofrmCaluclationsOnAgeUsingCommaSeperatedAND() {
		Person gem = new Person();
		gem.setAge(1);
		statefulSession.insert(gem);
		statefulSession.fireAllRules();

		assertThat(gem.getAge(), is(2));
	}
}
