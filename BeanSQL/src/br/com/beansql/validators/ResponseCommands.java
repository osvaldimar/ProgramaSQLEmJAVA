package br.com.beansql.validators;

import br.com.beansql.control.ALIAS;
import br.com.beansql.model.ConjuntoTiposDeDados;

/**
 * Classe respons�vel por retornar uma resposta com os comandos inseridos
 * Armazena os comandos, argumentos inseridos, ConjuntoTiposDeDados quando criado uma tabela
 * Armazena nome de Tabelas e DataBase. M�todos getters s�o utilizados para recuperar as respostas dos comandos
 * @author osvaldimar.costa
 *
 */
public class ResponseCommands {

	private Command comando;		//enum para Command
	private String[] argumentos;	//argumentos dos valores e atributos
	private ConjuntoTiposDeDados[] conjuntoTipoDados;	//tipos de dados dos atributos (integer, varchar...)
	private String[] nameOfTabelas;
	private Criterio criterios;
	private String nameOfDataBase;
	private ALIAS alias;			//apelidos para os nomes de campos e tabelas
	private String erro;
	
	public ResponseCommands(Command comando, String erro){
		this.comando = comando;		//enum de Command
		this.erro = erro;
	}
	public ResponseCommands(Command comando, String[] args, ConjuntoTiposDeDados[] conjunto, String nameOfTabela[], Criterio criterios, String nameOfDataBase) {
		this.comando = comando;		//enum de Command
		this.argumentos = args;		//argumentos e valores		
		this.conjuntoTipoDados = conjunto; //tipos de dados que corresponde a cada argumento
		this.nameOfTabelas = nameOfTabela;
		this.nameOfDataBase = nameOfDataBase ;
		this.criterios = criterios;
		this.alias = null;
	}
	public ResponseCommands(Command comando, String[] args, ConjuntoTiposDeDados[] conjunto, String nameOfTabela[], Criterio criterios, String nameOfDataBase, ALIAS alias) {
		this.comando = comando;		//enum de Command
		this.argumentos = args;		//argumentos e valores		
		this.conjuntoTipoDados = conjunto; //tipos de dados que corresponde a cada argumento
		this.nameOfTabelas = nameOfTabela;
		this.nameOfDataBase = nameOfDataBase ;
		this.criterios = criterios;
		this.alias = alias;
	}
	
	/**
	 * M�todo retorna uma constante Enum para informar qual comando est� sendo utilizado
	 * @return Command 
	 */
	public Command getComando(){
		return comando;
	}
	/**
	 * M�todo retorna argumentos como atributos de tabelas, valores etc.
	 * @return String[] de argumentos
	 */
	public String[] getArgumentos() {
		return argumentos;
	}
	/**
	 * M�todo retorna um array de objeto ConjuntoTiposDeDados
	 * utilizados para identificar os tipos de dados para cada atributo criado na tabela
	 * @return ConjuntoTiposDeDados[] retorna um array de tipos de Dados
	 */
	public ConjuntoTiposDeDados[] getTiposDeDados() {
		return conjuntoTipoDados;
	}
	
	/**
	 * M�todo utilizado para recuperar as tabelas utilizadas nos comandos inseridos
	 * @return String[] com os nomes das tabelas
	 */
	public String[] getNameOfTabela() {
		return nameOfTabelas;
	}
	/**
	 * M�todo utilizado para recuperar as tabelas utilizadas nos comandos inseridos
	 * @return String com o nome da DataBase
	 */
	public String getNameOfDataBase() {
		return nameOfDataBase;
	}
	/**
	 * M�todo utilizado para recuperar os criterios do Select
	 * @return Criterio com os criterios inseridos no select
	 */
	public Criterio getCriterios() {
		return criterios;
	}
	public ALIAS getAlias() {
		return alias;
	}
	public void setAlias(ALIAS alias) {
		this.alias = alias;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
	
	
}
