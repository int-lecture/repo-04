package var.chatclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	
	static Map<String,User> map = new HashMap<>();
	
	List<Message> list = new ArrayList();
	
	private String name;
	
	public int sequence;
	
	public User(String name){
		this.name=name;
	}
	
	
	
	
	
}
