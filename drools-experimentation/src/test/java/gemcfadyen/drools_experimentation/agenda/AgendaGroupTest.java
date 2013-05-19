package gemcfadyen.drools_experimentation.agenda;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsAgendaEventListener;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AgendaGroupTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsAgendaEventListener droolsAgendaEventListener = new DroolsAgendaEventListener();
	
	@Before
	public void setup() {
		statefulSession =  DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("agenda-group.drl", this.getClass());
		statefulSession.addEventListener(droolsAgendaEventListener);
	}
	
	@After
	public void tearDown(){
		if(statefulSession != null){
			statefulSession.dispose();
		}
	}
	// Here we have two agenda groups. Two rules match the fact inserted, each
	// rule is on a different agenda group.
	// When the rules are fired, because we have not explicitly set the agenda
	// group's focus, the rules engine will default
	// to running the activations on MAIN. As there are no rules with an agenda
	// group of main, no rules are actually
	@Test
	public void shouldPlaceAllRulesAssociatedWithHighPriorityTasksOnTheActivationListButFireNoneAsAgendaFocusIsNotSet() {
		Task highPriorityTask = new Task();
		highPriorityTask.setPriority(PriorityEnum.HIGH);
		statefulSession.setGlobal("task", highPriorityTask);
		statefulSession.insert(highPriorityTask);
		List<String> activations = droolsAgendaEventListener
				.getActivationsCreated();

		assertThat(activations.get(0), is("highest priority rule"));
		assertThat(activations.get(1), is("deadline for high priority rules"));

		statefulSession.fireAllRules();

		assertThat(highPriorityTask.getDeadlineForEvaluation(), is(0));
		assertNull(highPriorityTask.getManagedBy());

	}

	// Here we are explicitly setting the focus to the agenda group 'priority'.
	// This means, when the rules are fired, all those rules that are on the
	// activation list for the agenda 'priority' will be run
	@Test
	public void shouldPlaceAllRulesAssociatedWithAgendaGroupPriorityOnTheActivationList() {
		Task highPriorityTask = new Task();
		highPriorityTask.setPriority(PriorityEnum.HIGH);
		statefulSession.setGlobal("task", highPriorityTask);
		statefulSession.getAgenda().getAgendaGroup("priority").setFocus();
		statefulSession.insert(highPriorityTask);

		List<String> activations = droolsAgendaEventListener
				.getActivationsCreated();
		for (String activation : activations) {
			System.out.println("Activations " + activation);
		}

		statefulSession.fireAllRules();

		// Even though the rules are fired, there are two activations - one for
		// the agenda group 'priority' and one for the agenda group 'deadline'
		assertThat(activations.size(), is(2));
		assertThat(activations.get(0), is("highest priority rule"));
		assertThat(activations.get(1), is("deadline for high priority rules"));

		// however you can see from the results object that only the rule
		// associated with the agenda group 'priority' have run.
		assertThat(highPriorityTask.getDeadlineForEvaluation(), is(0));
		assertThat(highPriorityTask.getManagedBy(), is("Mr High Priority"));

	}

	@Test
	public void shouldRunAllTheRulesUnderTheSameActivationGroup() {
		Task lowPriorityTask = new Task();
		lowPriorityTask.setPriority(PriorityEnum.LOW);
		statefulSession.setGlobal("task", lowPriorityTask);
		statefulSession.insert(lowPriorityTask);
		// ActivationGroup activationGroupForPriorityActivation =
		// statefulSession.getAgenda().getActivationGroup("priority activation");

		statefulSession.getAgenda().getAgendaGroup("admin").setFocus();

		List<String> activations = droolsAgendaEventListener
				.getActivationsCreated();

		for (String activation : activations) {
			System.out.println("Activations " + activation);
		}

		statefulSession.fireAllRules();

		// All possible rules to be executed are placed on activations.
		// These cover three agenda groups - proirity, deadline and admin.
		assertThat(activations.size(), is(4));
		assertThat(activations.get(0), is("lowest priority rule"));
		assertThat(activations.get(1), is("deadline for low priority rules"));
		assertThat(activations.get(2), is("get tasks for Mr Low Priority"));
		assertThat(activations.get(3),
				is("get number of evaluation days required for Low Priority"));

		// when the rules are actually fired, because the agenda group is set
		// explicitly to 'admin',
		// only the rules under 'admin' agenda-group are actually executed.
		// All rules in the agenda group with the focus are run because there is
		// no explicit activation group set
		assertThat(lowPriorityTask.getDeadlineChecked(), is(1));
		assertThat(lowPriorityTask.getManagerChecked(), is(1));

	}

}
