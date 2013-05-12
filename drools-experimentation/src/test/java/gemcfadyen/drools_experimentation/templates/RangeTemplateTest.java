package gemcfadyen.drools_experimentation.templates;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class RangeTemplateTest {
	private TemplateExpander expander = new TemplateExpander();
	private StatefulKnowledgeSession statefulKnowledgeSession;

	@Before
	public void setup() throws Exception {

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();	
		InputStream inputStream = this.getClass().getResourceAsStream("../template-range.drl");
		
		Collection<RangeParamSet> paramtersForTemplate = new ArrayList<RangeParamSet>();

		paramtersForTemplate.add(new RangeParamSet("weight", 10, 99, EnumSet.of(ItemCode.LOCK, ItemCode.STOCK))); // field, lower, upper, codeSet
		paramtersForTemplate.add(new RangeParamSet("price", 10, 50, EnumSet.of(ItemCode.BARREL)));

		knowledgeBase = expander.getKnowledgeBaseInitialisedWithTemplatedDrl(knowledgeBase, inputStream, paramtersForTemplate);
		statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
	}

	//various objects are passed into the stateful session. They are evaluated against the knowledge base which contains the logic in the generated templated drl.
	//those objects that meet the criteria are written out to the global variable.
	//eg:  Item D weight in range: 85 code: LOCK
	//     Item B weight in range: 44 code: STOCK	
	@Test
	public void shouldGenerateTemplate() {
		Result returnedMessage = new Result();
		statefulKnowledgeSession.insert(new Item("A", 130, 42, ItemCode.LOCK)); // name, weight, price, code
		statefulKnowledgeSession.insert(new Item("B", 44, 44, ItemCode.STOCK));
		statefulKnowledgeSession.insert(new Item("C", 123, 180, ItemCode.BARREL));
		
		statefulKnowledgeSession.setGlobal("returnedMessage", returnedMessage);
		statefulKnowledgeSession.insert(new Item("D", 85, 9, ItemCode.LOCK));

		statefulKnowledgeSession.fireAllRules();
		assertThat(returnedMessage.getMessage(), is("Item D weight in range: 85 code: LOCK*Item B weight in range: 44 code: STOCK*"));

	}

}
