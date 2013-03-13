package gemcfadyen.drools_experimentation;

import gemcfadyen.drools_experimentation.helloworld.HelloWorld;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class DroolsWorkingMemoryHelper {

	private static KnowledgeBase getKnowledgeBaseInitialisedWithDroolsFileNamed(KnowledgeBuilder knowledgeBuilder) {
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		return knowledgeBase;
	}
	
	private static KnowledgeBuilder getKnowledgeBuilderWithDroolsFileNamed(String drlFileName){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		Resource drlFile = ResourceFactory.newClassPathResource("../" + drlFileName, HelloWorld.class);
		knowledgeBuilder.add(drlFile, ResourceType.DRL);
		
		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Errors found in knowledge builder " + knowledgeBuilder.getErrors().toString());
			throw new RuntimeException("Can't compile hello-world.drl");
		}
		
		return knowledgeBuilder;
	}
	
	public static StatefulKnowledgeSession getStatefulWorkingMemoryUsingDroolsFile(String drlFilename){
		KnowledgeBase knowledgeBase = getKnowledgeBaseInitialisedWithDroolsFileNamed(getKnowledgeBuilderWithDroolsFileNamed(drlFilename));
		
		StatefulKnowledgeSession statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		statefulSession.addEventListener(new DebugWorkingMemoryEventListener());
		
		return statefulSession;
	}
	
	public static StatelessKnowledgeSession getStatelessWorkingMemoryUsingDroolsFile(String drlFilename){
		KnowledgeBase knowledgeBase = getKnowledgeBaseInitialisedWithDroolsFileNamed(getKnowledgeBuilderWithDroolsFileNamed(drlFilename));
		return knowledgeBase.newStatelessKnowledgeSession();
	}
}
