package gemcfadyen.drools_experimentation.bankaccount;

public class BankAccount {
	private int accountBalance;

	public int getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}

	public void deposit(int amount) {
		this.accountBalance = accountBalance + amount;
	}

	public void withdraw(int amount) {
		System.out.println("Withdrawing " + amount);
		this.accountBalance = accountBalance - amount;
		System.out.println("balance " + accountBalance);
	}

}
