package nlp.seastar.baseline;

public class Word {
	private String word;
	private int hamCount;
	private int spamCount;

	public Word() {
		this.hamCount = 0;
		this.spamCount = 0;
	}
	
	public Word(boolean isHam, String word)
	{
		this.word = word;
		if(isHam){
			this.hamCount = 1;
			this.spamCount = 0;
		}else{
			this.spamCount = 1;
			this.hamCount = 0;
		}
	}

	public Word(String word, int hamCount, int spamCount) {
		super();
		this.word = word;
		this.hamCount = hamCount;
		this.spamCount = spamCount;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getHamCount() {
		return hamCount;
	}

	public void setHamCount(int hamCount) {
		this.hamCount = hamCount;
	}

	public int getSpamCount() {
		return spamCount;
	}
	
	public void incrementSpamCount(){
		spamCount++;
	}
	
	public void incrementHamCount(){
		hamCount++;
	}

	public void setSpamCount(int spamCount) {
		this.spamCount = spamCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		Word other = (Word) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", hamCount=" + hamCount + ", spamCount="
				+ spamCount + "]";
	}

}
