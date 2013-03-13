package gemcfadyen.drools_experimentation;

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
	
	private static KnowledgeBuilder getKnowledgeBuilderWithDroolsFileNamed(String drlFileName, Class className ){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		Resource drlFile = ResourceFactory.newClassPathResource("../" + drlFileName, className);
		knowledgeBuilder.add(drlFile, ResourceType.DRL);
		
		if (knowledgeBuilder.hasErrors()) {
			System.out.println("Errors found in knowledge builder " + knowledgeBuilder.getErrors().toString());
			throw new RuntimeException("Can't compile " + drlFileName);
		}
		
		return knowledgeBuilder;
	}
	
	public static StatefulKnowledgeSession getStatefulWorkingMemoryUsingDroolsFile(String drlFilename, Class className){
		KnowledgeBase knowledgeBase = getKnowledgeBaseInitialisedWithDroolsFileNamed(getKnowledgeBuilderWithDroolsFileNamed(drlFilename, className));
		
		StatefulKnowledgeSession statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		statefulSession.addEventListener(new DebugWorkingMemoryEventListener());
		
		return statefulSession;
	}
	
	public static StatelessKnowledgeSession getStatelessWorkingMemoryUsingDroolsFile(String drlFilename, Class className){
		KnowledgeBase knowledgeBase = getKnowledgeBaseInitialisedWithDroolsFileNamed(getKnowledgeBuilderWithDroolsFileNamed(drlFilename, className));
		return knowledgeBase.newStatelessKnowledgeSession();
	}
}
