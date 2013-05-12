package gemcfadyen.drools_experimentation;


import java.util.ArrayList;
import java.util.List;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
/**
 * The event listener will keep track of the agenda list in session
 * @author Georgina
 *
 */
public class DroolsAgendaEventListener implements AgendaEventListener {

	List<String> activationsCreated = new ArrayList<String>();
	List<String> activationsCancelled = new ArrayList<String>();
	
	public List<String> getActivationsCreated(){
		return activationsCreated;
	}
	
	public List<String> getActivationsCancelled(){
		return activationsCancelled;
	}
	
	@Override
	public void activationCreated(ActivationCreatedEvent event) {
		activationsCreated.add(event.getActivation().getRule().getName());
		System.out.println("Activation Created" + event.getActivation());
		
	}

	@Override
	public void activationCancelled(ActivationCancelledEvent event) {
		activationsCancelled.add(event.getActivation().getRule().getName());
		System.out.println("Activation cancelled" + event.getActivation());
		
	}

	@Override
	public void beforeActivationFired(BeforeActivationFiredEvent event) {
		System.out.println("beforeActivationFired" +event.getActivation().getRule().getName() );
		
	}

	@Override
	public void afterActivationFired(AfterActivationFiredEvent event) {
		System.out.println("afterActivationFired" +event.getActivation().getRule().getName() );
		
	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		System.out.println("agendaGroupPopped" +event.getAgendaGroup().getName() );
		
	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
		System.out.println("agendaGroupPushed" +event.getAgendaGroup().getName() );
		
	}

	@Override
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		System.out.println("beforeRuleFlowGroupActivated" );
		
	}

	@Override
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		System.out.println("afterRuleFlowGroupActivated" );
		
	}

	@Override
	public void beforeRuleFlowGroupDeactivated(
			RuleFlowGroupDeactivatedEvent event) {
		System.out.println("beforeRuleFlowGroupDeactivated" );
		
		
	}

	@Override
	public void afterRuleFlowGroupDeactivated(
			RuleFlowGroupDeactivatedEvent event) {
		System.out.println("afterRuleFlowGroupDeactivated" );
		
	}

	
}

