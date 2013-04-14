package gemcfadyen.drools_experimentation.bankaccount;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Using logicalInsertion model a bank account which adheres to the following rules :-
 * 1. You can deposit any positive value
 * 2. You can withdraw any amount
 * 3. If you withdraw more than your funds, a block is put on your account and no further withdrawals can take place
 * 4. Once the balance is back to 0 or above, the account becomes unblocked
 * 
 * @author Georgina
 */
public class BankAccountTest {
	
	private StatefulKnowledgeSession statefulSession;
	private KnowledgeRuntimeLogger logger;

	@Before
	public void setup(){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource drlFile = new ClassPathResource("../bank-account.drl", this.getClass());
		knowledgeBuilder.add(drlFile, ResourceType.DRL);
		
		if(knowledgeBuilder.hasErrors()){
			System.out.println("Errors in drl file " + knowledgeBuilder.getErrors());
		}
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulSession, "C:/Users/Georgina/Documents/GitHub/Drools/log/bankaccount");
	}
	
	@After
	public void tearDown(){
		logger.close();
		statefulSession.dispose();
	}
	
	@Test
	public void shouldDepositMoneyIntoTheBankAccount(){
		BankAccount bankAccount = new BankAccount();
		TransactionBook moneyToDeposit = new TransactionBook(100, TransactionType.DEPOSIT);
		statefulSession.insert(bankAccount);
		statefulSession.insert(moneyToDeposit);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(100));
	}

	
	@Test
	public void shouldWithdrawMoneyFromBankAccount(){
		BankAccount bankAccount = new BankAccount();
		bankAccount.deposit(100);
		TransactionBook moneyToWithdraw = new TransactionBook(5, TransactionType.WITHDRAW);
		statefulSession.insert(bankAccount);
		statefulSession.insert(moneyToWithdraw);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(95));
	}
	
	@Test
	public void shouldBlockAccountIfThereIsNoMoneyInAccount(){
		BankAccount bankAccount = new BankAccount();
		bankAccount.deposit(10);
		TransactionBook moneyToWithdraw = new TransactionBook(20, TransactionType.WITHDRAW);
		FactHandle accountFact = statefulSession.insert(bankAccount);
		FactHandle withdrawalFact = statefulSession.insert(moneyToWithdraw);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(-10));
		
		//Account should now be blocked - so lets try to withdraw some more money and check that the balance does not change

		statefulSession.update(withdrawalFact, new TransactionBook(5, TransactionType.WITHDRAW));
		statefulSession.update(accountFact, bankAccount);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(-10));
	}
	
	
}
