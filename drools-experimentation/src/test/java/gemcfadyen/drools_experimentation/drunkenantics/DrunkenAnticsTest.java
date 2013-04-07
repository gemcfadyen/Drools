package gemcfadyen.drools_experimentation.drunkenantics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a kata dreamt up by a male colleague :-)
 * 
 * @author Georgina
 *
 */

public class DrunkenAnticsTest {
	private StatefulKnowledgeSession statefulSession;

	@Before
	public void setup() {
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		Resource drlfile = ResourceFactory
				.newClassPathResource("../drunken-antics.drl", this.getClass());
		knowledgeBuilder.add(drlfile, ResourceType.DRL);
		knowledgeBase.addKnowledgePackages(knowledgeBuilder
				.getKnowledgePackages());

		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Errors in drools file"
					+ knowledgeBuilder.getErrors().toString());
		}

		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
	}

	@Test
	public void shouldReturnNoAffairWhenManAndWomanAreSingle() {
		boolean isSingle = true;
		Man geezer = new Man(isSingle);
		Woman gal = new Woman(isSingle);

		statefulSession.insert(geezer);
		statefulSession.insert(gal);
		statefulSession.fireAllRules();
		
		assertTrue(geezer.getIsLoyal());
		assertTrue(gal.getIsLoyal());
	}
	
	@Test
	public void shouldReturnThatBothManAndWomanAreAdultersIfNeitherAreSingle(){
		boolean notSingle = false;
		Man geezer = new Man(notSingle);
		Woman gal = new Woman(notSingle);

		statefulSession.insert(geezer);
		statefulSession.insert(gal);
		statefulSession.fireAllRules();
		
		assertFalse(geezer.getIsLoyal());
		assertFalse(gal.getIsLoyal());
	}
	
	@Test
	public void shouldReturnThatTheWomanIsAnAdultererIfSheIsNotSingleButTheGuyIs(){
		boolean isSingle = true;
		
		Man geezer = new Man(isSingle);
		Woman gal = new Woman(!isSingle);

		statefulSession.insert(geezer);
		statefulSession.insert(gal);
		statefulSession.fireAllRules();
		
		assertTrue(geezer.getIsLoyal());
		assertFalse(gal.getIsLoyal());
	}

}
