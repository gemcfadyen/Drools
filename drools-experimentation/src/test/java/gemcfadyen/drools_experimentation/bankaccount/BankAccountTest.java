package gemcfadyen.drools_experimentation.bankaccount;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import gemcfadyen.drools_experimentation.DroolsWorkingMemoryEventListener;

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
	private DroolsWorkingMemoryEventListener droolsWorkingMemoryListener = new DroolsWorkingMemoryEventListener();

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
		statefulSession.addEventListener(droolsWorkingMemoryListener);
		logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulSession, "C:/Users/Georgina/Documents/GitHub/Drools/log/bankaccount");
		
	}
	
	@After
	public void tearDown(){
		logger.close();
		statefulSession.dispose();
	}
	
	@Test
	public void shouldDepositMoneyIntoTheBankAccount(){
		BankAccount bankAccount = new BankAccount("Gee");
		TransactionBook moneyToDeposit = new TransactionBook(100, TransactionType.DEPOSIT);
		statefulSession.insert(bankAccount);
		statefulSession.insert(moneyToDeposit);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(100));
	}

	
	@Test
	public void shouldWithdrawMoneyFromBankAccount(){
		BankAccount bankAccount = new BankAccount("Gee");
		bankAccount.deposit(100);
		TransactionBook moneyToWithdraw = new TransactionBook(5, TransactionType.WITHDRAW);
		statefulSession.insert(bankAccount);
		statefulSession.insert(moneyToWithdraw);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(95));
	}
	
	@Test
	public void shouldBlockAccountIfThereIsNoMoneyInAccount(){
		BankAccount bankAccount = createBankAccountWIthABalanceOfTenPounds();
		TransactionBook moneyToWithdraw = new TransactionBook(20, TransactionType.WITHDRAW);
		FactHandle accountFact = statefulSession.insert(bankAccount);
		FactHandle withdrawalFact = statefulSession.insert(moneyToWithdraw);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(-10));
		
		//Account should now be blocked - so lets try to withdraw some more money and check that the balance does not change

		statefulSession.update(withdrawalFact, new TransactionBook(5, TransactionType.WITHDRAW));
		
		//In order to get the 'block' rule on the activation list, it is necessary to re-insert the bankAccount. Otherwise, it will not be triggered
		statefulSession.update(accountFact, bankAccount);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(-10));
	}
	
	
	@Test
	public void shouldAutomaticallyUnblockAccountIfEnoughMoneyIsDepositedAfterBeingOverdrawn(){
		BankAccount bankAccount = createBankAccountWIthABalanceOfTenPounds();
		TransactionBook moneyToWithdraw = new TransactionBook(20, TransactionType.WITHDRAW);
		
		FactHandle bankAccountFactHandle = statefulSession.insert(bankAccount);
		FactHandle firstWithdrawal = statefulSession.insert(moneyToWithdraw);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(-10));
		System.out.println("******************************* now try to withdraw another 10 - should not work as account is overdrawn ****");
		
		//Account should now be blocked - so lets try to withdraw some more money and check that the balance does not change
		//update the withdrawalFact to withdraw a new amount (this will also prevent > 1 withdrawal TransactionBook being 
		//in the session and thus being evaluated each time the rules are fired.
		moneyToWithdraw.setAmountForTransaction(5);
		statefulSession.update(firstWithdrawal, moneyToWithdraw);
		statefulSession.update(bankAccountFactHandle, bankAccount);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(-10));
		System.out.println("******************************* now deposit 5, should change balance ****");
		
		//lets remove the withdrawal fact so the session doesnt try to re-evaluate it, and insert a deposit
		statefulSession.retract(firstWithdrawal);
		
		//a deposit of 5 is not enough to unblock the account, but is a start!
		TransactionBook depositMoney = new TransactionBook(5, TransactionType.DEPOSIT);
		FactHandle firstDepositFactHandle = statefulSession.insert(depositMoney);
		statefulSession.update(bankAccountFactHandle, bankAccount);
		statefulSession.fireAllRules();
		
		assertThat(bankAccount.getAccountBalance(), is(-5));
		System.out.println("******************************* now try to withdraw 5 - nothing should change as account is still overdrawn");
		
		//We will retract the deposit transactionBook as it is still in the session and would be re-evaluated when we fire all the rules.
		statefulSession.retract(firstDepositFactHandle);
		//insert a new withdrawal transaction book
		FactHandle secondWithdrawalFactHandle = statefulSession.insert(new TransactionBook(5, TransactionType.WITHDRAW));
		System.out.println("account balance " + bankAccount.getAccountBalance());
		statefulSession.update(bankAccountFactHandle, bankAccount);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(-5));
		System.out.println("******************************* now deposit 10, balance should change and withdrawals should be re-instated");
	
		//Lets deposit some more money to take us into the green so we insert a new Deposit Transaction Book
		statefulSession.retract(secondWithdrawalFactHandle);
		FactHandle secondDepositFactHandle = statefulSession.insert(new TransactionBook(10, TransactionType.DEPOSIT));
		statefulSession.update(bankAccountFactHandle, bankAccount);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(5));
		
		//Now we are at a healthy balance - we should be able to withdraw again as the block should have been automatically retracted from the session 
		//the balance changing on a withdraw proves that the Block has been automatically retracted
		statefulSession.retract(secondDepositFactHandle);
		statefulSession.insert(new TransactionBook(2, TransactionType.WITHDRAW));
		statefulSession.update(bankAccountFactHandle, bankAccount);
		statefulSession.fireAllRules();
		assertThat(bankAccount.getAccountBalance(), is(3));
	}

	private BankAccount createBankAccountWIthABalanceOfTenPounds() {
		BankAccount bankAccount = new BankAccount("Gee");
		bankAccount.deposit(10);
		return bankAccount;
	}
	
	
	
	
}
