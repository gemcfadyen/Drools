package gemcfadyen.drools_experimentation.agenda;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsAgendaEventListener;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class ActivationGroupTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsAgendaEventListener droolsAgendaEventListener = new DroolsAgendaEventListener();

	@Before
	public void setup() {
		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile(
						"activation-group.drl", this.getClass());
		
		statefulSession.addEventListener(droolsAgendaEventListener);
	}

	@Test
	public void shouldRunAllTheRulesUnderTheSameActivationGroup() {
		Task lowPriorityTask = new Task();
		lowPriorityTask.setPriority(PriorityEnum.LOW);
		statefulSession.setGlobal("task", lowPriorityTask);
		statefulSession.insert(lowPriorityTask);

		// you can also set the focus from within the drools file using
		// drools.setFocus("admin");
		statefulSession.getAgenda().getAgendaGroup("admin").setFocus();

		List<String> activations = droolsAgendaEventListener
				.getActivationsCreated();

		for (String activation : activations) {
			System.out.println("Activations " + activation);
		}

		statefulSession.fireAllRules();

		// All possible rules to be executed are placed on activations.
		// These cover three agenda groups - proirity, deadline and admin.
		assertThat(activations.size(), is(2));
		assertThat(activations.get(0), is("get tasks for Mr Low Priority"));
		assertThat(activations.get(1),
				is("get number of evaluation days required for Low Priority"));

		// when the rules are actually fired, because the agenda group is set
		// explicitly to 'admin',
		// only the rules under 'admin' agenda-group are actually in line to be
		// executed.
		// Because the two rules under admin share the same activation group -
		// once the first rule evaluates to true, all other rules
		// on the activation group for that agenda will be cancelled.
		assertThat(lowPriorityTask.getDeadlineChecked(), is(1));
		assertThat(lowPriorityTask.getManagerChecked(), is(0));

		List<String> cancelledActivations = droolsAgendaEventListener.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(1));
		assertThat(cancelledActivations.get(0), is("get tasks for Mr Low Priority"));
	}
}
