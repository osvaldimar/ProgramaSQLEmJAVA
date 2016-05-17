package br.com.agenda.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Contato
 *
 */
@Entity

public class Contato implements Serializable, Comparable<Contato> {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)  
    @Column(nullable=false) 
	private int id;
	
	@Column(nullable=false) 
	private String nome;
	
	private String email;
	
	private String endereco;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)  
    @JoinTable(name = "contato_telefone", joinColumns = { @JoinColumn(name = "id_contato") }, 
    inverseJoinColumns = { @JoinColumn(name = "id_telefone") })
	private List<Telefone> telefones;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getTodosTelefones() {
		String tels = "";
		for(Telefone t : telefones){
			tels += (tels.isEmpty() ? "" : " / ") + t.getNumero();
		}
		return tels;
	}
	
	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Override
	public int compareTo(Contato o) {
		String nome = o.getNome();
		return this.getNome().compareTo(nome);		
	}
	
		
}
