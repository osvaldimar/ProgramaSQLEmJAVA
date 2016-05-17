package br.com.agenda.control;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.agenda.dao.ContatoDAO;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Telefone;
import br.com.agenda.model.TipoTelefone;


@ManagedBean(name="contatoVisualBean")
@SessionScoped
public class ContatoVisualizarBean {
	
	private Contato contatoVisual;
	private Telefone telefone1;
	private Telefone telefone2;
	private Telefone telefone3;	
	private List<Telefone> telefones;
	
	public ContatoVisualizarBean(){
		contatoVisual = new Contato();	
		telefone1 = new Telefone();
		telefone2 = new Telefone();
		telefone3 = new Telefone();
		telefones = new ArrayList<Telefone>();
	}
	
	/**
	 * Método recebe um int id para buscar o contato pelo id na base de dados e direciona para uma página de visualização para este contato
	 * @param id int
	 * @return String visualizar - página para ser redirecionada
	 */
	public String visualizarID(int id){
		contatoVisual = new Contato();	
		telefone1 = new Telefone();
		telefone2 = new Telefone();
		telefone3 = new Telefone();
		telefones = new ArrayList<Telefone>();
		
		ContatoDAO dao = new ContatoDAO();
		List<Contato> l = dao.listarContatoId(id);
		contatoVisual = l.get(0); //carrega novo contato para visualização
		
		
		for(int i = 0; i < contatoVisual.getTelefones().size(); i++){
			if(i == 0){
				telefone1 = contatoVisual.getTelefones().get(i);
			}else if(i == 1){
				telefone2 = contatoVisual.getTelefones().get(i);
			}else{
				telefone3 = contatoVisual.getTelefones().get(i);
			}
		}
		
		return "visualizar";
		
	}
	
	/**
	 * Método realiza as alterações dos contatos, é verificado a lista de telefones e validado campo nome em branco
	 * @return
	 */
	public String salvarAlteracoes(){
		
		System.out.println("tentando alterar contato");			
		
		//add os telefones na lista
		if( telefone1.getNumero() != null && !telefone1.getNumero().isEmpty() ){
			telefones.add(telefone1);
		}
		if( telefone2.getNumero() != null && !telefone2.getNumero().isEmpty() ){
			telefones.add(telefone2);
		}
		if( telefone3.getNumero() != null && !telefone3.getNumero().isEmpty() ){
			telefones.add(telefone3);
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		//validação do campo nome e telefone em branco
		if( contatoVisual.getNome() != null && !(contatoVisual.getNome().isEmpty()) ){
			if(!telefones.isEmpty()){
				try{
					ContatoDAO dao = new ContatoDAO();			
					contatoVisual.setTelefones(telefones);
					dao.atualizarContato(contatoVisual);
					
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterações OK","Contato alterado com sucesso");
					context.addMessage(null, msg);					
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){			
						
					}
					
					visualizarID(contatoVisual.getId());	//atualiza novo contato alterado pelo id na sessão
					
					return "visualizar";
							
				}catch(Exception e){
					System.out.println("Erro ao adicionar na base de dados");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro BD","Ocorreu um erro ao atualizar no banco de dados");
					context.addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Telefone não informado","Obrigatório adicionar pelo menos um telefone");
				context.addMessage(null, msg);
			}			
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Nome não informado", "Obrigatório informar o nome do contato");
			context.addMessage(null, msg);
		}
		
		return "";
	}
	
	/**
	 * Método responsável por remover o contato que está na sessão
	 * @return String - retorna String com nome da páginada de sucesso
	 */
	public String excluirContato(){
		//esclui contato que está na sessão de visualização
		
		System.out.println("tentando excluir contato");	
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			ContatoDAO dao = new ContatoDAO();			
			dao.removerContato(contatoVisual);
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK","Contato excluido com sucesso");
			context.addMessage(null, msg);					
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){			
				
			}
			
			return "remove_ok";
					
		}catch(Exception e){
			System.out.println("Erro ao excluir na base de dados");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro BD","Ocorreu um erro ao excluir no banco de dados");
			context.addMessage(null, msg);
		}
		
		return "";
		
	}
	
	
	//getters and setters

	public Contato getContatoVisual() {
		return contatoVisual;
	}

	public void setContatoVisual(Contato contatoVisual) {
		this.contatoVisual = contatoVisual;
	}

	public Telefone getTelefone1() {
		return telefone1;
	}

	public void setTelefone1(Telefone telefone1) {
		this.telefone1 = telefone1;
	}

	public Telefone getTelefone2() {
		return telefone2;
	}

	public void setTelefone2(Telefone telefone2) {
		this.telefone2 = telefone2;
	}

	public Telefone getTelefone3() {
		return telefone3;
	}

	public void setTelefone3(Telefone telefone3) {
		this.telefone3 = telefone3;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}
	

}
