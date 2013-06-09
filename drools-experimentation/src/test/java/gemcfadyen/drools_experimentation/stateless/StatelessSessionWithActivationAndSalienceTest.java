package gemcfadyen.drools_experimentation.stateless;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryHelper;

import java.util.ArrayList;
import java.util.List;

import org.drools.command.CommandFactory;
import org.drools.runtime.Globals;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

public class StatelessSessionWithActivationAndSalienceTest {
	//This test uses a drl which checks for jojo's and chacha's birthdays. 
	//If it is not the persons birthday a commiseration message is displayed for that person 
	// We use activation groups so that you dont get the commiseration message if it is also your birthday.
    //Ie: Using activations, as soon as one //rule on that activation resolves to true, the rest of the rules are cancelled. 
	//Because we want to print out happy birthday for more than one person (if it is both their birthdays) we put each rule on a different activation group, otherwise you 
	//would only get a message for the person who was evaluated first 
	private StatelessKnowledgeSession statelessSession;
	
	@Before
	public void setup() {
	    statelessSession = DroolsWorkingMemoryHelper.getStatelessWorkingMemoryUsingDroolsFile("stateless-with-activations.drl", this.getClass());
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

	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday ChaCha"));
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
	    BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/76");
	    BirthdayPerson dada = new BirthdayPerson("Daddy", "25/10/48");
	    BirthdayPerson mama = new BirthdayPerson("Mummy", "11/12/50");
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(jojo, "jojo"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(chacha, "cha"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(dada, "dada"));
	    commandsForTheStatelessSession.add(CommandFactory.newInsert(mama, "mammy"));

	    BirthdayResult birthdayResult = new BirthdayResult();
	    commandsForTheStatelessSession.add(CommandFactory.newSetGlobal("birthdayMessage", birthdayResult));

	    // Here you can not simply pass the collection in to execute()
	    // we must use the commandFactory to trigger the rules off because the collection is a collection of commands rather than POJO's 
	    statelessSession.execute(CommandFactory.newBatchExecution(commandsForTheStatelessSession));
	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday ChaChaHappy Birthday Jo-JoSorry no birthday greeting for you MummySorry no birthday greeting for you Daddy"));
	}

	@Test
	public void shouldEvaluateACollectionOfObjectsInOneSwoopToFindOutIfTheirBirthdayMatches(){
	    List collectionOfPeople = new ArrayList();
	    BirthdayPerson jojo = new BirthdayPerson("JoJo", "29/05/82");
	    BirthdayPerson chacha = new BirthdayPerson("ChaCha", "27/12/76");
	    BirthdayPerson dada = new BirthdayPerson("Daddy", "25/10/48");
	    BirthdayPerson mama = new BirthdayPerson("Mummy", "11/12/50");
	    collectionOfPeople.add(jojo);
	    collectionOfPeople.add(chacha);
	    collectionOfPeople.add(dada);
	    collectionOfPeople.add(mama);

	    BirthdayResult birthdayResult = new BirthdayResult();
	    statelessSession.setGlobal("birthdayMessage", birthdayResult);

	    // Here we can just pass the collection of POJO's in directly because the Command Factory is not being used explicitly
	    statelessSession.execute(collectionOfPeople);
	    assertThat(birthdayResult.getBirthdayMessage(), is("Happy Birthday ChaChaHappy Birthday Jo-JoSorry no birthday greeting for you MummySorry no birthday greeting for you Daddy"));
	}

}
