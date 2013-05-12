package gemcfadyen.drools_experimentation.helloworld;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Global;
import org.drools.definition.rule.Query;
import org.drools.definition.rule.Rule;
import org.drools.definition.type.FactType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class PackageInspectionTest {
	@Test
	public void shouldInspectThePackageInformation() {

	    KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	    Resource drlFile = ResourceFactory.newClassPathResource( "../hello-world.drl", this.getClass());
	    knowledgeBuilder.add(drlFile, ResourceType.DRL);

	    Collection<KnowledgePackage> knowledgePackages = knowledgeBuilder.getKnowledgePackages();

	    Collection<Rule> rules = null;
	    Collection<Query> queries = null;
	    Collection<org.drools.definition.process.Process> process = null;
	    String name = null;
	    Collection<Global> globals = null;
	    Collection<String> function = null;
	    Collection<FactType> facttypes = null;

	    for (KnowledgePackage packet : knowledgePackages) {
	        facttypes = packet.getFactTypes();
	        function = packet.getFunctionNames();
	        globals = packet.getGlobalVariables();
	        name = packet.getName();
	        process = packet.getProcesses();
	        queries = packet.getQueries();
	        rules = packet.getRules();
	    }

	    assertThat(name, is("gemcfadyen.drools_experimentation"));

	    for (FactType fact : facttypes) {
	        System.out.println("fact " + fact.getName());
	    }

	    assertThat(rules.size(), is(2));
	    Object[] ruleArray = rules.toArray();
	    Rule firstRule = (Rule) ruleArray[0];
	    Rule secondRule = (Rule) ruleArray[1];
	    assertThat(firstRule.getName(), is("Hello World"));
	    assertThat(firstRule.getNamespace(),is("gemcfadyen.drools_experimentation"));
	    assertThat(firstRule.getPackageName(),is("gemcfadyen.drools_experimentation"));

	    assertThat(secondRule.getName(), is("Goodbye"));
	    assertThat(secondRule.getNamespace(),is("gemcfadyen.drools_experimentation"));
	    assertThat(secondRule.getPackageName(),is("gemcfadyen.drools_experimentation"));

	    assertThat(globals.size(), is(1));
	    Object[] globalArray = globals.toArray();
	    Global global = (Global) globalArray[0];
	    assertThat(global.getName(), is("list"));
	    assertThat(global.getType(), is("java.util.List"));

	    assertThat(process.size(), is(0));
	    assertThat(queries.size(), is(0));
	    assertThat(function.size(), is(0));
	    assertThat(facttypes.size(), is(0));

	}

	//According to the DroolsExpert documentation it is not possible to add one knowledgepackage to two knowledge bases.  (Chapter 3.2.1) 
	//This test has let me add one package to two knowledge bases.
	@Test
	public void shouldNotAddAPackageToTwoKnowledgeBuilders() {
	    KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	    Resource drlFile = ResourceFactory.newClassPathResource( "../hello-world.drl", this.getClass());
	    knowledgeBuilder.add(drlFile, ResourceType.DRL);

	    Collection<KnowledgePackage> knowledgePackages = knowledgeBuilder.getKnowledgePackages();

	    KnowledgeBase one = KnowledgeBaseFactory.newKnowledgeBase();
	    KnowledgeBase two = KnowledgeBaseFactory.newKnowledgeBase();

	    one.addKnowledgePackages(knowledgePackages);
	    StatefulKnowledgeSession firstSession = one
	            .newStatefulKnowledgeSession();
	    List firstList = new ArrayList();
	    Message firstMessage = new Message();
	    firstMessage.setStatus(Message.HELLO);
	    firstSession.setGlobal("list", firstList);
	    firstSession.insert(firstMessage);
	    firstSession.fireAllRules();
	    assertThat(firstList.size(), is(1));

	    two.addKnowledgePackages(knowledgePackages);
	    StatefulKnowledgeSession secondSession = two
	            .newStatefulKnowledgeSession();

	    List secondList = new ArrayList();
	    Message secondMessage = new Message();
	    secondMessage.setStatus(Message.HELLO);
	    secondSession.setGlobal("list", secondList);

	    secondSession.insert(secondMessage);
	    secondSession.fireAllRules();
	    assertThat(secondList.size(), is(1));

	    Collection<? extends WorkingMemoryEntryPoint> entryPoints = (Collection<? extends WorkingMemoryEntryPoint>) secondSession.getWorkingMemoryEntryPoints();
	    assertThat(entryPoints.size(), is(1));
	    Object[] entryPointArray = entryPoints.toArray();
	    WorkingMemoryEntryPoint entryPoint = (WorkingMemoryEntryPoint) entryPointArray[0];
	    assertThat(entryPoint.getFactCount(), is(1L));

	}

}
