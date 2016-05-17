package br.com.agenda.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.agenda.dao.ContatoDAO;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Telefone;

@ManagedBean(name="contatoPesquisaBean")
@ViewScoped
public class ContatoPesquisaBean {
	
	private List<Contato> listaContatos;		//lista de contatos
	private List<Contato> listaContatosFiltro;	//lista para filtrar os contatos no componente datatable
	
	public ContatoPesquisaBean(){
		
		listaContatos  = new ArrayList<Contato>();
		pesquisarTodosContatos();
		
	}
	
	/**
	 * Método faz a busca de todos os contatos na base de dados e armazena na lista de contatos do controle bean 
	 * Também é realizado a ordenando pelo nome do contato
	 */
	public void pesquisarTodosContatos(){
		
		ContatoDAO dao = new ContatoDAO();	
		listaContatos = dao.listarTodosContatos();
		Collections.sort(listaContatos);
		
	}
	
	
	//gettes and setters
	
	public List<Contato> getListaContatos() {
		return listaContatos;
	}
	public void setListaContatos(List<Contato> listaContatos) {
		this.listaContatos = listaContatos;
	}
	public List<Contato> getListaContatosFiltro() {
		return listaContatosFiltro;
	}
	public void setListaContatosFiltro(List<Contato> listaContatosFiltro) {
		this.listaContatosFiltro = listaContatosFiltro;
	}
	
	
	

}
