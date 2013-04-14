package gemcfadyen.drools_experimentation.bankaccount;

public class TransactionBook {
	private int amountForTransaction;
	private TransactionType transactionType;

	public int getAmountForTransaction() {
		return amountForTransaction;
	}

	public void setAmountForTransaction(int amountForTransaction) {
		this.amountForTransaction = amountForTransaction;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	TransactionBook(int amountForTransaction, TransactionType transactionType) {
		this.amountForTransaction = amountForTransaction;
		this.transactionType = transactionType;
	}

}
