/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.control;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import br.com.beansql.exceptions.AliasBeanSQLException;
import br.com.beansql.exceptions.BeanSQLException;
import br.com.beansql.exceptions.DataBaseBeanSQLException;
import br.com.beansql.exceptions.TableBeanSQLException;
import br.com.beansql.exceptions.TipoDadosBeanSQLException;
import br.com.beansql.model.AtributosValores;
import br.com.beansql.model.Coluna;
import br.com.beansql.model.Data;
import br.com.beansql.model.DataBase;
import br.com.beansql.model.Tabela;
import br.com.beansql.model.Tupla;
import br.com.beansql.model.Usuario;
import br.com.beansql.validators.ComandosAlgoritmos;
import br.com.beansql.validators.Condicao;
import br.com.beansql.validators.Criterio;
import br.com.beansql.validators.Operador;
import br.com.beansql.validators.OrderByComparator;
import br.com.beansql.validators.ResponseCommands;
import br.com.beansql.validators.SequenciaDeComandos;
import br.com.beansql.validators.TiposDeDados;

public class CentralDeComandos {

	private Data data;
	private MyConsole console;
	private static long TEMPO_TRANSACAO;

	public CentralDeComandos(MyConsole console) {
		this.console = console;
		TabelaDecorator.console = console; // inicia e decoração de tabela com o
		TEMPO_TRANSACAO = 0;									// padrão do console atual
	}
	
	private static void iniciarTempoTransacao(){
		TEMPO_TRANSACAO = System.currentTimeMillis();
	}
	
	public static String getFimTempoTransacao(){
		
		TEMPO_TRANSACAO = System.currentTimeMillis() - TEMPO_TRANSACAO;
		DecimalFormat f = new DecimalFormat("0.00");
		double d = TEMPO_TRANSACAO / 1000.0;
		String tempo = f.format(d);
		
		return " (" + tempo + " seconds) ";
	}
	
	/**
	 * Method principal, inicia a central de comandos onde é efetuada todas as
	 * operações do Banco de Dados como: leitura de comandos, saida de dados e
	 * escrita no banco
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {

		// efetua em primeiro momento a leitura do banco principal de acordo com
		// a interface e o console utilizado
		// efetua a verredura de usuarios no banco para validar o acesso
		// efetua o processo do console como leitura de comandos com loop
		// infinito ao menos que saia do programa com exit ou quit

		loadData(); // carrega o banco
		login(); // valida login e password
		// string para help
		String help = "List of all BeanSQL commands:\n"
				+ "create database [name]\t\t- create the database\n"
				+ "create table 	[name]\t\t- create the table\n"
				+ "show databases\t\t\t- show all databases\n"
				+ "show tables\t\t\t- show all tables\n"
				+ "describe tables\t\t\t- description of table\n"
				+ "use database\t\t\t- use database current\n"
				+ "insert into values\t\t- insert values in table\n"
				+ "drop table [name]\t\t- delete the table\n"
				+ "drop database [name]\t\t- delete the database\n"
				+ "delete from table [conditions]\t- delete registers\n"
				+ "update table set fields\t\t- update registers\n"
				+ "select * from table\t\t- select all fields in table\n"
				+ "select a1, a2 from table\t- select all fields in table\n"
				+ "select * from table join table\t- select all fields in table with joins\n"
				+ "select * from table [where]\t- select fields in table with [where/and/or/like]\n"
				+ "select * from table order by\t- select fields in table with order by field\n"
				+ "select [field/table] AS [alias]\t- select fields and tables with alias\n"
				+ "type of data to enter [integer, double, char, varchar(), date yyyy-mm-dd]\n"
				+ "type of data to enter [null, not null, unique, primary key]\n"
				+ "Enter 'exit' or 'quit' to exit the program\n\n"
				+ "Type 'help;' or 'h;' or '?;' for help.\n\n";

		String welcome = "\nWelcome to the BeanSQL console.  Commands end with ;\n"
				+ "Version: BeanSQL v19.0 Beta Edition. All rights reserved.\n"
				+ "Copyright (c) 2013, Developed by Osvaldimar Costa, osvaldimar@gmail.com\n"
				+ "Type 'help;' or 'h;' or '?;' for help. Enter 'exit' or 'quit' to exit the program\n"; // Type '\\c' to clear the current input statement.";

		console.printOnConsole(welcome); // boas vindas
		console.printOnConsole("\nStart BeanSQL!!\n\n"); // start BeanSQL
		String cursor = "BeanSQL> "; // cursor principal
		String cursorContinuacao = "      -> "; // cursor de quebra de linha
		console.printOnConsole(cursor); // print o primeiro cursor para entrada de dados

		// leitura do console
		String entrada = ""; // variavel para receber a entrada de comandos
		while (!entrada.equalsIgnoreCase("EXIT")
				&& !entrada.equalsIgnoreCase("QUIT")) { // caso a entrada seja igual ao exit ou quit, encerra o programa
			
			entrada += console.readerConsole().trim();
			
			// verificar se comando eh o exit
			if (!entrada.equalsIgnoreCase("EXIT")
					&& !entrada.equalsIgnoreCase("QUIT")) { // caso a entrada seja igual ao exit ou quit, encerra o programa
				try {
					if (!entrada.isEmpty()) { // se entrada naum estiver vazia  verifica final de linha de camando ";" // se estiver vazia print cursor padrão
						if (entrada.substring(entrada.length() - 1,
								entrada.length()).equalsIgnoreCase(";")) {
							// verifica help
							if (entrada.equalsIgnoreCase("help;")
									|| entrada.equalsIgnoreCase("h;")
									|| entrada.equalsIgnoreCase("?;")) {
								console.printOnConsole(help);
								entrada = "";
								console.printOnConsole(cursor);
							} else {	
								//inicia o tempo da transacao
								CentralDeComandos.iniciarTempoTransacao();
								// aqui vem validadores, sequencia de comandos e palavras chaves
								executCommand(SequenciaDeComandos
										.sortSequenciaDeComandos(entrada));
								entrada = ""; // necessario setar vazio pois após termino de comando ";" variavel entrada recebera novas sequencias de comandos
								console.printOnConsole(cursor); // após executar o comando,  print cursor BeanSQL>
							}
						} else { // caso naum seja termino de comando ";" executa quebra de linha de continuação e continua com mais comandos na proxima linha
							entrada += " "; // necessario concaternar com um espaço para separar o comando na proxima linha
							console.printOnConsole(cursorContinuacao); // caso naum for final de comando ";" print o sor de continuação de outros comandos "->"
						}
					} else {
						console.printOnConsole(cursor); // se entrada for vazia,
														// print cursor BeanSQL>
					}
				} catch (Exception e) {
					e.printStackTrace();
					// console.printOnConsole("Houve uma exception inesperada!!!\n");
					// //caso exista alguma exceção em toda excução print
					// exception - necessário verificar qual erro ocorrido
					// trata todas as expetions possiveis em todo programa,
					// porém não trata os erros devidamente em escabilidade e
					// devolve exception para classe principla AppMyBeanSQL
					console.printOnConsole("Error 01 Invalid command detected, verify syntax version to your BeanSQL!\n");
				}
			}
		}
		console.printOnConsole("Bye!\n\n"); // após final de loop com quit ou exit encerra o programa
		System.exit(0);

	}

	/**
	 * Method executa o comando passado como parametro com informações de qual
	 * comando executar no swith case e as sequencias de comandos
	 * 
	 * @param response
	 */
	public void executCommand(ResponseCommands response) {
		// console.printOnConsole("execut command = " + response.getComando() +
		// " sequencia = " + response.getSequencia() + "\n");

		switch (response.getComando()) {
		case EXIT: 
			console.printOnConsole("Bye!\n\n"); // após final de loop com quit ou exit encerra o programa
			System.exit(0);
			break;
		case INVALID_COMMAND:
			console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
			break;
		case ERROR_IN:
			console.printOnConsole( response.getErro() + ", verify syntax version to your BeanSQL!\n"); //response.getArgumentos()[0] retorna um erro a ser mostrado
			break;
		case KEY_WORD_RESERVED:
			console.printOnConsole("invalid arguments format near '" + response.getArgumentos()[0] + "', key word reserved to your BeanSQL\n");
			break;
			
		case SHOW_DATABASES: centralPrintShow(new AtributosValores("Databases", null, data.showDataBases()));			
			break;
			
		case USE:
			if (data.useDataBase(response.getArgumentos()[0])) {// retorna true se conseguiu achar database;
				data.setCurrentDataBase(console.readerDatabase(response
						.getArgumentos()[0]));
				console.printOnConsole("Database changed\n");
			} else {
				console.printOnConsole("Database no exist!\n");
			}
			break;
		case SHOW_TABLES:
			try {
				centralPrintShow(new AtributosValores("tables_in_"+ data.getCurrentDataBase().getNameDataBase(), null,  data.getCurrentDataBase().showTables()));
			} catch (NullPointerException e) {
				console.printOnConsole("No database selected!\n");
			}
			break;
		case CREATE_DATABASE:
			try {
				data.addDataBase(response.getArgumentos()[0]);
				console.writeDB(data);
				console.printOnConsole("Database created successful\n");
			} catch (DataBaseBeanSQLException e) { // caso ocorra erro ao  add DataBase ao banco
				console.printOnConsole(e.getMessage());
			}
			break;
		case CREATE_TABLE:
			createTable(response);// utiliza o metodo createTable para add tabela ao DataBase
			break;
		case INSERT_INTO_VALUES:
			insertIntoValues(response);
			break;
		case UPDATE_SET_WHERE:
			updateTable(response);
			break;
		case DESCRIBE:
			describeTable(response);
			break;
		case SELECT_FROM_JOIN_WHERE:
			if(data.getCurrentDataBase() != null){
				if(!(response.getAlias() == null)){	//se alias for null, é um select sem apelidos
					try{
						data.getCurrentDataBase().setAlias(response.getAlias(), response.getNameOfTabela());//premissa, add os apelidos e nomes dados fantasia dados a tabela e colunas - Alias
						centralPrintSelectJoinWhere(response);
						data.getCurrentDataBase().removeAlias(response.getAlias());//remove os apelidos
						
					}catch(AliasBeanSQLException e){
						data.getCurrentDataBase().removeAlias(response.getAlias()); //remove os apelidos caso tenha algum erro e já adicionou apelidos os mesmos são removidos
						console.printOnConsole(e.getMessage());
					}
				}else{
					centralPrintSelectJoinWhere(response);	//print select normal
				}
				
			}else{
				console.printOnConsole("No database selected!\n");
			}
			break;
		case DROP_TABLE:
			centralDropTable(response);
			break;
		case DROP_DATABASE:
			centralDropDataBase(response);
			break;
		case DELETE_FROM_WHERE:
			centralDeleteFromWhere(response);
			break;
		default:
		}

	}

