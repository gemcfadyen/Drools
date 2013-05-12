package gemcfadyen.drools_experimentation.knowledgeResultError;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.junit.Before;
import org.junit.Test;


//Tried to override the duplicate rule property knowledgeBuilderConfiguration.setOption(KBuilderSeverityOption.get("drools.kbuilder.severity.duplicateRule", ResultSeverity.ERROR)); knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(knowledgeBuilderConfiguration); Resource drlFile = ResourceFactory.newClassPathResource("knowledge-builder-info-result.drl", this.getClass()); to do same for InFO but couldnt get it working.
public class KnowledgeResultErrorTest {
	private KnowledgeBuilder knowledgeBuilder;
	@Before
	public void setupKnowledgeBuilder() {
	    knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

	    Resource drlFile = ResourceFactory.newClassPathResource(
	            "../knowledge-builder-error-result.drl", this.getClass());
	    knowledgeBuilder.add(drlFile, ResourceType.DRL);
	}

	@Test
	public void shouldReturnAllErrorLevelMessagesFromDrlCompilation(){      
	    KnowledgeBuilderResults allErrorLevelMessages = knowledgeBuilder.getResults(ResultSeverity.ERROR);

	    assertThat(allErrorLevelMessages.size(), is(3));
	    assertTrue(knowledgeBuilder.hasResults(ResultSeverity.ERROR));

	    List<String> errorMessages = new ArrayList<String>();
	    List<Integer> lineNumbers = new ArrayList<Integer>();

	    for(Iterator<KnowledgeBuilderResult> knowledgeBuilderErrors = allErrorLevelMessages.iterator(); knowledgeBuilderErrors.hasNext();){
	        KnowledgeBuilderResult knowledgeBuilderResult = knowledgeBuilderErrors.next();
	        errorMessages.add(knowledgeBuilderResult.getMessage());
	        int[] lineNumbersFromResult = knowledgeBuilderResult.getLines();
	        lineNumbers.add(lineNumbersFromResult[0]);
	    }

	    assertThat(errorMessages.get(0), is("Error importing : 'a.b.c.NonExistingClassName'"));
	    assertThat(errorMessages.get(1), is("Rule Compilation error Only a type can be imported. a.b.c.NonExistingClassName resolves to a package"));
	    assertThat(errorMessages.get(2), is("Rule Compilation error Only a type can be imported. a.b.c.NonExistingClassName resolves to a package"));

	    assertTrue(lineNumbers.contains(1));
	    assertTrue(lineNumbers.contains(4));
	    assertTrue(lineNumbers.contains(12));

	    //Here we have obtained a list of all ERROR messages and the line numbers they appear on
	}

	 

}
