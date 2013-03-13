package gemcfadyen.drools_experimentation.helloworld;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class HelloWorld {

	private void setupKnowledgeBuilderAndLoadDroolsFile(){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource droolsFile = ResourceFactory.newClassPathResource("hello-world.drl", HelloWorld.class);
		knowledgeBuilder.add(droolsFile, ResourceType.DRL);
		
		if(knowledgeBuilder.hasErrors()){
			System.out.println("Errors found in knowledge builder " + knowledgeBuilder.getErrors().toString() );
			throw new RuntimeException("Can't compile hello-world.drl");
		}
		
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		
		StatefulKnowledgeSession statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		
		statefulSession.addEventListener(new DebugAgendaEventListener());
		statefulSession.addEventListener(new DebugWorkingMemoryEventListener());
	}
	
}


