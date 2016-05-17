package br.com.agenda.control;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import br.com.agenda.dao.ContatoDAO;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Telefone;
import br.com.agenda.model.TipoTelefone;


@ManagedBean(name="contatoBean")
@RequestScoped
public class ContatoControlBean {

	private Contato contato;
	private Telefone telefone1;
	private Telefone telefone2;
	private Telefone telefone3;	
	private TipoTelefone tipoTelefone1;
	private TipoTelefone tipoTelefone2;
	private TipoTelefone tipoTelefone3;
	private List<Telefone> telefones;
	
	public ContatoControlBean(){
		contato = new Contato();
		telefone1 = new Telefone();
		telefone2 = new Telefone();
		telefone3 = new Telefone();
		telefones = new ArrayList<Telefone>();
	}
	
	public void limparForm(){
		contato = new Contato();
		telefone1 = new Telefone();
		telefone2 = new Telefone();
		telefone3 = new Telefone();
		telefones = new ArrayList<Telefone>();
	}
	
	/**
	 * Método adiciona um novo contato na base de dados
	 * É feito a verificação dos telefones informados e adiciona a lista de telefones no contato
	 * @return
	 */
	public String adicionarContato(){
		
		System.out.println("tentando adicionar");			
		
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
		
		FacesContext contex = FacesContext.getCurrentInstance();
		
		//validação do campo nome e telefone em branco
		if( contato.getNome() != null && !(contato.getNome().isEmpty()) ){
			if(!telefones.isEmpty()){
				try{
					ContatoDAO dao = new ContatoDAO();			
					contato.setTelefones(telefones);
					dao.salvarContato(contato);
					
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastro OK","Contato adicionado com sucesso");
					contex.addMessage(null, msg);					
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){			
						
					}
					return "cadastro_ok";
							
				}catch(Exception e){
					System.out.println("Erro ao adicionar na base de dados");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro BD","Ocorreu um erro ao adicionar no banco de dados");
					contex.addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Telefone não informado","Obrigatório adicionar pelo menos um telefone");
				contex.addMessage(null, msg);
			}			
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Nome não informado", "Obrigatório informar o nome do contato");
			contex.addMessage(null, msg);
		}
		
		return "";
		
	}
	
	
	//getters and setters
	
	public Contato getContato() {
		return contato;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
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

	public TipoTelefone getTipoTelefone1() {
		return tipoTelefone1;
	}

	public void setTipoTelefone1(TipoTelefone tipoTelefone1) {
		this.tipoTelefone1 = tipoTelefone1;
	}

	public TipoTelefone getTipoTelefone2() {
		return tipoTelefone2;
	}

	public void setTipoTelefone2(TipoTelefone tipoTelefone2) {
		this.tipoTelefone2 = tipoTelefone2;
	}

	public TipoTelefone getTipoTelefone3() {
		return tipoTelefone3;
	}

	public void setTipoTelefone3(TipoTelefone tipoTelefone3) {
		this.tipoTelefone3 = tipoTelefone3;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	
	
}
