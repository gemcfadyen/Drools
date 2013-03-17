package gemcfadyen.drools_experimentation.firealarm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FireAlarmTest {
	private StatefulKnowledgeSession statefulSession;
	private Room livingRoom;
	private Room kitchen;
	private Room bedroom;

	Sprinkler livingRoomSprinkler;
	Sprinkler kitchenSprinkler;
	Sprinkler bedroomSprinkler;

	@Before
	public void setup() {
		createRooms();
		createSprinklers();

		statefulSession = DroolsWorkingMemoryHelper
				.getStatefulWorkingMemoryUsingDroolsFile("fireAlarm.drl",
						getClass());

		insertRoomsAndSprinklersToStatefulSession();
	}

	private void createRooms() {
		livingRoom = new Room("living-room");
		kitchen = new Room("kitchen");
		bedroom = new Room("bedroom");
	}

	private void insertRoomsAndSprinklersToStatefulSession() {
		statefulSession.insert(livingRoom);
		statefulSession.insert(kitchen);
		statefulSession.insert(bedroom);
		statefulSession.insert(livingRoomSprinkler);
		statefulSession.insert(kitchenSprinkler);
		statefulSession.insert(bedroomSprinkler);
	}

	private void createSprinklers() {
		livingRoomSprinkler = new Sprinkler(livingRoom, false);
		kitchenSprinkler = new Sprinkler(kitchen, false);
		bedroomSprinkler = new Sprinkler(bedroom, false);
	}

	@After
	public void tearDown() {
		statefulSession.dispose();
	}
	
	@Test
	public void theSprinklersShouldAllBeOffIfNoFireIsPresent() {
		statefulSession.fireAllRules();

		assertThat(kitchenSprinkler.getIsOn(), is(false));
		assertThat(livingRoomSprinkler.getIsOn(), is(false));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
	}

	@Test
	public void theSprinklerShouldComeOnInTheRoomWhichIsOnFire() {
		Fire kitchenFire = new Fire(kitchen);
		statefulSession.insert(kitchenFire);
		
		statefulSession.fireAllRules();

		assertThat(kitchenSprinkler.getIsOn(), is(true));
		assertThat(livingRoomSprinkler.getIsOn(), is(false));
		assertThat(bedroomSprinkler.getIsOn(), is(false));

	}
	
	@Test
	public void whenThereAreTwoFiresTwoSprinklersShouldBeSwitchedOn(){
		Fire kitchenFire = new Fire(kitchen);
		Fire livingRoomFire = new Fire(livingRoom);
		
		//keeping a FactHandle allows you to retract/modify the objects at a later time
		FactHandle kitchenFireHandle = statefulSession.insert(kitchenFire);
		FactHandle livingRoomFireHandle = statefulSession.insert(livingRoomFire);
		
		assertThat(kitchenSprinkler.getIsOn(), is(false));
		assertThat(livingRoomSprinkler.getIsOn(), is(false));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
		
		statefulSession.fireAllRules();
		assertThat(kitchenSprinkler.getIsOn(), is(true));
		assertThat(livingRoomSprinkler.getIsOn(), is(true));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
		
		//lets modify the session - lets extinguish a fire
		statefulSession.retract(kitchenFireHandle);
		statefulSession.fireAllRules();
		assertThat(kitchenSprinkler.getIsOn(), is(false));
		assertThat(livingRoomSprinkler.getIsOn(), is(true));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
		
		//lets put out the living room fire 
		statefulSession.retract(livingRoomFireHandle);
		statefulSession.fireAllRules();
		
		assertThat(kitchenSprinkler.getIsOn(), is(false));
		assertThat(livingRoomSprinkler.getIsOn(), is(false));
		assertThat(bedroomSprinkler.getIsOn(), is(false));
		
	}


}
