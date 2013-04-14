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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((transactionType == null) ? 0 : transactionType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionBook other = (TransactionBook) obj;
		if (transactionType != other.transactionType)
			return false;
		return true;
	}

}
