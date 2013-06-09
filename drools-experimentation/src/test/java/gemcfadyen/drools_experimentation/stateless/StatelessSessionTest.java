package gemcfadyen.drools_experimentation.stateless;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.ArrayList;
import java.util.List;

import org.drools.command.CommandFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.Globals;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class StatelessSessionTest {
	private StatelessKnowledgeSession statelessSession;

	@Before
	public void setup() {
	    statelessSession = DroolsWorkingMemoryHelper.getStatelessWorkingMemoryUsingDroolsFile("stateless-session.drl", this.getClass());
	}

	@Test
	public void shouldReturnABirthdayMessageWhenPersonsBirthdateMatches() {
		BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayResult birthdayResult = new BirthdayResult();
	    statelessSession.setGlobal("birthdayMessage", birthdayResult);
	    statelessSession.execute(CommandFactory.newInsert(jojo));

	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday Jo-Jo"));
	}

	@Test
	public void shouldNotReturnABirthdayMessageIfItIsNotThePersonsBirthday() {
		BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/76");
	    BirthdayResult birthdayResult = new BirthdayResult();
	    statelessSession.setGlobal("birthdayMessage", birthdayResult);
	    statelessSession.execute(CommandFactory.newInsert(chacha));

	    assertThat(birthdayResult.getBirthdayMessage(), is(""));
	}

	@Test
	public void shouldObtainGlobalAfterRulesHaveFiredFromTheStatelessSession(){
		BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayResult birthdayResult = new BirthdayResult();
	    statelessSession.setGlobal("birthdayMessage", birthdayResult);
	    statelessSession.execute(CommandFactory.newInsert(jojo));

	    Globals allGlobals = statelessSession.getGlobals();
	    BirthdayResult globalFromSession = (BirthdayResult)allGlobals.get("birthdayMessage");

	    assertThat(globalFromSession.getBirthdayMessage(), is("Happy Birthday Jo-Jo"));
	}

	@Test
	public void shouldEvaluateSeveralPeopleInOneSwoopToFindOutIfTheirBirthdayMatches(){
	    List commandsForTheStatelessSession = new ArrayList();
	    BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/1976");
	    BirthdayPerson dada = new BirthdayPerson("Daddy", "25/10/1948");
	    BirthdayPerson mama = new BirthdayPerson("Mummy", "11/12/1948");
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(jojo, "jojo"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(chacha, "cha"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(dada, "dada"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(mama, "mammy"));

	    BirthdayResult birthdayResult = new BirthdayResult();
	    commandsForTheStatelessSession.add(CommandFactory.newSetGlobal("birthdayMessage", birthdayResult));

	    // Here you can not simply pass the collection in to execute()
	    // we must use the commandFactory to trigger the rules off because the collection is a collection of commands rather than POJO's 
	    //Ie the execute method only allows for a single command
	    statelessSession.execute(CommandFactory.newBatchExecution(commandsForTheStatelessSession));
	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday Jo-Jo"));
	}

	@Test
	public void shouldEvaluateACollectionOfObjectsInOneSwoopToFindOutIfTheirBirthdayMatches(){
	    List collectionOfPeople = new ArrayList();
	    BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/1976");
	    BirthdayPerson dada = new BirthdayPerson("Daddy", "25/10/1948");
	    BirthdayPerson mama = new BirthdayPerson("Mummy", "11/12/1948");
	    collectionOfPeople.add(jojo);
	    collectionOfPeople.add(chacha);
	    collectionOfPeople.add(dada);
	    collectionOfPeople.add(mama);

	    BirthdayResult birthdayResult = new BirthdayResult();
	    statelessSession.setGlobal("birthdayMessage", birthdayResult);

	    // Here we can just pass the collection of POJO's in directly because the Command Factory is not being used explicitly
	    statelessSession.execute(collectionOfPeople);
	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday Jo-Jo"));
	}


	//BatchExecution returns ExecutionResults when execute is called
	@Test
	public void shouldInspectTheExecutionResult(){
	    List commandsForTheStatelessSession = new ArrayList();
	    BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/1976");
	    BirthdayPerson dada = new BirthdayPerson("Daddy", "25/10/1948");
	    BirthdayPerson mama = new BirthdayPerson("Mummy", "11/12/1948");
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(jojo, "jojo"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(chacha, "cha"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(dada, "dada"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(mama, "mammy"));

	    BirthdayResult birthdayResult = new BirthdayResult();
	    commandsForTheStatelessSession.add(CommandFactory.newSetGlobal("birthdayMessage", birthdayResult));

	    // ExecutionResult is only avail when you use the CommandFactory in the execute method.
	    // Using execute(collection of pojo's) is a void method.
	    ExecutionResults sessionResults = statelessSession.execute(CommandFactory.newBatchExecution(commandsForTheStatelessSession));

	    //The identifiers relate to the 'out identifiers' specfied in the newInsert command -  ie: jojo, cha, dada, mammy
	    assertThat(sessionResults.getIdentifiers().size(), is(4));

	    //We can get the facts back from the session and check they are the same object reference as was passed in
	    BirthdayPerson jojoFromSession = (BirthdayPerson)sessionResults.getValue("jojo");
	    assertTrue(jojo == jojoFromSession);

	    BirthdayPerson chachaFromSession = (BirthdayPerson)sessionResults.getValue("cha");
	    assertTrue(chacha == chachaFromSession);

	    BirthdayPerson daddyFromSession = (BirthdayPerson)sessionResults.getValue("dada");
	    assertTrue(daddyFromSession == dada);

	    BirthdayPerson mummyFromSession = (BirthdayPerson)sessionResults.getValue("mammy");
	    assertTrue(mummyFromSession == mama);

	}


	//execute using new insert returns DefaultFactHandle

	 // @Test // public void shouldManipulateTheFactHandleAndReFireTheRules() { // Person jojo = new Person("JoJo", "29/05/82"); // BirthdayResult birthdayResult = new BirthdayResult(); // statelessSession.setGlobal("birthdayMessage", birthdayResult); // DefaultFactHandle sessionResults = statelessSession.execute(CommandFactory.newInsert(jojo)); // // assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday Jo-Jo")); //
	 // sessionResults.getEntryPoint().getEntryPointId(); // // sessionResults.getExternalForm(); // System.out.println(sessionResults.getExternalForm()); // System.out.println(sessionResults.getFirstLeftTuple().getFirstChild() ); //
	 // }

}
