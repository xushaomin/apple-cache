package com.appleframework.cache.redis;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 4474048784930020918L;
	
	private String name;
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	public User(){}
	
	public User(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}

	public static User create(String name, Integer age) {
		return new User(name, age);
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + "]";
	}

}
