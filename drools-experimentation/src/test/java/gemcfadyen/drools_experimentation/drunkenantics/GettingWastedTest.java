package gemcfadyen.drools_experimentation.drunkenantics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryEventListener;
import gemcfadyen.drools_experimentation.drunkenantics.Person.PersonType;

import java.util.ArrayList;
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

public class GettingWastedTest {
	private StatefulKnowledgeSession workingMemory;
	private DroolsWorkingMemoryEventListener workingMemoryEventListener = new DroolsWorkingMemoryEventListener();

	@Before
	public void setup() {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		Resource drlFileResource = new ClassPathResource(
				"../getting-wasted.drl", this.getClass());
		knowledgeBuilder.add(drlFileResource, ResourceType.DRL);

		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Rules have errors "
					+ knowledgeBuilder.getErrors());
		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder
				.getKnowledgePackages());
		workingMemory = knowledgeBase.newStatefulKnowledgeSession();

	}

	@Test
	public void shouldGiveManUnderAlcoholLimitAnotherDrink() {
		Person manUnderAlcoholLimit = new Person(PersonType.MAN);

		workingMemory.insert(manUnderAlcoholLimit);
		workingMemory.fireAllRules();

		assertThat(manUnderAlcoholLimit.getNumberOfDrinksConsumed(), is(1));

	}

	@Test
	public void shouldGiveWomanUnderAlcoholLimitAnotherDrink() {
		Person womanUnderAlcoholLimit = new Person(PersonType.WOMAN);

		workingMemory.insert(womanUnderAlcoholLimit);
		workingMemory.fireAllRules();

		assertThat(womanUnderAlcoholLimit.getNumberOfDrinksConsumed(), is(1));

	}

	@Test
	public void shouldGiveGeordieUnderAlcoholLimitAnotherDrink() {
		Person geordieUnderAlcoholLimit = new Person(PersonType.GEORDIE);

		workingMemory.insert(geordieUnderAlcoholLimit);
		workingMemory.fireAllRules();

		assertThat(geordieUnderAlcoholLimit.getNumberOfDrinksConsumed(), is(1));

	}

	@Test
	public void shouldNotGiveAnyMoreDrinksToAManWhoHasAlreadyHad20Units() {
		Person drunkMan = new Person(PersonType.MAN);
		drunkMan.setNumberOfDrinksConsumed(20);

		workingMemory.insert(drunkMan);
		workingMemory.fireAllRules();

		assertThat(drunkMan.getNumberOfDrinksConsumed(), is(20));
	}

	@Test
	public void shouldNotGiveAnyMoreDrinksToAWomanWhoHasAlreadyHad15Units() {
		Person drunkWoman = new Person(PersonType.WOMAN);
		drunkWoman.setNumberOfDrinksConsumed(15);

		workingMemory.insert(drunkWoman);
		workingMemory.fireAllRules();

		assertThat(drunkWoman.getNumberOfDrinksConsumed(), is(15));
	}

	@Test
	public void shouldNotGiveAnyMoreDrinksToAGeordieWhoHasAlreadyHad50Units() {
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(50);

		workingMemory.insert(drunkGeordie);
		workingMemory.fireAllRules();

		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
	}

	@Test
	public void shouldServeNobodyIfAnyOneInThePartyIsOverTheLimit() {
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(50);
		Person sobreWoman = new Person(PersonType.WOMAN);
		sobreWoman.setNumberOfDrinksConsumed(12);
		Person sobreMan = new Person(PersonType.MAN);
		sobreMan.setNumberOfDrinksConsumed(11);
		
		workingMemory.insert(drunkGeordie);
		workingMemory.insert(sobreWoman);
		workingMemory.insert(sobreMan);
		workingMemory.fireAllRules();
		
		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
		assertThat(sobreWoman.getNumberOfDrinksConsumed(), is(12));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(11));
	}
	
	@Test
	public void shouldMakeDrunkPersonSickWhenTheyExceedTheirAlcoholLimit(){
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(50);
		
		workingMemory.addEventListener(workingMemoryEventListener);
		workingMemory.insert(drunkGeordie);
		workingMemory.fireAllRules();
		
		List<String> factsInTheSession = workingMemoryEventListener.getInsertedObjects();
		assertTrue(factsInTheSession.contains("Puke"));
		
	
		List<Object> newObjectsInSession = workingMemoryEventListener.getActualObjectInserted();
		List<Puke> pukers = getAllThoseSick(newObjectsInSession);
		
		assertThat(pukers.size(), is(1));
		assertTrue(pukers.get(0).equals(new Puke(drunkGeordie)));
		
	}
	
	@Test
	public void shouldNotServeAnyoneAtThePartyIfThereIsAnySick(){
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(50);
		
		Person sobreMan = new Person(PersonType.MAN);
		sobreMan.setNumberOfDrinksConsumed(9);
		
		workingMemory.addEventListener(workingMemoryEventListener);
		workingMemory.insert(drunkGeordie);
		workingMemory.insert(sobreMan);
		workingMemory.fireAllRules();
		
		List<String> factsInTheSession = workingMemoryEventListener.getInsertedObjects();
		assertTrue(factsInTheSession.contains("Puke"));
		List<Puke> pukers = getAllThoseSick(workingMemoryEventListener.getActualObjectInserted());
		assertThat(pukers.size(), is(1));
		assertTrue(pukers.get(0).equals(new Puke(drunkGeordie)));
		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(9));
		
	}
	
	@Test
	public void shouldRemoveTheSickPeopleIfABouncerIsPresent(){
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(50);
		
		Person sobreMan = new Person(PersonType.MAN);
		sobreMan.setNumberOfDrinksConsumed(9);
		
		workingMemory.addEventListener(workingMemoryEventListener);
		workingMemory.insert(drunkGeordie);
		workingMemory.insert(sobreMan);

		workingMemory.fireAllRules();
		
		//because one member of the party is above the limit, neither person gets served another drink
		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(9));
		
		assertTrue(workingMemoryEventListener.getInsertedObjects().contains("Puke"));
		List<Puke> pukers = getAllThoseSick(workingMemoryEventListener.getActualObjectInserted());
		assertThat(pukers.size(), is(1));
		assertTrue(pukers.get(0).equals(new Puke(drunkGeordie)));
		
		//Because the drunkGeordie has been sick we want to send a bouncer in to sort the situation out
		Person bouncer = new Person(PersonType.BOUNCER);
		workingMemory.insert(bouncer);
		workingMemory.fireAllRules();
	
		//The bouncer evicts the drunk Geordie, the sick is cleaned up. This allows the sobreMan to get served
		List<Object> wasters = getAllFactsRemovedFromTheSession(workingMemoryEventListener.getActualObjectsRetracted());
		
		assertThat(wasters.size(), is(2));
		assertTrue(wasters.get(0).equals(drunkGeordie));
		assertTrue(wasters.get(1).equals(new Puke(drunkGeordie)));
		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(10)); //man's drink count has gone up proving the sick has been cleaned up
	}
	
	@Test
	public void shouldSeveralPeopleBeSickTheBouncerShouldSortOutTheCrowd(){
		Person drunkGeordie = new Person(PersonType.GEORDIE);
		drunkGeordie.setNumberOfDrinksConsumed(49);
		
		Person sobreMan = new Person(PersonType.MAN);
		sobreMan.setNumberOfDrinksConsumed(9);
		
		Person sobreWoman = new Person(PersonType.MAN);
		sobreWoman.setNumberOfDrinksConsumed(1);
		
		workingMemory.addEventListener(workingMemoryEventListener);
		FactHandle geordieFactHandle = workingMemory.insert(drunkGeordie);
		FactHandle mansFactHandle = workingMemory.insert(sobreMan);
		workingMemory.insert(sobreWoman);

		workingMemory.fireAllRules();
		
		//because one member of the party is above the limit, neither person gets served another drink
		assertThat(drunkGeordie.getNumberOfDrinksConsumed(), is(50));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(10));
		assertThat(sobreWoman.getNumberOfDrinksConsumed(), is(2));

		//re-evaluate the rools now that the Geordie has reached his limit
		workingMemory.update(geordieFactHandle, drunkGeordie);
		workingMemory.fireAllRules();
		
		assertTrue(workingMemoryEventListener.getInsertedObjects().contains("Puke"));
		List<Puke> pukers = getAllThoseSick(workingMemoryEventListener.getActualObjectInserted());
		assertThat(pukers.size(), is(1));
		assertTrue(pukers.get(0).equals(new Puke(drunkGeordie)));
		
		//The man has had some sneaky drinks out the back...re-evaluate the rules
		sobreMan.setNumberOfDrinksConsumed(20);
		workingMemory.update(mansFactHandle, sobreMan);
		workingMemory.fireAllRules();
		
		pukers = getAllThoseSick(workingMemoryEventListener.getActualObjectInserted());
		assertThat(pukers.size(), is(2));
		assertTrue(pukers.contains(new Puke(drunkGeordie)));
		assertTrue(pukers.contains(new Puke(sobreMan)));
		
		//sobreWoman wants a drink but can't get served because of the commotion
		assertThat(sobreWoman.getNumberOfDrinksConsumed(), is(2));
		
		//send a bouncer in to sort out the mess
     	Person bouncer = new Person(PersonType.BOUNCER);
		workingMemory.insert(bouncer);
		workingMemory.fireAllRules();
		
		List<Object> wasters = getAllFactsRemovedFromTheSession(workingMemoryEventListener.getActualObjectsRetracted());
		assertThat(wasters.size(), is(4));
		assertTrue(wasters.contains(sobreMan));
		assertTrue(wasters.contains(drunkGeordie));
		assertTrue(wasters.contains(new Puke(drunkGeordie)));
		assertTrue(wasters.contains(new Puke(sobreMan)));
		
		//now the mess is cleaned up, the lady can get her cocktail..
		assertThat(sobreWoman.getNumberOfDrinksConsumed(), is(3));	
	}
	
	@Test
	public void shouldNotServeAnyoneWhoIsOverTheirMaximumLimit(){
		Person alcoholic = new Person(PersonType.MAN);
		alcoholic.setNumberOfDrinksConsumed(100); //exceeded limit already
		
		workingMemory.insert(alcoholic);
		workingMemory.addEventListener(workingMemoryEventListener);
		workingMemory.fireAllRules();
		
		//cant be served anymore
		assertThat(alcoholic.getNumberOfDrinksConsumed(), is(100));
		//Alcoholic is hardened so is not sick
		assertThat(workingMemoryEventListener.getActualObjectInserted().size(), is(0)); 
	}
	
	private List<Puke> getAllThoseSick(List<Object> factsInSession){
		List<Puke> lightweights = new ArrayList<Puke>();
		
		for(Object insertedFacts: factsInSession){
			Puke puker = null;
			if (insertedFacts instanceof Puke){
				puker = (Puke)insertedFacts;
				lightweights.add(puker);
			}
		}
		return lightweights;
	}
	
	
	private List<Object> getAllFactsRemovedFromTheSession(List<Object> peopleRetracted){
		List<Object> evictedFromSession = new ArrayList<Object>();
		
		for(Object facts : peopleRetracted){
			if(facts instanceof Person){
				Person personChuckedOut = (Person)facts;
				evictedFromSession.add(personChuckedOut);
				System.out.println("person chucked out " + personChuckedOut.getType().toString());
			}
			
			if(facts instanceof Puke){
				Puke chunder = (Puke)facts;
				evictedFromSession.add(chunder);
				System.out.println("puke cleaned up");
			}
		}
		return evictedFromSession;
	}
}
