package nlp.seastar.emails;

import java.util.ArrayList;

public class Email {
	private String id;
	private EmailCategory category;
	private ArrayList<String> tokens;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EmailCategory getCategory() {
		return category;
	}

	public void setCategory(EmailCategory category) {
		this.category = category;
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}

	public Email() {
		this.tokens = new ArrayList<String>();
	}

	public Email(String id) {
		super();
		this.id = id;
		this.category = EmailCategory.HAM;
		this.setTokens(new ArrayList<String>());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Email other = (Email) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Email [id=" + id + ", category=" + category + "]";
	}

}
