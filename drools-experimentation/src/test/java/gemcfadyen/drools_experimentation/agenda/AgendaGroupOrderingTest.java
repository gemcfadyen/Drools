package gemcfadyen.drools_experimentation.agenda;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsAgendaEventListener;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AgendaGroupOrderingTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsAgendaEventListener droolsAgendaEventListener = new DroolsAgendaEventListener();

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile(
						"agenda-group-ordering.drl", this.getClass());
		statefulSession.addEventListener(droolsAgendaEventListener);
	}

	@After
	public void tearDown() {
		if (statefulSession != null) {
			statefulSession.dispose();
		}
	}

	@Test
	public void shouldRunAllTheRulesUnderTheSameActivationGroup() {
		Task lowPriorityTask = new Task();
		lowPriorityTask.setPriority(PriorityEnum.LOW);
		statefulSession.setGlobal("task", lowPriorityTask);
		statefulSession.insert(lowPriorityTask);
		// Here we are setting the order of the rules to execute in the drl
		// file. the 'define-order'group refers to a rule which sets this up.
		statefulSession.getAgenda().getAgendaGroup("define-order").setFocus();

		List<String> activations = droolsAgendaEventListener.getActivationsCreated();

		statefulSession.fireAllRules();

		// All possible rules to be executed are placed on activations.
		assertThat(activations.size(), is(3));
		assertThat(activations.get(0), is("shouldDefineOrder"));
		assertThat(activations.get(1), is("get tasks for Mr Low Priority"));
		assertThat(activations.get(2),
				is("get number of evaluation days required for Low Priority"));

		// when the rules are actually fired, because the agenda group is set
		// explicitly to 'define-order',
		// This rule uses drools.setFocus to push two more agenda groups onto
		// the activation stack.
		// As both these agenda-groups are on the default activation they both
		// run
		// If the two agenda-groups had the same activation group explicitly
		// set, then once the first rule had evaluated to true,
		// the rest would be cancelled from the activation list.

		assertThat(lowPriorityTask.getDeadlineChecked(), is(1));
		assertThat(lowPriorityTask.getManagerChecked(), is(1));

	}

}
