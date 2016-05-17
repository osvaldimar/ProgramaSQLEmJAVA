package br.com.beansql.validators;

public class Condicao {

	private String campoComparar;
	private String campoOuValorComparado;
	private Operador operador;
	
	public Condicao(){
		
	}
	public Condicao(String campo, Operador operador, String campoOuValor) {
		this.campoComparar = campo.toLowerCase();
		this.campoOuValorComparado = campoOuValor.toLowerCase();
		this.operador = operador;
	}

	public String getCampoComparar() {
		return campoComparar;
	}

	public void setCampoComparar(String campoComparar) {
		this.campoComparar = campoComparar.toLowerCase();
	}

	public String getCampoOuValorComparado() {
		return campoOuValorComparado;
	}

	public void setCampoOuValorComparado(String campoOuValorComparado) {
		this.campoOuValorComparado = campoOuValorComparado.toLowerCase();
	}

	public Operador getOperador() {
		return operador;
	}

	public void setOperador(Operador operador) {
		this.operador = operador;
	}
	
}
