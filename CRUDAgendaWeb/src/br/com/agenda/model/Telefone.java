package br.com.agenda.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Telefone
 *
 */
@Entity

public class Telefone implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)  
    @Column(nullable=false) 
	private int id;
	
	@Column(nullable=false) 
	private String numero;
	
	@Column(name="tipo_tel")
	private TipoTelefone tipoTelefone;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public TipoTelefone getTipoTelefone() {
		return tipoTelefone;
	}

	public void setTipoTelefone(TipoTelefone tipoTelefone) {
		this.tipoTelefone = tipoTelefone;
	}

	
   
}
