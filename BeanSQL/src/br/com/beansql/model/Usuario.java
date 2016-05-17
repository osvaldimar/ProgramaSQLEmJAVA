
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;

public class Usuario implements Serializable {

	private String name;
	private String password;
	
	public Usuario(String name, String password) {	
		
		this.name = name;
		this.password = password;
		
	}

	
	//Getters and Setters
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
