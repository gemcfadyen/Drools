package gemcfadyen.drools_experimentation.templates;

import java.util.EnumSet;

public class RangeParamSet {
	private String field;
	private int lower;
	private int upper;
	EnumSet<ItemCode> codes;
	
	public RangeParamSet(String field, int lower, int upper, EnumSet<ItemCode> codeSet) {
				this.field = field;
				this.lower = lower;
				this.upper = upper;
				this.codes = codeSet;
			}	

		public void setCodes(EnumSet<ItemCode> codes) {
				this.codes = codes;
			}

		public String getCodes(){
				StringBuilder result = new StringBuilder();
				String deliminator = "";
				
				for(ItemCode itemCode: codes){
					result.append(deliminator).append(" == ItemCode.").append(itemCode);
					deliminator = " ||";
				}
				
				return result.toString();
			}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public int getLower() {
			return lower;
		}

		public void setLower(int lower) {
			this.lower = lower;
		}

		public int getUpper() {
			return upper;
		}

		public void setUpper(int upper) {
			this.upper = upper;
		}



}
