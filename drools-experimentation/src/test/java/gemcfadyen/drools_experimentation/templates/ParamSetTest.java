package gemcfadyen.drools_experimentation.templates;

import static org.drools.io.ResourceFactory.newReaderResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import gemcfadyen.drools_experimentation.DroolsAgendaEventListener;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryEventListener;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.template.ObjectDataCompiler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParamSetTest {
	private StatefulKnowledgeSession statefulSession;
	private String drl;
	private KnowledgeRuntimeLogger logger;
    private Collection<ParamSet> paramSets;
	

	@Before
	public void shouldCreateARulesFileForCategorisingWords() {

		Collection<ParamSet> paramSets = createLibraryOfWordsOfWhichTheRulesWillBeFormed();

		ObjectDataCompiler converter = new ObjectDataCompiler();
		InputStream templateStream = this.getClass().getResourceAsStream(
				"../template-paramset.drl");
		drl = converter.compile(paramSets, templateStream);
		System.out.println("DRL: " + drl);

		KnowledgeBase knowledgeBase = createKnowledgeBaseUsing(drl);
		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		statefulSession.addEventListener(new DroolsAgendaEventListener());
		statefulSession
				.addEventListener(new DroolsWorkingMemoryEventListener());
		logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulSession,
				"C:/Users/Georgina/Documents/GitHub/Drools/log/template");

	}

	@After
	public void tearDown() {
		if (statefulSession != null) {
			logger.close();
			statefulSession.dispose();
		}
	}

	@Test
	public void shouldCheckTheRulesHaveBeenCreatedInDrl() {
		assertTrue(drl.contains("blablabla  has evaluated to:  false"));
		assertTrue(drl.contains("Sausage  has evaluated to:  true"));
	}

	@Test
	public void shouldUseTemplatedDrlToEvaluateFactInsertedIntoSession() {
		ParamSet param = new ParamSet("Sausage", 15, true);

		List<String> globals = new ArrayList<String>();
		statefulSession.setGlobal("list", globals);
	    statefulSession.insert(param);
		statefulSession.fireAllRules();
				
		assertThat(param.getLimit(), is(-1));
		assertThat(globals.size(), is(1));
		assertThat(globals.get(0), is("hi global added"));

	}

	private Collection<ParamSet> createLibraryOfWordsOfWhichTheRulesWillBeFormed() {
		paramSets = new ArrayList<ParamSet>();
		paramSets.add(new ParamSet("Sausage", 12, true));
		paramSets.add(new ParamSet("blablabla", 1, false));
		return paramSets;
	}
	
	private KnowledgeBase createKnowledgeBaseUsing(String drlRules) {
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Reader reader = new StringReader(drlRules);

		knowledgeBuilder.add(newReaderResource(reader), ResourceType.DRL);
		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Errors in drl " + knowledgeBuilder.getErrors());
		}
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		return knowledgeBase;
		
		
		
		//KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
		//KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		//Reader rdr = new StringReader( drl );
		//kBuilder.add( ResourceFactory.newReaderResource( rdr ), ResourceType.DRL );
		//if( kBuilder.hasErrors() ){
		    // ...
		   // throw new IllegalStateException( "DRL errors" );
		//}
		//kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
	}

}
