package gemcfadyen.drools_experimentation.declare;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class DeclareATypeInDroolsTest {
	private StatefulKnowledgeSession statefulSession;

	@Before
	public void setup(){
	    statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("declare-type.drl", this.getClass());
	}

	@Test
	public void shouldAddAPayementToThePensionListWhenPersonIsOver60(){
	    List<String> pensionPaymentList = new ArrayList<String>();

	    Calendar sixtyYearsAgoForPensionStartDate = Calendar.getInstance();
	    sixtyYearsAgoForPensionStartDate.set(1953, 1, 1);
	    Date datePensionAgeCalculatedFrom = sixtyYearsAgoForPensionStartDate.getTime();

	    statefulSession.setGlobal("pensionPayements", pensionPaymentList);
	    statefulSession.insert(datePensionAgeCalculatedFrom);

	    statefulSession.fireAllRules();

	    assertThat(pensionPaymentList.size(), is(1));
	    assertThat(pensionPaymentList.get(0), is("Pension given to Bertie"));

	}

}