	// CENTRAL DE COMANDOS
	public void centralDropDataBase(ResponseCommands response) {
		for (String name : data.getNameOfAllDataBases()) {
			if (name.equalsIgnoreCase(response.getNameOfDataBase())) {// na  verdade é o nome do database
				// se achar o nome da DataBase então deleta
				if (data.getCurrentDataBase() != null) { // verificar se a database current é diferente de null
					if (data.getCurrentDataBase().getNameDataBase().equalsIgnoreCase(name)) {
						// se a database current for a propria que esta deletando remove
						data.setCurrentDataBase(null);
					}
				}
				// deleta todas as tabelas da database e os arquivos database.table.db
				DataBase databaseDeleteTable = console.readerDatabase(response.getNameOfDataBase());
				
				// Iterator it = databaseDeleteTable.getTabelas().iterator();
				for (Tabela tabela : databaseDeleteTable.getTabelas()) {
					String path = databaseDeleteTable.getNameDataBase() + "."
							+ tabela.getNameOfTabela();
					//premissa necessário deletar todas tabelas da database
					// remove todas as tabelas
					console.dropTable(path); // deleta o arquivo database.table
				}
				databaseDeleteTable.getTabelas().clear();
				console.writeDatabase(databaseDeleteTable); // grava dados no arquivo database

				data.getNameOfAllDataBases().remove(name);// remove o nome da database do banco
				console.dropDataBase(name); // deleta o arquivo database passando o nome por paramento
				console.writeDB(data); // grava no banco a DataBase deletada
				console.printOnConsole("Drop database successful\n");
				return;
			}
		}
		// se não finalizou então não achou a database
		console.printOnConsole("Database '"+ response.getNameOfDataBase() + "' doesn't exist!\n");
	}

	// DROP
	private void centralDropTable(ResponseCommands response) {
		if (data.getCurrentDataBase() != null) {
			for (Tabela t : data.getCurrentDataBase().getTabelas()) {
				if (t.getNameOfTabela().equalsIgnoreCase(
						response.getNameOfTabela()[0])) {
					// se achar o nome da tabela no DataBase current então deleta
					String path = data.getCurrentDataBase().getNameDataBase()
							+ "." + t.getNameOfTabela();
					data.getCurrentDataBase().getTabelas().remove(t); // remove a tabela do database current
					console.writeDatabase(data.getCurrentDataBase()); // grava dados no arquivo database
					console.dropTable(path); // deleta o arquivo database.table
					console.printOnConsole("Drop table successful\n");
					return;
				}
			}
			// se não finalizou então não achou a tabela
			console.printOnConsole("Table '" + response.getNameOfTabela() + "' doesn't exist!\n");
		} else {
			console.printOnConsole("No database selected!\n");
		}
	}
	
	//
	public void updateTable(ResponseCommands response){
		if (data.getCurrentDataBase() != null) {
			//verifica nome da tabela
			String path = "";
			try{
				if(data.getCurrentDataBase().containsNameOfTable(response.getNameOfTabela()[0])){
					//path é nome do arquivo.tbl DataBase.Table exemplo: beansql.tabela1.tbl					
					path = data.getCurrentDataBase().getNameDataBase() + "." + response.getNameOfTabela()[0];					
				}
			}catch(DataBaseBeanSQLException e){
				//lança exception caso retorne false na comparacao do nome da tabela na database
				console.printOnConsole(e.getMessage());
				return;
			}
			
			//verifica quantidade de campos da tabela
			int contTotalArrayAV = 0;
			for(Tabela t : data.getCurrentDataBase().getTabelas()){				
				if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[0])){
					//se achar o nome da tabela da database no response entao contador recebe todas colunas da tabela
					contTotalArrayAV += t.getColuna().size();
				}
			}
			//separa campos e valores
			String[] camposSET = new String[response.getArgumentos().length];	//para cada args do response retorna um 'campo=valor' do array de set
			String[] valoresSET = new String[response.getArgumentos().length];
			for(int i = 0; i < response.getArgumentos().length; i++){
				String[] separar = response.getArgumentos()[i].split("=");	//é validado 
				camposSET[i] = separar[0].trim(); //retorna o campo em primeiro
				valoresSET[i] = separar[1].trim(); //retorna o valor em segundo
			}
			
