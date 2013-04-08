package gemcfadyen.drools_experimentation.drunkenantics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryEventListener;
import gemcfadyen.drools_experimentation.drunkenantics.Person.PersonType;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
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
	public void shouldOnlyServeThosePeopleWhoAreUnderTheLimit() {
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
		assertThat(sobreWoman.getNumberOfDrinksConsumed(), is(13));
		assertThat(sobreMan.getNumberOfDrinksConsumed(), is(12));
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
		
	}
}
