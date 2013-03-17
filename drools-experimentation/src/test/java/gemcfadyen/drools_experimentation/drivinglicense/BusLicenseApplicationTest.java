package gemcfadyen.drools_experimentation.drivinglicense;

import static gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper.getStatelessWorkingMemoryUsingDroolsFile;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.TUESDAY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class BusLicenseApplicationTest {
	private StatelessKnowledgeSession statelessSession;
	
	@Before
	public void setup(){
		statelessSession = getStatelessWorkingMemoryUsingDroolsFile("busApplication.drl", getClass());
	}
	
	@Test
	public void shouldReturnEligableIfApplicationWasSubmittedBeforeCutoffDate(){
		Application applicationForm = new Application(getApplicationDateOf(TUESDAY,AUGUST,2010), false);
		Driver busDriver = new Driver("Fred", 45);
		
		statelessSession.execute(Arrays.asList(applicationForm, busDriver));
		
		assertThat(applicationForm.getIsEligable(), is(true));
	}
	
	@Test
	public void shouldReturnNotEligableIfApplicationWasSubmittedAfterCutoffDate(){
		Application applicationForm = new Application(getApplicationDateOf(MONDAY,JUNE,2012), false);
		Driver busDriver = new Driver("Terry", 33);
		
		statelessSession.execute(Arrays.asList(applicationForm, busDriver));
		
		assertThat(applicationForm.getIsEligable(), is(false));
	}
	
	private Date getApplicationDateOf(int day, int month, int year){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		Date date = new Date(calendar.getTimeInMillis());
		return date;
	}

}
