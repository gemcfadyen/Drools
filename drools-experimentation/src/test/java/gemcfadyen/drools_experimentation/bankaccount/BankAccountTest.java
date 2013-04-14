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
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;


/**
 * Using logicalInsertion model a bank account which adheres to the following rules :-
 * 1. You can deposit any positive value
 * 2. You can withdraw any amount
 * 3. If you withdraw more than your funds, a block is put on your account
 * 4. Once the balance is back to 0 or above, the account becomes unblocked
 * 
 * @author Georgina
 */
public class BankAccountTest {
	
	private StatefulKnowledgeSession statefulSession;

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
		
	}
}
