package gemcfadyen.drools_experimentation.agenda;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsAgendaEventListener;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.List;

import junit.framework.Assert;

import org.drools.base.RuleNameEndsWithAgendaFilter;
import org.drools.base.RuleNameEqualsAgendaFilter;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.RuleNameStartsWithAgendaFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//When fireAllRules is invoked, you can pass it an AgendaFilter.
//The types of AgendaFilter available are: RuleNameEndsWith, RuleNameEquals, RuleNameMatches, RuleNameStartsWith.
//The matching rules will be placed on the activation list
public class AgendaFilteringTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsAgendaEventListener agendaListener = new DroolsAgendaEventListener();
	
	@Before
	public void setupDroolsKnowledgeBase() {
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("agenda-filtering.drl", this.getClass());
		statefulSession.addEventListener(agendaListener);
	}

	@After
	public void tearDown(){
	  if(statefulSession !=null){
		statefulSession.dispose();
	  }
	}
	
	//This test puts the 'positive test', 'another positive test' and 'is zero' rules on the activation list
	//As we have an agenda filter, we only want to execute the rule named 'is zero'. As the other two positive rule names do not conform,
	//they are cancelled from the activation list
	//only the 'is zero' test is run
	@Test
	public void shouldFilterOutTheRulesThatDoNotMatchTheExactRuleNameProvidedInTheFilter() {
		Number positiveValue = new Number(10);
		Number equalValue = new Number(0);
		
		statefulSession.insert(positiveValue);
		statefulSession.insert(equalValue);
		statefulSession.fireAllRules(new RuleNameEqualsAgendaFilter("is zero"));
		
		List<String> createdActivations = agendaListener.getActivationsCreated();
		assertThat(createdActivations.size(), is(3));
		assertThat(createdActivations.get(0), is("positive test"));
		assertThat(createdActivations.get(1), is("another positive test"));
		assertThat(createdActivations.get(2), is("is zero"));

		List<String> cancelledActivations = agendaListener.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(2));
		String cancelledActivation = cancelledActivations.get(1);
		assertThat(cancelledActivation, is("positive test"));
		String secondCancelledActivation = cancelledActivations.get(0);
		assertThat(secondCancelledActivation, is("another positive test"));
		
		
	}
	
	//This test puts the 'positive test', 'another positive test' and 'is zero' rules on the activation list
	//As we have an agenda filter, the 'is zero' doesn't conform, and is therefore cancelled from the activation list
	//only the 'positive test' and 'another positive test' rules are run
	@Test
	public void shouldFilterOutTheRulesThatDoNotMatchEndInTheWordTest() {
		Number positiveValue = new Number(10);
		Number equalValue = new Number(0);
		
		statefulSession.insert(positiveValue);
		statefulSession.insert(equalValue);
		statefulSession.fireAllRules(new RuleNameEndsWithAgendaFilter("test"));
		
		List<String> createdActivations = agendaListener.getActivationsCreated();
		assertThat(createdActivations.size(), is(3));
		assertThat(createdActivations.get(0), is("positive test"));
		assertThat(createdActivations.get(1), is("another positive test"));
		assertThat(createdActivations.get(2), is("is zero"));

		List<String> cancelledActivations = agendaListener.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(1));
		String cancelledActivation = cancelledActivations.get(0);
		assertThat(cancelledActivation, is("is zero"));
		
	}
	
	@Test
	public void shouldFilterOutTheRulesThatDoNotStartWithTheNameInTheFilter() {
		Number negativeValue = new Number(-10);
		Number equalValue = new Number(0);
		
		statefulSession.insert(negativeValue);
		statefulSession.insert(equalValue);
		statefulSession.fireAllRules(new RuleNameStartsWithAgendaFilter("i"));
		
		List<String> createdActivations = agendaListener.getActivationsCreated();
		assertThat(createdActivations.size(), is(2));
		assertThat(createdActivations.get(0), is("negative test"));
		assertThat(createdActivations.get(1), is("is zero"));

		List<String> cancelledActivations = agendaListener.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(1));
		String cancelledActivation = cancelledActivations.get(0);
		assertThat(cancelledActivation, is("negative test"));	
	}
	
	@Test
	public void shouldFilterOutTheRulesThatDoNotContainWithTheNameInTheFilter() {
		Number positiveValue = new Number(10);
		Number negativeValue = new Number(-10);
		Number equalValue = new Number(0);
		
		statefulSession.insert(positiveValue);
		statefulSession.insert(negativeValue);
		statefulSession.insert(equalValue);
		statefulSession.fireAllRules(new RuleNameMatchesAgendaFilter("positive test"));
		
		List<String> createdActivations = agendaListener.getActivationsCreated();
		assertThat(createdActivations.size(), is(4));

		assertThat(createdActivations.get(0), is("positive test"));
		assertThat(createdActivations.get(1), is("another positive test"));
		assertThat(createdActivations.get(2), is("negative test"));
		assertThat(createdActivations.get(3), is("is zero"));

		List<String> cancelledActivations = agendaListener.getActivationsCancelled();
		assertThat(cancelledActivations.size(), is(3));
		Assert.assertFalse(cancelledActivations.contains("positive test"));
	}
			
}
