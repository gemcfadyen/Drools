package gemcfadyen.drools_experimentation.drivinglicense;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Test;

public class TravelOfficeTest {
	private StatefulKnowledgeSession statefulSession;
	private DroolsWorkingMemoryEventListener workingMemoryEventListener = new DroolsWorkingMemoryEventListener();
	
	@Before
	public void setup(){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource droolsFile = new ClassPathResource("../travel-pass.drl", this.getClass());
		
		knowledgeBuilder.add(droolsFile, ResourceType.DRL);
		
		if(knowledgeBuilder.hasErrors()){
			System.out.println("knowledge builder has errors " + knowledgeBuilder.getErrors());
		}
		
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		
		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		statefulSession.addEventListener(workingMemoryEventListener);
	}
	
	@Test
	public void shouldIssueAChildsTravelPassToPersonUnderAge16(){
		Applicant child = new Applicant("Bobby", 13);
	
		statefulSession.insert(child);
		statefulSession.fireAllRules();
		
		List<String> objectsInsertedIntoWorkingMemory = workingMemoryEventListener.getInsertedObjects();
		assertThat(objectsInsertedIntoWorkingMemory.size(), is(2));
		Collection<String> expectedObjects = createListOfClassNamesExpectedInSession("Applicant", "IsChild");
		assertTrue(objectsInsertedIntoWorkingMemory.containsAll(expectedObjects));
	}

	
	@Test
	public void shouldIssueAnAdultsTravelPassWhenPersonIs16(){
		Applicant adult = new Applicant("Fred", 16);
		
		statefulSession.insert(adult);
		statefulSession.fireAllRules();
		
		List<String> objectsInsertedIntoWorkingMemory = workingMemoryEventListener.getInsertedObjects();
		assertThat(objectsInsertedIntoWorkingMemory.size(), is(2));
		
		Collection<String> expectedObjects = createListOfClassNamesExpectedInSession("Applicant", "IsAdult");
		assertTrue(objectsInsertedIntoWorkingMemory.containsAll(expectedObjects));
	}
	
	@Test
	public void shouldIssueAnAdultsTravelPassWhenPersonIsOver16(){
		Applicant adult = new Applicant("Ernest", 46);
		
		statefulSession.insert(adult);
		statefulSession.fireAllRules();
		
		List<String> objectsInsertedIntoWorkingMemory = workingMemoryEventListener.getInsertedObjects();
		assertThat(objectsInsertedIntoWorkingMemory.size(), is(2));
		
		Collection<String> expectedObjects = createListOfClassNamesExpectedInSession("Applicant", "IsAdult");
		assertTrue(objectsInsertedIntoWorkingMemory.containsAll(expectedObjects));
	}
	
	@Test
	public void shouldRevokeChildWhenPersonTurnsSixteen(){
		Applicant child = new Applicant("Bobby", 15);
		
		FactHandle facthandle = statefulSession.insert(child);
		statefulSession.fireAllRules();
		
	    assertThatThereAreTwoObjectsInSession();
	
		modifyTheFactsInTheSessionToAgeBobbyByOneYear(child, facthandle);
		
		DroolsWorkingMemoryEventListener workingMemoryListenerAfterRetraction = new DroolsWorkingMemoryEventListener();
		statefulSession.addEventListener(workingMemoryListenerAfterRetraction);
		statefulSession.fireAllRules();
		
		assertThatTheApplicantHasBeenUpdated();
		assertThatTheIsChildFactHasBeenRetracted();
		assertThatIsAdultHasBeenInsertedSinceBobbyAged(workingMemoryListenerAfterRetraction);
	}

	private void assertThatIsAdultHasBeenInsertedSinceBobbyAged(
			DroolsWorkingMemoryEventListener workingMemoryListenerAfterRetraction) {
		List<String> objectsInsertedIntoWorkingMemory = workingMemoryListenerAfterRetraction.getInsertedObjects();
		//This contains only the objects inserted since the modification of the factHandle
		assertThat(objectsInsertedIntoWorkingMemory.size(), is(1));
		
		Collection<String> expectedObjectsAfterSixteenthBirthday = createListOfClassNamesExpectedInSession("IsAdult");
		assertTrue(objectsInsertedIntoWorkingMemory.containsAll(expectedObjectsAfterSixteenthBirthday));
	}

	private void assertThatTheIsChildFactHasBeenRetracted() {
		Collection<String> retractedObjects = createListOfClassNamesExpectedInSession("IsChild");
		List<String> objectsRetractedInWorkingMemory = workingMemoryEventListener.getRetractedObjects();
		assertThat(objectsRetractedInWorkingMemory.size(), is(1));
		assertTrue(objectsRetractedInWorkingMemory.containsAll(retractedObjects));
	}

	private void assertThatTheApplicantHasBeenUpdated() {
		Collection<String> updatedObjects = createListOfClassNamesExpectedInSession("Applicant");
		List<String> objectsUpdatedInWorkingMemory = workingMemoryEventListener.getUpdatedObjects();
		assertThat(objectsUpdatedInWorkingMemory.size(), is(1));
		assertTrue(objectsUpdatedInWorkingMemory.containsAll(updatedObjects));
	}

	private void modifyTheFactsInTheSessionToAgeBobbyByOneYear(Applicant child,
			FactHandle facthandle) {
		child.setAge(16);
		statefulSession.update(facthandle, child);
	}

	private void assertThatThereAreTwoObjectsInSession() {
		List<String> objectsInsertedIntoWorkingMemory = workingMemoryEventListener.getInsertedObjects();
		assertThat(objectsInsertedIntoWorkingMemory.size(), is(2));
		
		Collection<String> expectedObjects = createListOfClassNamesExpectedInSession("Applicant", "IsChild");
		assertTrue(objectsInsertedIntoWorkingMemory.containsAll(expectedObjects));
	}
	
	private Collection<String> createListOfClassNamesExpectedInSession(String...names) {
		Collection<String> expectedObjects = new ArrayList<String>(2);
		for(String name: names){
			expectedObjects.add(name);
		}
		return expectedObjects;
	}

}
