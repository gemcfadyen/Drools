package gemcfadyen.drools_experimentation.queries;

import java.util.ArrayList;
import java.util.List;

public class Followers {
	 private List<Follower> followers = new ArrayList<Follower>();

	public List<Follower> getFollowers() {
		return followers;
	}

	public void setFollowers(List<Follower> followers) {
		this.followers = followers;
	}
	
	public void addFollower(Follower follower){
		followers.add(follower);
	}
}
