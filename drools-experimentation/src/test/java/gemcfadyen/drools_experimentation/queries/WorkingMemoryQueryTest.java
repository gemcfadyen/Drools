package gemcfadyen.drools_experimentation.queries;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkingMemoryQueryTest {

	private StatefulKnowledgeSession statefulSession;
	private Followers followers;
	
	@Before
	public void setup(){
		statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("query.drl", this.getClass());
		followers = setupFollowers();
		statefulSession.insert(followers);
	}

	@Test
	public void shouldFindTheGetAllFollowersQueryOnTheDrlAndExecuteItReturningAListOfFollowers(){
		QueryResults queryResults = statefulSession.getQueryResults("get all followers");
		Followers allFollowersReturnedFromQuery = null;
		
		for(QueryResultsRow result : queryResults){
			Object resultObject = result.get("followers");
		    allFollowersReturnedFromQuery = (Followers)resultObject;
		}
		
		assertNotNull(allFollowersReturnedFromQuery);
		assertThat(allFollowersReturnedFromQuery.getFollowers().size(), is(2));
		
		Follower one = allFollowersReturnedFromQuery.getFollowers().get(0);
		Follower two = allFollowersReturnedFromQuery.getFollowers().get(1);
		
		Assert.assertTrue(one.equals(new Follower("Fred", "London")));
		Assert.assertTrue(two.equals(new Follower("Bill", "France")));

	}
	
	private Followers setupFollowers() {
		Follower fred = new Follower("Fred", "London");
		Follower bill = new Follower("Bill", "France");
		
		followers = new Followers();
		followers.addFollower(fred);
		followers.addFollower(bill);
		return followers;
	}
}
