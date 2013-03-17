package gemcfadyen.drools_experimentation.firealarm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FireAlarmTest {
private StatefulKnowledgeSession statefulSession;
	
	@Before
	public void setup(){
	 statefulSession = DroolsWorkingMemoryHelper.getStatefulWorkingMemoryUsingDroolsFile("fireAlarm.drl", getClass());
	}
	
	@After
	public void tearDown(){
		statefulSession.dispose();
	}
	
	@Test
	public void theSprinklerShouldComeOnInTheRoomWhichIsOnFire(){
		Room livingRoom = new Room("living-room");
		Room kitchen = new Room("kitchen");
		Room bedroom = new Room("bedroom");
		
		Fire kitchenFire = new Fire(kitchen);
		
		Sprinkler livingRoomSprinkler = new Sprinkler(livingRoom, false);
		Sprinkler kitchenSprinkler = new Sprinkler(kitchen, false);
		Sprinkler bedroomSprinkler = new Sprinkler(bedroom, false);
		
		statefulSession.insert(livingRoom);
		statefulSession.insert(kitchen);
		statefulSession.insert(bedroom);
		statefulSession.insert(livingRoomSprinkler);
		statefulSession.insert(kitchenSprinkler);
		statefulSession.insert(bedroomSprinkler);
		statefulSession.insert(kitchenFire);		
		statefulSession.fireAllRules();
		
		assertThat(kitchenSprinkler.getIsOn(), is(true));
		assertThat(livingRoomSprinkler.getIsOn(), is(false));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
	

	}
}