			//valida os campos do set e valores para alterar
			for(String campo : camposSET){ //for para contar os campos set informados para update
				try {
					data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campo);
				} catch (DataBaseBeanSQLException e) {
					//campo não existe na tabela finaliza metodo
					console.printOnConsole(e.getMessage());
					return;
				}
			}			
			//valida valores alterados em cada campo para update
			Tabela tabela = null;
			try {
				tabela = data.getCurrentDataBase().getTabelaPeloNome(response.getNameOfTabela()[0]);
			} catch (DataBaseBeanSQLException e) {
				console.printOnConsole(e.getMessage()); //já foi validado acima o nome da tabela
				return;
			} 
			for(Coluna c : tabela.getColuna()){
				for(int i = 0; i < camposSET.length; i++){
					//se for valido camposSET[i], então atualiza sem o nome absoluto se o mesmo for
					camposSET[i] = SequenciaDeComandos.getSomenteNomeCampoSemNomeAbsoluto(camposSET[i]);//elimina nome da tabela com pontos table.campo
					if( c.getName().equalsIgnoreCase(camposSET[i]) ){
						try{
							//System.out.println("C = "+c.getName()+" / camposSET = "+camposSET[i]+" / item = "+valoresSET[i]);
							if(c.isValidItemInseridoNaColuna(c.getTiposDeDados().getTipo(), valoresSET[i])){//se algum valor dos campos não for valido retorna mensagem de erro
								//ok valor is valid
							}else {
								console.printOnConsole("Invalid values detected \"" + valoresSET[i] + "\" for column '" +c.getName()+"'\n");
								return; // para finalizar o metodo
							}
							
						} catch (IndexOutOfBoundsException e) {
							console.printOnConsole("Invalid values detected \"" + valoresSET[i] + "\" for column '" +c.getName()+"'\n");
							return; // para finalizar o metodo
						} catch (NumberFormatException e) {
							console.printOnConsole("Invalid values detected \"" + valoresSET[i] + "\" for column '" +c.getName()+"'\n");
							return; // para finalizar o metodo
						}
					}
				}
			}
			
			
			//leitura das tabelas
			ArrayList<Tupla> listaDeTuplasDaTabela = new ArrayList<Tupla>();
			listaDeTuplasDaTabela = console.readerListTuplas(path);
			//se alguma ArrayList<Tupla> retornada do console for null, então não há valores
			if(listaDeTuplasDaTabela == null){
				//verifica em primeiro momento se naum retorna exceções no nome dos argumentos campos inseridos para where
				if(!response.getCriterios().getCondicoes().isEmpty()){
					for(int i = 0; i < response.getCriterios().getCondicoes().size(); i++){
						
						Condicao condicao = response.getCriterios().getCondicoes().get(i);
						String campoComparar = condicao.getCampoComparar();
						
						try {
							data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoComparar);
						} catch (BeanSQLException e) {
							console.printOnConsole(e.getMessage());
							return; //finaliza metodo
						}
						
					}
				}
				console.printOnConsole("No values in table, is empty!\n");
				return; //finaliza metodo
			}
			
			//recupera dados das tuplas de registros da tabela
			AtributosValores[] arrayAV = new AtributosValores[contTotalArrayAV];
			int contArrayAV = 0;
			for(Tabela t : data.getCurrentDataBase().getTabelas()){ //contador de todas tabelas da database
				if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[0])){		//se achou o nome da tabela do response na database
					//obs arrayListaDeTuplasDasTabelas = e a mesma quantidade de tabelas, pois cada tabela tem uma listaDeTuplas das tabelas
					for(int j = 0; j < t.getColuna().size(); j++){//percorre coluna por coluna de cada tabela
						ArrayList<String> listaParaAV = new ArrayList<String>();//uma lista para atributos valores
						for(Tupla tupla : listaDeTuplasDaTabela){		//percorre cada linha, registro que retorna um map, percorre toda a coluna para depois ir para prox coluna										
							//um linha de tupla eh lida, mas add somente uma coluna por vez
							String key = t.getColuna().get(j).getName();	//retorna o nome da coluna pelo inteiro j, será o valor da chave								
							String valor = tupla.getMapaTuplas().get(key);	//retorna o valor correspondente a key que é o nome da coluna
							listaParaAV.add(valor);	//add valor na lista para o array de atributos valores									
						}	
						arrayAV[contArrayAV++] = new AtributosValores(t.getColuna().get(j).getName(), t.getNameOfTabela(), listaParaAV);
					}
				}
			}
			
			int contChangedAffected = 0, contRowsAffected = 0;
			//verifica condições where
			if(!response.getCriterios().getCondicoes().isEmpty()){
				//verifica duplicatas de campos
				
				for(int i = 0; i < response.getCriterios().getCondicoes().size(); i++){
					
					Condicao condicao = response.getCriterios().getCondicoes().get(i);
					String campoComparar = condicao.getCampoComparar();
					
					try {
						data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoComparar);
					} catch (BeanSQLException e) {
						console.printOnConsole(e.getMessage());
						return; //finaliza metodo
					}
					
				}
				
				//se chegou aki codigo sem erros de variaveis e campos, porem valores podem esta incorretos
				for(int i = 0; i < arrayAV[0].getValores().size(); i++){
					Criterio criterio = new Criterio();
					for(int j = 0; j < response.getCriterios().getCondicoes().size(); j++){
						Condicao condicao = response.getCriterios().getCondicoes().get(j);
						String campoComparar = condicao.getCampoComparar();
						String campoOuValor = condicao.getCampoOuValorComparado();
						
						for(AtributosValores av : arrayAV){
							if(av.getNomeAtributo().equalsIgnoreCase(campoComparar)
									|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoComparar)){
								campoComparar = av.getValores().get(i);								
							}
						}
						
						try {
							data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoOuValor);
							for(AtributosValores av : arrayAV){
								if(av.getNomeAtributo().equalsIgnoreCase(campoOuValor)
										|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoOuValor)){
									campoOuValor = "'"+av.getValores().get(i)+"'";
								}
							}
						} catch (BeanSQLException e) {
							//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo						
							if(e.getMessage().contains("ambiguous")){
								console.printOnConsole(e.getMessage());
								return; //finaliza metodo
							} //se naum a variavel eh um valor a ser comparado
						}
						
						Condicao condicaoResult = new Condicao();
						condicaoResult.setCampoComparar(campoComparar);
						condicaoResult.setCampoOuValorComparado(campoOuValor);
						condicaoResult.setOperador(condicao.getOperador());
						
						criterio.getCondicoes().add(condicaoResult);
						criterio.setOperadorUniao(response.getCriterios().getOperadorUniao());
					}
					try {
						if(Criterio.verificarRegistroAtendeCriterios(criterio)){	//se atende criterio é alterado
							boolean lineChanged = false;
							//UPDATE - alterar valores
							for(int w = 0; w < camposSET.length; w++){
								for(int x = 0; x < arrayAV.length; x++){
									if(camposSET[w].equalsIgnoreCase(arrayAV[x].getNomeAtributo())){	//nome do campo update é igual ao nome do atributo valores
										
										String update = SequenciaDeComandos.eliminaApostofro(valoresSET[w]);
										for(Coluna c : tabela.getColuna()){
											if(c.getName().equalsIgnoreCase(arrayAV[x].getNomeAtributo())){	//se for um double grava no padrão '0.00'
												if(c.getTiposDeDados().getTipo().equals(TiposDeDados.DOUBLE)){
													double d = Double.parseDouble(update);
													DecimalFormat format = new DecimalFormat("0.00");
													update = format.format(d);
												}
											}
										}
										
										arrayAV[x].getValores().set(i, update);	//seta na posicão atual do getValores o valor correspondente ao nome do atributo e campoSET
										contChangedAffected++;
										lineChanged = true; //se alterou uma linha é true
									}
								}
							}
							if(lineChanged){	//se linha foi alterada incrementa
								contRowsAffected++;
								lineChanged = false;	
							}
						}
						
					} catch(IndexOutOfBoundsException e){
						//caso retorne erro na manipulação de Index, retorna null com propósito de msg de error padrão 
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					} catch (TipoDadosBeanSQLException e) {
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					}					
				}
				
				
			}else{	//se estiver vazia lista de criterios e naum tem where entaum atualiza todos os campos
				//UPDATE - alterar valores
				for(int w = 0; w < camposSET.length; w++){	//for para compos update
					for(int x = 0; x < arrayAV.length; x++){	//for para cada atributo do array AtributosValores
						if(camposSET[w].equalsIgnoreCase(arrayAV[x].getNomeAtributo())){	//nome do campo update é igual ao nome do atributo valores
							
							for(int i = 0; i < arrayAV[x].getValores().size(); i++){	//for para pecorrer cada registro (item ou valor) de cada coluna(nome do AtributoValores)
								String update = SequenciaDeComandos.eliminaApostofro(valoresSET[w]);
								for(Coluna c : tabela.getColuna()){
									if(c.getName().equalsIgnoreCase(arrayAV[x].getNomeAtributo())){	//se for um double grava no padrão '0.00'
										if(c.getTiposDeDados().getTipo().equals(TiposDeDados.DOUBLE)){
											double d = Double.parseDouble(update);
											DecimalFormat format = new DecimalFormat("0.00");
											update = format.format(d);
										}
									}
								}
								
								arrayAV[x].getValores().set(i, update );	//seta na posicão atual do getValores o valor correspondente ao nome do atributo e campoSET
								contChangedAffected++;
							}
						}						
					}
				}
				contRowsAffected = arrayAV[0].getValores().size();//conta todas as linhas dos valores alterado da coluna
			}
						
			//SE NÃO ALTEROU ABOSUTAMENTE NADA, FINALIZA METODO E NÃO PERSISTE NADA
			if(contChangedAffected == 0){
				String tempo = CentralDeComandos.getFimTempoTransacao();
				console.printOnConsole("Query OK, "+ contRowsAffected + " rows affected, Changed: "+contChangedAffected + " " + tempo + "\n\n");
				return;
			}

			//premissa se chegou até aqui todos valores inseridos são válidos, porém necessário verificar unicidades e chaves
			//verifica se existe primary key e qual a coluna?
			for(Coluna c : tabela.getColuna()){
				if(!(c.getTiposDeDados().getPK() == null)){	//se não for null verifica se o valor se repete na coluna que contem a primary key
					
					ArrayList<Tupla> listaTuplas = console.readerListTuplas(path);	//lista para recuperar as tuplas do tabela que o nome do arquivo é o "path" obs: cada tupla um registro um hashmap						
					if(!(listaTuplas == null)){
						//ler dados e verifica se já contem o valor
						ArrayList<String> listaValoresColuna = new ArrayList<String>();//uma lista para atributos valores
						for(Tupla tupla : listaTuplas){		//percorre cada linha, registro que retorna um map, percorre toda a coluna para depois ir para prox coluna										
							//um linha de tupla eh lida, mas add somente uma coluna por vez
							String key = c.getName();	//retorna o nome da coluna pelo será o valor da chave								
							String valor = tupla.getMapaTuplas().get(key);	//retorna o valor correspondente a key que é o nome da coluna
							listaValoresColuna.add(valor.toLowerCase());	//add valor na lista para o array de atributos valores com letras minusculas								
						}
						
						//verifica se na lista de valores recuperada da coluna que contém a primary key contém o valor novo a ser inserido
						for(int y = 0; y < arrayAV.length; y++){	//for para nome dos Atributos Valores do array
							if(arrayAV[y].getNomeAtributo().equalsIgnoreCase(c.getName())){
								for(int k = 0; k < arrayAV[y].getValores().size(); k++){
									
									String update = arrayAV[y].getValores().get(k);	//recupera cada valor da lista de valores de um nome do atributo que é primary key
									boolean contemOne = false;
									for(String s : arrayAV[y].getValores()){//for para todos valores da coluna
										if(s.equalsIgnoreCase(update)){//se a lista nova contem o update
											if(!contemOne){	//se contemOne for false, blz tem um
												contemOne = true;
											}else{	//se já for true é duplicado
												console.printOnConsole("Duplicate entry \"" + update + "\" for key 'PRIMARY' in column '" + c.getName() + "'\n");
												return; // para finalizar o metodo
											}
										}
									}
									
								}
								break; //se achou o nome do atributo correpondente ao nome da coluna que é primary key, finaliza não existe duas colunas primary key
							}
						}
					}
					break; //se achou finaliza for
				}
			}
			
			//APÓS ALTERAR VALORES COMPLETOS OU COM WHERE PERSISTE A LISTA DE TUPLAS(REGISTROS) NA TABLE DA DATABASE
			ArrayList<Tupla> listaTuplasPersistir = new ArrayList<Tupla>();
			for(int i = 0; i < arrayAV[0].getValores().size(); i++){	//for para contar os valores da tabela
				HashMap<String, String> mapa = new HashMap<String, String>();
				
				for(int j = 0; j < arrayAV.length; j++){				//for para os atributos da tabela
					String key = arrayAV[j].getNomeAtributo();
					String valor = arrayAV[j].getValores().get(i);
					mapa.put(key, valor);								//add valores no mapa de acordo com a coluna e o valor
				}
				//add cada tupla na lista de tuplas
				Tupla tupla = new Tupla(path, mapa);
				listaTuplasPersistir.add(tupla);
			}
			//escreve a lista de tuplas no arquivo database.table.db
			console.writeListaDeTuplas(listaTuplasPersistir, path);
			String tempo = CentralDeComandos.getFimTempoTransacao();
			console.printOnConsole("Query OK, "+ contRowsAffected + " rows affected, Changed: "+contChangedAffected + " " + tempo + "\n\n");
			return; //finaliza metodo
			
		}else{
			console.printOnConsole("No database selected!\n");
		}
	}
	
	// DELETE from where
	public void centralDeleteFromWhere(ResponseCommands response){
		if (data.getCurrentDataBase() != null) {
			//verifica nome da tabela
			String path = "";
			try{
				if(data.getCurrentDataBase().containsNameOfTable(response.getNameOfTabela()[0])){
					//path é nome do arquivo.tbl DataBase.Table exemplo: beansql.tabela1.tbl					
					path = data.getCurrentDataBase().getNameDataBase() + "." + response.getNameOfTabela()[0];					
				}
			}catch(DataBaseBeanSQLException e){
				//lança exception caso retorne false na comparacao do nome da tabela na database
				console.printOnConsole(e.getMessage());
				return;
			}
			
			//verifica quantidade de campos da tabela
			int contTotalArrayAV = 0;
			for(Tabela t : data.getCurrentDataBase().getTabelas()){				
				if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[0])){
					//se achar o nome da tabela da database no response entao contador recebe todas colunas da tabela
					contTotalArrayAV += t.getColuna().size();
				}
			}
			
			//leitura das tabelas
			ArrayList<Tupla> listaDeTuplasDaTabela = new ArrayList<Tupla>();
			listaDeTuplasDaTabela = console.readerListTuplas(path);
			//se alguma ArrayList<Tupla> retornada do console for null, então não há valores
			if(listaDeTuplasDaTabela == null){
				//verifica em primeiro momento se naum retorna exceções no nome dos argumentos campos inseridos para where
				if(!response.getCriterios().getCondicoes().isEmpty()){
					for(int i = 0; i < response.getCriterios().getCondicoes().size(); i++){
						
						Condicao condicao = response.getCriterios().getCondicoes().get(i);
						String campoComparar = condicao.getCampoComparar();
						
						try {
							data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoComparar);
						} catch (BeanSQLException e) {
							console.printOnConsole(e.getMessage());
							return; //finaliza metodo
						}
						
					}
				}
				console.printOnConsole("No values in table, is empty!\n");
				return; //finaliza metodo
			}
			
			//recupera dados das tuplas de registros da tabela
			AtributosValores[] arrayAV = new AtributosValores[contTotalArrayAV];
			int contArrayAV = 0;
			for(Tabela t : data.getCurrentDataBase().getTabelas()){ //contador de todas tabelas da database
				if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[0])){		//se achou o nome da tabela do response na database
					//obs arrayListaDeTuplasDasTabelas = e a mesma quantidade de tabelas, pois cada tabela tem uma listaDeTuplas das tabelas
					for(int j = 0; j < t.getColuna().size(); j++){//percorre coluna por coluna de cada tabela
						ArrayList<String> listaParaAV = new ArrayList<String>();//uma lista para atributos valores
						for(Tupla tupla : listaDeTuplasDaTabela){		//percorre cada linha, registro que retorna um map, percorre toda a coluna para depois ir para prox coluna										
							//um linha de tupla eh lida, mas add somente uma coluna por vez
							String key = t.getColuna().get(j).getName();	//retorna o nome da coluna pelo inteiro j, será o valor da chave								
							String valor = tupla.getMapaTuplas().get(key);	//retorna o valor correspondente a key que é o nome da coluna
							listaParaAV.add(valor);	//add valor na lista para o array de atributos valores									
						}	
						arrayAV[contArrayAV++] = new AtributosValores(t.getColuna().get(j).getName(), t.getNameOfTabela(), listaParaAV);
					}
				}
			}
			
			//verifica condições where
			if(!response.getCriterios().getCondicoes().isEmpty()){
				//verifica duplicatas de campos
				int contRowsDeleted = 0;
				
				for(int i = 0; i < response.getCriterios().getCondicoes().size(); i++){
					
					Condicao condicao = response.getCriterios().getCondicoes().get(i);
					String campoComparar = condicao.getCampoComparar();
					
					try {
						data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoComparar);
					} catch (BeanSQLException e) {
						console.printOnConsole(e.getMessage());
						return; //finaliza metodo
					}
					
				}
				//se chegou aki codigo sem erros de variaveis e campos, porem valores podem esta incorretos
				//recupera valores dos registros e tuplas para o criterio
				AtributosValores[] arrayAVWhere = new AtributosValores[contTotalArrayAV];
				//loop para inicializar o arrayAVWhere
				for(int x = 0; x < arrayAV.length; x++){
					AtributosValores at = new AtributosValores();
					at.setNomeAtributo(arrayAV[x].getNomeAtributo());
					at.setTabelaOrigem(arrayAV[x].getTabelaOrigem());
					arrayAVWhere[x] = at;
				}
				for(int i = 0; i < arrayAV[0].getValores().size(); i++){
					Criterio criterio = new Criterio();
					for(int j = 0; j < response.getCriterios().getCondicoes().size(); j++){
						Condicao condicao = response.getCriterios().getCondicoes().get(j);
						String campoComparar = condicao.getCampoComparar();
						String campoOuValor = condicao.getCampoOuValorComparado();
						
						for(AtributosValores av : arrayAV){
							if(av.getNomeAtributo().equalsIgnoreCase(campoComparar)
									|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoComparar)){
								campoComparar = av.getValores().get(i);								
							}
						}
						
						try {
							data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), campoOuValor);
							for(AtributosValores av : arrayAV){
								if(av.getNomeAtributo().equalsIgnoreCase(campoOuValor)
										|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoOuValor)){
									campoOuValor = "'"+av.getValores().get(i)+"'";
								}
							}
						} catch (BeanSQLException e) {
							//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo						
							if(e.getMessage().contains("ambiguous")){
								console.printOnConsole(e.getMessage());
								return; //finaliza metodo
							} //se naum a variavel eh um valor a ser comparado
						}
						
						Condicao condicaoResult = new Condicao();
						condicaoResult.setCampoComparar(campoComparar);
						condicaoResult.setCampoOuValorComparado(campoOuValor);
						condicaoResult.setOperador(condicao.getOperador());
						
						criterio.getCondicoes().add(condicaoResult);
						criterio.setOperadorUniao(response.getCriterios().getOperadorUniao());
					}
					try {
						if(!Criterio.verificarRegistroAtendeCriterios(criterio)){	//se for igual ao criterio naum será add no arrayAVWhere para ser persistido novamente
							for(int x = 0; x < arrayAV.length; x++){															
								arrayAVWhere[x].getValores().add(arrayAV[x].getValores().get(i));
							}
						}else{
							contRowsDeleted++;
						}
					} catch(IndexOutOfBoundsException e){
						//caso retorne erro na manipulação de Index, retorna null com propósito de msg de error padrão 
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					} catch (TipoDadosBeanSQLException e) {
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					}					
				}
				
				//se naum add nada no arrayAVWhere entaum nada foi selecionado
				if(arrayAVWhere[0].getValores().isEmpty()){
					String tempo = CentralDeComandos.getFimTempoTransacao();
					console.printOnConsole("Query OK, "+ 0 + " rows deleted" + tempo + "\n\n");
					return; //finaliza metodo
				}else{
					arrayAV = arrayAVWhere;
				}
				
				//persistir a tabela. com valoes auterados ou naum	
				ArrayList<Tupla> listaTuplasPersistir = new ArrayList<Tupla>();
				for(int i = 0; i < arrayAV[0].getValores().size(); i++){	//for para contar os valores da tabela
					HashMap<String, String> mapa = new HashMap<String, String>();
					
					for(int j = 0; j < arrayAV.length; j++){				//for para os atributos da tabela
						String key = arrayAV[j].getNomeAtributo();
						String valor = arrayAV[j].getValores().get(i);
						mapa.put(key, valor);								//add valores no mapa de acordo com a coluna e o valor
					}
					//add cada tupla na lista de tuplas
					Tupla tupla = new Tupla(path, mapa);
					listaTuplasPersistir.add(tupla);
				}
				//escreve a lista de tuplas no arquivo database.table.db
				console.writeListaDeTuplas(listaTuplasPersistir, path);
				String tempo = CentralDeComandos.getFimTempoTransacao();
				console.printOnConsole("Query OK, "+ contRowsDeleted + " rows deleted" + tempo + "\n\n");
				return; //finaliza metodo
				
			}else{	//se estiver vazia lista de criterios entaum deleta todos os itens
				//deleta somente o arquivo database.table, pois irá deletar o arquivo, mas database ainda contem a tabela, só naum os registros, irá mostrar empty
				console.dropTable(path); // deleta o arquivo database.table
				String tempo = CentralDeComandos.getFimTempoTransacao();
				console.printOnConsole("Query OK, "+ arrayAV[0].getValores().size() + " rows deleted" + tempo + "\n\n");
				return;
			}
			
		}else{
			console.printOnConsole("No database selected!\n");
		}
	}
	
	// SELECTS
	private void centralPrintSelectJoinWhere(ResponseCommands response){
			
			//verificar como os campos são verificados quando é alias
			//array de string para armazenar os enderecos das tabelas
			String[] arrayPath = new String[response.getNameOfTabela().length];
			int contArrayPath = 0;	//contador do arrayPath
			
			//passo 1 - verifica nome das tabelas
			for(String s : response.getNameOfTabela()){
				try{
					if(data.getCurrentDataBase().containsNameOfTable(s)){
						//path é nome do arquivo.tbl DataBase.Table exemplo: beansql.tabela1.tbl
						//premissa verificar se o nome da tabela é um alias temporario no objeto database current
						if(!(response.getAlias() == null)){	//se contiver apelidos, verifica se esta tabela escolhida é um alias
							
							if(response.getAlias().getMapaTabelasAS().containsKey(s)){	//variavel 's' é o nome escolhido da tabela pode ser um apelido ou não
								//se a tabela for um apelido add ao path o caminho original da referencia de tabela deste apelido, pegando o nome pelo 'value do hashmap'
								arrayPath[contArrayPath++] = data.getCurrentDataBase().getNameDataBase() + "." + response.getAlias().getMapaTabelasAS().get(s); 
							}else{
								//se não tiver o apelido entaum é uma tabela original
								arrayPath[contArrayPath++] = data.getCurrentDataBase().getNameDataBase() + "." + s; //processo normal sem apelidos 'alias'
							}
							
						}else{
							arrayPath[contArrayPath++] = data.getCurrentDataBase().getNameDataBase() + "." + s; //processo normal sem apelidos 'alias'
						}
					}						
				}catch(DataBaseBeanSQLException e){
					//lança exception caso retorne false na comparacao do nome da tabela na database
					console.printOnConsole(e.getMessage());
					return;
				}
			}
			//verifica se não contem tabela com nome duplicado
			for(int i = 0; i < response.getNameOfTabela().length; i++){
				for(int j = i+1; j < response.getNameOfTabela().length; j++){
					if(response.getNameOfTabela()[i].equalsIgnoreCase(response.getNameOfTabela()[j])){
						//se conter nomes de argumentos iguais, error
						console.printOnConsole("Column '" + response.getNameOfTabela()[i] + "' is ambiguous, verify syntax version to your BeanSQL!\n");
						return;
					}
				}
			}
			
			//passo 2 - verifica lista de argumentos "atributos informados" (atributo ou tabela.atributo) existe ou é duplicados em outras tabelas
			
			if(response.getArgumentos() != null){
				for(String nomeCampo : response.getArgumentos()){
					try {
						//valida os argumentos (campo das tabelas) se existe o atributo e se não contém duplicatas em outras tabelas usadas em joins
						data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), nomeCampo);
					} catch (BeanSQLException e) {
						if(!(response.getAlias() == null)){
							if(!response.getAlias().getMapCamposAS().isEmpty()){
								
								//verifica se contem o apelido
								String original =  response.getAlias().getMapCamposAS().get( nomeCampo.toLowerCase() );	//retorna o campo original
								try {
									data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), original); //verifica novamente se o original contem
								} catch (DataBaseBeanSQLException e1) {
									//caso realmente naum tenha nem pelo nome original retorna error, obs isso ja foi validado
									console.printOnConsole(e.getMessage());
									return;
								}	
								
							}else{ //se naum tiver apelidos retorna error
								console.printOnConsole(e.getMessage());
								return;
							}
						}else{	   //se alias for null retorna error
							console.printOnConsole(e.getMessage());
							return;
						}
					}
				}
			}
			
			//passo 3 recupera tuplas da base de dados		
			int contTotalArrayAV = 0;	//contador do array do AtributosValores que recebera cada coluna no response
			int contArrayAV = 0;		//contador atual do array do AtributosValores
			for(int i = 0; i < response.getNameOfTabela().length; i++){
				for(Tabela t : data.getCurrentDataBase().getTabelas()){				
					if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[i])){
						//se achar o nome da tabela da database no response entao contador recebe todas colunas da tabela
						contTotalArrayAV += t.getColuna().size();
					}
				}
			} 						
			AtributosValores[] arrayAV = new AtributosValores[contTotalArrayAV];			
			
			//tratamento de erros lança null caso a listaTuplas lida do console não existir, lista para todas as tabelas
			ArrayList<Tupla>[] arrayListaDeTuplasDasTabelas = new ArrayList[response.getNameOfTabela().length];
			for(int i = 0; i < arrayPath.length; i++){
				//leitura das tabelas
				arrayListaDeTuplasDasTabelas[i] = console.readerListTuplas(arrayPath[i]);
				//se alguma ArrayList<Tupla> retornada do console for null, então não há valores
				if(arrayListaDeTuplasDasTabelas[i] == null){
					console.printOnConsole("No values in set, is empty!\n");
					return; //finaliza metodo
				}
			}
			
			//variavel para mixar as tabelas com join
			JoinTable joinTabelas = new JoinTable();
			int contArrayListaDeTuplas = 0; //contador do arrayListaDeTuplasDasTabelas, em conjunto com as tabelas do response
			for(String s : response.getNameOfTabela()){					//contador dos nomes das tabelas no response
				for(Tabela t : data.getCurrentDataBase().getTabelas()){ //contador de todas tabelas da database
					if(t.getNameOfTabela().equalsIgnoreCase(s)){		//se achou o nome da tabela do response na database
						//obs arrayListaDeTuplasDasTabelas = e a mesma quantidade de tabelas, pois cada tabela tem uma listaDeTuplas das tabelas
						for(int j = 0; j < t.getColuna().size(); j++){//percorre coluna por coluna de cada tabela
							ArrayList<String> listaParaAV = new ArrayList<String>();//uma lista para atributos valores
							for(Tupla tupla : arrayListaDeTuplasDasTabelas[contArrayListaDeTuplas]){		//percorre cada linha, registro que retorna um map, percorre toda a coluna para depois ir para prox coluna										
								//um linha de tupla eh lida, mas add somente uma coluna por vez
								String key = t.getColuna().get(j).getName();	//retorna o nome da coluna pelo inteiro j, será o valor da chave								
								String valor = tupla.getMapaTuplas().get(key);	//retorna o valor correspondente a key que é o nome da coluna
								listaParaAV.add(valor);	//add valor na lista para o array de atributos valores									
							}	
							arrayAV[contArrayAV++] = new AtributosValores(t.getColuna().get(j).getName(), t.getNameOfTabela(), listaParaAV);
							joinTabelas.addTabelaJoin(t, listaParaAV.size()); 
						}
					}
				}
				contArrayListaDeTuplas++; //incrementa, pois a próxima tabela do response também será o proximo conteudo do array de lista de tuplas
			}				
			
			//passo 4 - faz joins das tabelas caso necessario
			if(response.getNameOfTabela().length > 1){//se for join, tem mais de uma tabela no select
				arrayAV = joinTabelas.calcularJoinTables(arrayAV, response);
			}
			
			//passo 5 - verifica lista de condições	WHERE e ON
			if(!response.getCriterios().getCondicoes().isEmpty()){
				
				String[] nomeTabelasUsadas = response.getNameOfTabela();
				
				//verifica duplicatas de campos
				for(int i = 0; i < response.getCriterios().getCondicoes().size(); i++){
					
					Condicao condicao = response.getCriterios().getCondicoes().get(i);
					String campoComparar = condicao.getCampoComparar();
					
					try {
						data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(nomeTabelasUsadas, campoComparar);
					} catch (BeanSQLException e) {
						//caso não acha o campoComparar pode ser um apelido
						//premissa os nomes dos apelidos dos campos tem q transformar nos originais para ter referencias dos campos originais
						if(!(response.getAlias() == null)){
							if(!response.getAlias().getMapCamposAS().isEmpty()){
								
								//verifica se contem o apelido
								String original =  response.getAlias().getMapCamposAS().get(campoComparar);	//retorna o campo original
								try {
									
									data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), original); //verifica novamente se o original contem
									//se for valido o original então transforma o apelido em original para referencia
									//condição do for atual será setado o campo original
									condicao.setCampoComparar(original);						//a variavel condicao replace o novo campoComparar com campo original
									response.getCriterios().getCondicoes().set(i, condicao);	//replace a condicao atual das codicoes do creterio
									
								} catch (DataBaseBeanSQLException e1) {
									//caso realmente naum tenha nem pelo nome original retorna error, obs isso ja foi validado
									console.printOnConsole(e.getMessage());
									return;
								}	
								
							}else{ //se naum tiver apelidos retorna error
								console.printOnConsole(e.getMessage());
								return;
							}
						}else{	   //se alias for null retorna error
							console.printOnConsole(e.getMessage());
							return;
						}
						//console.printOnConsole(e.getMessage());
						//return; //finaliza metodo
					}
					
				}
				//se chegou aki codigo sem erros de variaveis e campos, porem valores podem esta incorretos
				//recupera valores dos registros e tuplas para o criterio
				AtributosValores[] arrayAVWhere = new AtributosValores[contTotalArrayAV];
				//loop para inicializar o arrayAVWhere
				for(int x = 0; x < arrayAV.length; x++){
					AtributosValores at = new AtributosValores();
					at.setNomeAtributo(arrayAV[x].getNomeAtributo());
					at.setTabelaOrigem(arrayAV[x].getTabelaOrigem());
					arrayAVWhere[x] = at;
				}
				for(int i = 0; i < arrayAV[0].getValores().size(); i++){
					Criterio criterio = new Criterio();
					for(int j = 0; j < response.getCriterios().getCondicoes().size(); j++){
						Condicao condicao = response.getCriterios().getCondicoes().get(j);
						String campoComparar = condicao.getCampoComparar();
						String campoOuValor = condicao.getCampoOuValorComparado();
						
						for(AtributosValores av : arrayAV){
							if(av.getNomeAtributo().equalsIgnoreCase(campoComparar)
									|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoComparar)){
								campoComparar = av.getValores().get(i);								
							}
						}
						
						try {
							data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(nomeTabelasUsadas, campoOuValor);
							for(AtributosValores av : arrayAV){
								if(av.getNomeAtributo().equalsIgnoreCase(campoOuValor)
										|| (av.getTabelaOrigem()+"."+av.getNomeAtributo()).equalsIgnoreCase(campoOuValor)){
									campoOuValor = "'"+av.getValores().get(i)+"'";	
								}
							}
						} catch (BeanSQLException e) {
							//caso não acha o campoComparar pode ser um apelido
							//premissa os nomes dos apelidos dos campos tem q transformar nos originais para ter referencias dos campos originais
							if(!(response.getAlias() == null)){
								if(!response.getAlias().getMapCamposAS().isEmpty()){
									
									//verifica se contem o apelido
									String original =  response.getAlias().getMapCamposAS().get(campoOuValor);	//retorna o campo original
									try {
										
										data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), original); //verifica novamente se o original contem
										//se for valido o original então transforma o apelido em original para referencia
										//condição do for atual será setado o campo original
										condicao.setCampoOuValorComparado(original);				//a variavel condicao replace o novo campoOuValor com campo original
										response.getCriterios().getCondicoes().set(i, condicao);	//replace a condicao atual das codicoes do creterio
										
									} catch (DataBaseBeanSQLException e1) {
										
										//se retornar exception verifica se naum é ambiguous ou se realmente não tiver o campoOuValor é um valor a ser comparado
										if(e.getMessage().contains("ambiguous")){
											//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo	
											e.printStackTrace();
											console.printOnConsole(e.getMessage());
											return; //finaliza metodo
										} //se naum a variavel eh um valor a ser comparado
										
									}	
									
								}else{ //se naum tiver apelidos 
									
									if(e.getMessage().contains("ambiguous")){
										//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo	
										e.printStackTrace();
										console.printOnConsole(e.getMessage());
										return; //finaliza metodo
									} //se naum a variavel eh um valor a ser comparado
									
								}
							}else{	   //se alias for null
								
								if(e.getMessage().contains("ambiguous")){
									//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo	
									e.printStackTrace();
									console.printOnConsole(e.getMessage());
									return; //finaliza metodo
								} //se naum a variavel eh um valor a ser comparado
								
							}
							//se achou ambiguous finaliza com erro se nao a variavel campoOuValor naum eh um atributo						
							//if(e.getMessage().contains("ambiguous")){
							//	e.printStackTrace();
							//	console.printOnConsole(e.getMessage());
							//	return; //finaliza metodo
							//} //se naum a variavel eh um valor a ser comparado
						}
						
						Condicao condicaoResult = new Condicao();
						condicaoResult.setCampoComparar(campoComparar);
						condicaoResult.setCampoOuValorComparado(campoOuValor);
						condicaoResult.setOperador(condicao.getOperador());
						
						criterio.getCondicoes().add(condicaoResult);
						criterio.setOperadorUniao(response.getCriterios().getOperadorUniao());
					}
					try {
						if(Criterio.verificarRegistroAtendeCriterios(criterio)){
							for(int x = 0; x < arrayAV.length; x++){															
								arrayAVWhere[x].getValores().add(arrayAV[x].getValores().get(i));
								//System.out.println("arrayAVWhere[x] ADD="+arrayAV[x].getValores().get(i));
							}
						}
					} catch(IndexOutOfBoundsException e){
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					} catch (TipoDadosBeanSQLException e) {
						console.printOnConsole("Invalid command detected, verify syntax version to your BeanSQL!\n");
						return;
					}					
				}
				//verifica se a lista contém algum valor selected pelo where
				if(arrayAVWhere[0].getValores().isEmpty()){
					console.printOnConsole("No values in set, is empty!\n");
					return; //finaliza metodo
				}else{
					arrayAV = arrayAVWhere;
				}
			}
			
			//passo 6 - verifica ordenacao  order by
			if(!response.getCriterios().getListaCamposOrdenar().isEmpty()){
				//transforma a lista de ordens em array de Operador
				Operador[] ordens = new Operador[response.getCriterios().getListaTipoOrdem().size()];
				int contArrayOrderBy = 0;
				for(Operador o : response.getCriterios().getListaTipoOrdem()){
					ordens[contArrayOrderBy++] = o;
				}
				//transforma a lista de campos em array de String
				String[] campos = new String[response.getCriterios().getListaCamposOrdenar().size()];	//array para add os campos para ordenação
				contArrayOrderBy = 0;
				for(String s : response.getCriterios().getListaCamposOrdenar()){
					//premissa tranformar os campos em originais
					if(!(response.getAlias() == null)){
						if(!response.getAlias().getMapCamposAS().isEmpty()){
							
							//verifica se contem o apelido
							String original =  response.getAlias().getMapCamposAS().get( s );	//retorna o campo original se contiver
							try {
								data.getCurrentDataBase().validarDuplicatasNomeCampoTabela(response.getNameOfTabela(), original); //verifica novamente se o original contem
								//se for valido o original então transforma o apelido dos campos do comando order by em original para referencia
								s = original;
							} catch (DataBaseBeanSQLException e1) {
								//caso realmente naum tenha pelo nome original, entaum o campo para order by não é um apelido, segue fluxo normal sem alteração
							}	
							
						}
					}
					campos[contArrayOrderBy++] = s;
				}
				
				try {
					//esse método verifica se os campos existem, não contem duplicatas e retorna array de campos com nome absoluto
					//passando array de String com nome dos campos e retorna um array de String com nomes absoluto dos campos
					campos = data.getCurrentDataBase().getCamposNomeAbsoluto(response.getNameOfTabela(), campos);
				} catch (BeanSQLException e) {
					console.printOnConsole(e.getMessage());
					return;
				}
				
				//se chegou até aqui foram validados todos os campos para ordenação
				arrayAV = OrderByComparator.sortOrderBy(arrayAV, campos, ordens);
				
				
			}
			
			//passo 7 - recupera somente os atributos selecionados no response
			try{
				if(response.getArgumentos() == null){
					TabelaDecorator.printTabela(arrayAV);
					return; //finaliza metodo
				}else{
					//se não tem argumentos de atributos selecionados, neste caso mostra somente os proprios e elimina os outros atributos
					AtributosValores[] resultAV = new AtributosValores[response.getArgumentos().length];
					for(int i = 0; i < response.getArgumentos().length; i++){
						for(int j = 0; j < arrayAV.length; j++){
							//não se preocupar com atributos com nomes iguais de outras tabelas, pois acima já foi validado se tem o mesmo nome
							for(Tabela t : data.getCurrentDataBase().getTabelas()){
								for(String nomeTabelaResponse : response.getNameOfTabela()){
									if(t.getNameOfTabela().equalsIgnoreCase(nomeTabelaResponse)){		//achou a tabela no database
										//se contiver a coluna na tabela
										if(t.containsAtributo(response.getArgumentos()[i])){												
											if(t.getNameOfTabela().equalsIgnoreCase(arrayAV[j].getTabelaOrigem())){//se a tabela tiver o mesmo nome no arrayAV
												//verifica se o atributo do arrayAV é igual ao nome dos argumentos do response
												if((nomeTabelaResponse+"."+arrayAV[j].getNomeAtributo()).equalsIgnoreCase(response.getArgumentos()[i])
														|| arrayAV[j].getNomeAtributo().equalsIgnoreCase(response.getArgumentos()[i])){
													
													AtributosValores avTemp = new AtributosValores();
													avTemp.setValores(arrayAV[j].getValores());
													avTemp.setNomeAtributo( SequenciaDeComandos.getSomenteNomeCampoSemNomeAbsoluto( response.getArgumentos()[i]) );
													resultAV[i] = avTemp;
												}
											}
											
										}else if(!(response.getAlias() == null)){	//ou se alias for diferente de null verifica se eh um apelido verifica o nome original
											if(t.containsAtributo( response.getAlias().getMapCamposAS().get( response.getArgumentos()[i].toLowerCase() ))){
												if(t.getNameOfTabela().equalsIgnoreCase(arrayAV[j].getTabelaOrigem())){//se a tabela tiver o mesmo nome no arrayAV
													//verifica se o atributo do arrayAV é igual ao nome dos argumentos do response
													String original = response.getAlias().getMapCamposAS().get( response.getArgumentos()[i].toLowerCase() );
													if((nomeTabelaResponse+"."+arrayAV[j].getNomeAtributo()).equalsIgnoreCase( original )
															|| arrayAV[j].getNomeAtributo().equalsIgnoreCase( original )){
														
														AtributosValores avTemp = new AtributosValores();
														avTemp.setValores(arrayAV[j].getValores());
														avTemp.setNomeAtributo( SequenciaDeComandos.getSomenteNomeCampoSemNomeAbsoluto( response.getArgumentos()[i]) );
														resultAV[i] = avTemp;
													}
												}
											}
										}
										
									}
								}
							}
						}
					}
					TabelaDecorator.printTabela(resultAV);
					return; //finaliza metodo
				}
			}catch(IndexOutOfBoundsException e){
				console.printOnConsole("Error 02 ommand invalid detected, verify syntax version to your BeanSQL!\n");
				return;
			}
			
	}
	
	// DESCRIBE TABLE
	private void describeTable(ResponseCommands response) {
		if (data.getCurrentDataBase() != null) {
			for (Tabela t : data.getCurrentDataBase().getTabelas()) {
				if (t.getNameOfTabela().equalsIgnoreCase(
						response.getNameOfTabela()[0])) { // se achou, então é a tabela para executar o describe

					AtributosValores[] arrayAV = new AtributosValores[5]; // array de AtributosValores[] para passar pra print da TabelaDecorator
					ArrayList<String> listaField = new ArrayList<String>(); // lista para  cada coluna,  neste caso nome do campo
					ArrayList<String> listaTipo = new ArrayList<String>(); // lista do tipo
					ArrayList<String> listaIsNull = new ArrayList<String>();
					ArrayList<String> listaIsUnique = new ArrayList<String>();
					ArrayList<String> listaPK = new ArrayList<String>();
				
					for (int i = 0; i < t.getColuna().size(); i++) {
						listaField.add(t.getColuna().get(i).getName());
						// String qualTipo para verificar se é varchar e qual o
						// tamanho do varchar
						String qualTipo = t.getColuna().get(i).getTiposDeDados().getTipo().toString();
						int tamanho = t.getColuna().get(i).getTiposDeDados().getTamanhoVarchar();
						listaTipo.add(qualTipo.equalsIgnoreCase("VARCHAR") ? qualTipo + "(" + tamanho + ")" : qualTipo);

						// isNull, isUnique e pk pode lançar Exception de
						// NullPointerException, não necessariamente estão
						// inseridas com valores
						TiposDeDados tiposDeDados = t.getColuna().get(i).getTiposDeDados().getIsNull();
						if (tiposDeDados == null) {
							listaIsNull.add("NO");
						} else { // Se o tiposDeDados retorna NOT_NULL não pode
									// conter null, se não pode conter null YES
							listaIsNull.add((t.getColuna().get(i).getTiposDeDados().getIsNull().toString().equalsIgnoreCase("NOT_NULL")) ? "NO" : "YES");
						}
						
						tiposDeDados = t.getColuna().get(i).getTiposDeDados().getIsUnique();
						if (tiposDeDados == null) {
							listaIsUnique.add("");
						} else {
							listaIsUnique.add(t.getColuna().get(i).getTiposDeDados().getIsUnique().toString());
						}
						
						tiposDeDados = t.getColuna().get(i).getTiposDeDados().getPK();
						if (tiposDeDados == null) {
							listaPK.add("");
						} else {
							listaPK.add(t.getColuna().get(i).getTiposDeDados().getPK().toString());
						}
					}
					arrayAV[0] = new AtributosValores("Field", null, listaField);
					arrayAV[1] = new AtributosValores("Type", null, listaTipo);
					arrayAV[2] = new AtributosValores("Null", null, listaIsNull);
					arrayAV[3] = new AtributosValores("Key", null, listaPK);
					arrayAV[4] = new AtributosValores("Extra", null, listaIsUnique);
					TabelaDecorator.printTabela(arrayAV);
					return; // para finalizar o metodo
				}
			}
			// caso não finalize o metodo então não achou o nome da tabela para
			// inserir valores
			console.printOnConsole("Table '"+ response.getNameOfTabela()[0] + "' doesn't exist!\n");
		} else {
			console.printOnConsole("No database selected!\n");
		}
	}

	// INSERT INTO
	private void insertIntoValues(ResponseCommands response) {
		if (data.getCurrentDataBase() != null) {
			for (Tabela t : data.getCurrentDataBase().getTabelas()) {
				if (t.getNameOfTabela().equalsIgnoreCase(
						response.getNameOfTabela()[0])) {// se achou o nome da tabela no DataBase current
					// validação de tipos de dados inseridos
					if((t.getColuna().size() != response.getArgumentos().length)){
						console.printOnConsole("Column count invalid for inserted values, verify syntax version to your BeanSQL!\n");
						return; // para finalizar o metodo
					}					
					// path é nome do arquivo.tbl DataBase.Table exemplo: beansql.tabela1.tbl
					String path = data.getCurrentDataBase().getNameDataBase()
							+ "." + t.getNameOfTabela();
					// String[] arrayItens = new
					// String[response.getArgumentos().length];
					HashMap<String, String> mapa = new HashMap<String, String>();
					for (int i = 0; i < response.getArgumentos().length; i++) {
						// JAVA É FODAAAAAA
						// metodo estatico Coluna pode lançar varias exceptions
						try {
							// if(Coluna.isValidItemInserido(t.getColuna().get(i).getTiposDeDados().getTipo(),
							// response.getArgumentos()[i])){
							if (t.getColuna().get(i).isValidItemInseridoNaColuna(
											t.getColuna().get(i).getTiposDeDados().getTipo(), response.getArgumentos()[i])) {
								// key é o nome da coluna respectiva do for para
								// os argumentos informados, valores são os
								// argumentos (valores) respectivamente
								String gravar = response.getArgumentos()[i];
								if (t.getColuna().get(i).getTiposDeDados().getTipo().toString().equalsIgnoreCase("VARCHAR")
										|| t.getColuna().get(i).getTiposDeDados().getTipo().toString().equalsIgnoreCase("CHAR")
										|| t.getColuna().get(i).getTiposDeDados().getTipo().toString().equalsIgnoreCase("DATE")) {
									gravar = response.getArgumentos()[i].substring(1, response.getArgumentos()[i].length() - 1);
								
								} else if (t.getColuna().get(i).getTiposDeDados().getTipo().toString().equalsIgnoreCase("DOUBLE")) {
									Double d = Double.parseDouble(response.getArgumentos()[i]);
									DecimalFormat format = new DecimalFormat("0.00");
									gravar = format.format(d);
								}
								mapa.put(t.getColuna().get(i).getName(), gravar);

							} else {
								//console.printOnConsole("Invalid values detected \"" + response.getArgumentos()[i] + "\", verify syntax version to your BeanSQL!\n");
								console.printOnConsole("Invalid values detected \"" + response.getArgumentos()[i] + "\" for column '" +t.getColuna().get(i).getName()+"'\n");
								return; // para finalizar o metodo
							}
						} catch (IndexOutOfBoundsException e) {
							console.printOnConsole("Invalid values detected \"" + response.getArgumentos()[i] + "\" for column '" +t.getColuna().get(i).getName()+"'\n");
							return; // para finalizar o metodo
						} catch (NumberFormatException e) {
							console.printOnConsole("Invalid values detected \"" + response.getArgumentos()[i] + "\" for column '" +t.getColuna().get(i).getName()+"'\n");
							return; // para finalizar o metodo
						}
					}
					//premissa se chegou até aqui todos valores inseridos são válidos, porém necessário verificar unicidades e chaves
					//verifica se existe primary key e qual a coluna?
					for(Coluna c : t.getColuna()){
						if(!(c.getTiposDeDados().getPK() == null)){	//se não for null verifica se o valor se repete na coluna que contem a primary key
							
							ArrayList<Tupla> listaTuplas = console.readerListTuplas(path);	//lista para recuperar as tuplas do tabela que o nome do arquivo é o "path" obs: cada tupla um registro um hashmap						
							if(!(listaTuplas == null)){
								//ler dados e verifica se já contem o valor
								ArrayList<String> listaValoresColuna = new ArrayList<String>();//uma lista para atributos valores
								for(Tupla tupla : listaTuplas){		//percorre cada linha, registro que retorna um map, percorre toda a coluna para depois ir para prox coluna										
									//um linha de tupla eh lida, mas add somente uma coluna por vez
									String key = c.getName();	//retorna o nome da coluna pelo será o valor da chave								
									String valor = tupla.getMapaTuplas().get(key);	//retorna o valor correspondente a key que é o nome da coluna
									listaValoresColuna.add(valor.toLowerCase());	//add valor na lista para o array de atributos valores com letras minusculas								
								}
								
								//verifica se na lista de valores recuperada da coluna que contém a primary key contém o valor novo a ser inserido
								if(listaValoresColuna.contains(mapa.get(c.getName().toLowerCase()))){	//recupera o valor da key do mapa que é o nome da coluna
									console.printOnConsole("Duplicate entry '"+mapa.get(c.getName())+"' for key 'PRIMARY' in column '" + c.getName() + "'\n");
									return; // para finalizar o metodo
								}
							}
							break; //se achou finaliza for
						}
					}
				
					Tupla tupla = new Tupla(path, mapa);
					// antes de write verificar uniques, pks, nulls
					console.writeTuplas(tupla);
					String tempo = CentralDeComandos.getFimTempoTransacao();
					console.printOnConsole("Query OK, values inserted successfully" + tempo + "\n\n");
					return; // para finalizar o metodo
				}
			}
			// caso não finalize o metodo então não achou o nome da tabela para
			// inserir valores
			console.printOnConsole("Table '"+ response.getNameOfTabela()[0] + "' doesn't exist!\n");
		} else {
			console.printOnConsole("No database selected!\n");
		}
	}

	// CREATE TABLE
	private void createTable(ResponseCommands response) {
		if (data.getCurrentDataBase() != null) {
			try {
				Tabela tabela = new Tabela(response.getNameOfTabela()[0]);
				for (int i = 0; i < response.getArgumentos().length; i++) { // retorna nome dos atributos e os tipos
					tabela.addColuna(new Coluna(response.getArgumentos()[i], response.getTiposDeDados()[i]));
				}
				data.getCurrentDataBase().addTabela(tabela);
				console.writeDatabase(data.getCurrentDataBase());
				console.printOnConsole("Table created successful\n");
			} catch (TipoDadosBeanSQLException e) {
				console.printOnConsole(e.getMessage());
			} catch (TableBeanSQLException e) {
				console.printOnConsole(e.getMessage());
			}
		} else {
			console.printOnConsole("No database selected!\n");
		}
	}

	/**
	 * Method efetua o print de um unico AtributosValores especificado no
	 * parametro, caso não haja valores (null) NullPointerException e lançada e
	 * print "is empty!"
	 * 
	 * @param AtributosValores
	 */
	private void centralPrintShow(AtributosValores oneAtributoManyValores) {
		AtributosValores[] array = { oneAtributoManyValores };
		try {
			TabelaDecorator.printTabela(array);
		} catch (NullPointerException eNull) {
			// caso aconteça de não existir nomes de database, lança
			// NullPointerException
			console.printOnConsole("No "
					+ oneAtributoManyValores.getNomeAtributo() + ", is empty\n");
		} catch (Exception e) {
			console.printOnConsole("Exception occurred in printShow\n");
			e.printStackTrace();
		}
	}

	/**
	 * Method autentica login e password
	 */
	private void login() {
		String login = "";
		String password = "";
		boolean isAutenticado = false;
		int cont = 0; // contagem de tentativa de acesso

		while (!isAutenticado) { // enquanto nao for autenticado continua loop
									// até o maximo de tentativas

			if (cont >= 2) {
				System.exit(0);
			}
			console.printOnConsole("Enter login> ");
			login = console.readerConsole();
			console.printOnConsole("Enter password> ");
			password = console.readerConsolePassword();
			cont++;

			for (Usuario usuarioOfData : data.getAllUsuarios()) {
				if (login.equalsIgnoreCase(usuarioOfData.getName())
						&& password.equals(usuarioOfData.getPassword())) {
					isAutenticado = true;
				} else {
					console.printOnConsole("Login or password invalid!\n");
				}
			}
		}
	}

	/**
	 * Method carrega a o objeto Data em memoria
	 */
	private void loadData() {
		// 1 verifica se existe um banco no computador
		// 1.1 caso não exista cria um banco no diretorio atual do executavel jar
		// 1.2 caso exista carrega o banco para memoria, contendo os usuarios e os minimundos, databases
		data = console.loadDataPrincipal();
		data.setCurrentDataBase(null); // quando carrega o Banco, datafase default é null
	}

}
