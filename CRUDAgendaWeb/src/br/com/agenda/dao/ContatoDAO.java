package br.com.agenda.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.agenda.model.Contato;

public class ContatoDAO {
	
	private EntityManager getEntityManager() {
	    EntityManagerFactory factory = Persistence.createEntityManagerFactory("agendaweb");
	    EntityManager entityManager = factory.createEntityManager();

	    return entityManager;
	}
	
	public void salvarContato(Contato contato){
		
		EntityManager manager = getEntityManager();
		
		manager.getTransaction().begin();
		manager.persist(contato);		
		manager.getTransaction().commit();
		System.out.println("Contato adicionado com sucesso");

		manager.close();
	}
	
	public void atualizarContato(Contato contato){
		
		EntityManager manager = getEntityManager();
		
		manager.getTransaction().begin();		
		manager.merge(contato);
		manager.getTransaction().commit();
		System.out.println("Contato atualizado com sucesso");
		
		manager.close();
		
	}
	
	public void removerContato(Contato contato){
		
		EntityManager manager = getEntityManager();
		
		manager.getTransaction().begin();
		Contato contatoRemover = manager.find(Contato.class, contato.getId());
		manager.remove(contatoRemover);		
		manager.getTransaction().commit();
		System.out.println("Contato removido com sucesso");
		
		manager.close();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Contato> listarTodosContatos(){
		
		EntityManager manager = getEntityManager();
		
		String sql = "SELECT c FROM Contato c";
		Query query = manager.createQuery(sql);
		
		List<Contato> listaContatos = (List<Contato>) query.getResultList();
		
		System.out.println("Listando todos os contatos");
		
		return listaContatos;
	}

	@SuppressWarnings("unchecked")
	public List<Contato> listarContatosNome(String nome){
		
		EntityManager manager = getEntityManager();
		
		String sql = "SELECT c FROM Contato c where c.nome like '%"+ nome +"%'";
		Query query = manager.createQuery(sql);
		
		List<Contato> listaContatos = (List<Contato>) query.getResultList();
		
		System.out.println("Listando todos os contatos por nome");
		
		return listaContatos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Contato> listarContatosTelefone(String telefone){
		
		EntityManager manager = getEntityManager();
		
		String sql = "SELECT c FROM Contato c join Telefone t where t.numero like '%"+ telefone +"%'";
		Query query = manager.createQuery(sql);
		
		List<Contato> listaContatos = (List<Contato>) query.getResultList();
		
		System.out.println("Listando todos os contatos por telefone");
		
		return listaContatos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Contato> listarContatoId(int id){

		EntityManager manager = getEntityManager();
		
		String sql = "SELECT c FROM Contato c where c.id = "+ id;
		Query query = manager.createQuery(sql);
		
		List<Contato> listaContatos = (List<Contato>) query.getResultList();
		
		System.out.println("Lista contato por id");
		
		return listaContatos;
		
	}
	
}
