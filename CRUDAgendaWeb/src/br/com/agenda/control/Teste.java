package br.com.agenda.control;

import java.util.ArrayList;
import java.util.List;

import br.com.agenda.dao.ContatoDAO;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Telefone;
import br.com.agenda.model.TipoTelefone;

public class Teste {

	public static void main(String[] args) {
		
		ContatoDAO dao = new ContatoDAO();
		List<Telefone> listaTelefones = new ArrayList<Telefone>();
		
		//insert
		Contato contato = new Contato();
		contato.setId(1);
		contato.setNome("osvaldimar");
		
		Telefone tel = new Telefone();
		tel.setId(1);
		tel.setNumero("11 8531-9641");
		tel.setTipoTelefone(TipoTelefone.CELULAR);
		
		Telefone tel2 = new Telefone();
		tel2.setId(2);
		tel2.setNumero("11 4191-5421");
		tel2.setTipoTelefone(TipoTelefone.CASA);
		
		Telefone tel3 = new Telefone();
		tel3.setId(3);
		tel3.setNumero("11 4196-2827");
		tel3.setTipoTelefone(TipoTelefone.COMERCIAL);
		
		listaTelefones.add(tel);
		listaTelefones.add(tel2);	
		listaTelefones.add(tel3);
		contato.setTelefones(listaTelefones);
		
		dao.salvarContato(contato);
		/*
		//update
		Contato contatoAtualizar = new Contato();
		contatoAtualizar.setId(1);
		contatoAtualizar.setNome("osvaldimar update 2");
		Telefone tel = new Telefone();
		tel.setId(1);
		tel.setNumero("11 98888-9998");
		tel.setTipoTelefone(TipoTelefone.CELULAR);
		
		listaTelefones.add(tel);
		contatoAtualizar.setTelefones(listaTelefones);
		dao.atualizarContato(contatoAtualizar);
		/*
		//remove
		Contato c1 = new Contato();
		c1.setId(1);
		//Contato c2 = new Contato();
		//c2.setId(2);
		
		dao.removerContato(c1);
		*/
		
		
		//pesquisar nome		
		List<Contato> listaContatosNome = dao.listarContatosNome("osvaldimar");
		
		System.out.println("Listar contatos por nome");
		for(Contato c : listaContatosNome){
			System.out.print("Name Contato is = " + c.getNome());
			for(Telefone t : c.getTelefones()){
				System.out.print(" / Tel is = "+ t.getTipoTelefone() +" "+ t.getNumero());
			}			
		}
		System.out.println("\n-------------------");
		System.out.println();

		
		//pesquisar telefone
		List<Contato> listaContatosTelefone = dao.listarContatosTelefone("9641");
		
		System.out.println("Listar contatos por telefone");
		for(Contato c : listaContatosTelefone){
			System.out.print("Name Contato is = " + c.getNome());
			for(Telefone t : c.getTelefones()){
				System.out.print(" / Tel is = "+ t.getTipoTelefone() +" "+ t.getNumero());
			}			
		}
		System.out.println("\n-------------------");
		System.out.println();
		
		//listar todos
		List<Contato> listaContatos = dao.listarTodosContatos();
		
		System.out.println("Listar todos os contatos");
		for(Contato c : listaContatos){
			System.out.print("Name Contato is = " + c.getNome());
			for(Telefone t : c.getTelefones()){
				System.out.print(" / Tel is = "+ t.getTipoTelefone() +" "+ t.getNumero());
			}			
		}

	}

}
