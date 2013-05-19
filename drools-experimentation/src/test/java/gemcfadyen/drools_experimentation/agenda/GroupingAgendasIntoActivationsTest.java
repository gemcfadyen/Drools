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

public class GroupingAgendasIntoActivationsTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsAgendaEventListener droolsAgendaEventListener = new DroolsAgendaEventListener();

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile("grouping-agenda-to-activations.drl",
						this.getClass());
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
		
		for(String a: activations){
			System.out.println("ACtive " + a);
		}

		// All possible rules to be executed are placed on activations.
		assertThat(activations.size(), is(3));
		assertThat(activations.get(0), is("shouldDefineOrder"));
		assertThat(activations.get(1), is("get tasks for Mr Low Priority"));
		assertThat(activations.get(2), is("get number of evaluation days required for Low Priority"));

		// when the rules are actually fired, because the agenda group is set
		// explicitly to 'define-order',
		// This rule uses drools.setFocus to push two more agenda groups onto
		// the activation stack.
		// As both these agenda-groups are explicitly set to the same agenda
		// group "admin-activation"
		// as soon as one rule has passed, the rest of the rules on that same
		// activation list are cancelled.
		assertThat(lowPriorityTask.getDeadlineChecked(), is(0));
		assertThat(lowPriorityTask.getManagerChecked(), is(1));

		List<String> cancelledActivations = droolsAgendaEventListener
				.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(1));
		assertThat(cancelledActivations.get(0),
				is("get number of evaluation days required for Low Priority"));

	}

}
