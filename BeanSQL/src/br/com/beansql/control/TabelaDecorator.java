package br.com.beansql.control;

import java.text.DecimalFormat;

import br.com.beansql.model.AtributosValores;

public class TabelaDecorator {
	
	public static MyConsole console = null;		//Console atual para saida de dados
	
	private TabelaDecorator() {		
	}
	/**
	 * Method para printar na tela os atributos e valores de uma tabela
	 * @param listaAtributosValores
	 */
	
	public static void printTabela(AtributosValores[] listaAtributosValores){
		//listaAtributosValores = lista;
		int[] arrayTamanhoCampo = new int[listaAtributosValores.length];
		//for para armazenar em um array os tamanhos dos campos de cada atributo
		for(int i = 0; i < arrayTamanhoCampo.length; i++){
			//verifica a maior String de cada valor dos atributos
			int tamanho = verificarMaiorStringValor(listaAtributosValores[i]);
			if(tamanho < listaAtributosValores[i].getNomeAtributo().length()){
				tamanho = listaAtributosValores[i].getNomeAtributo().length();
			}
			//armazena no array o maior tamanho do campo para desenha
			arrayTamanhoCampo[i] = tamanho;	
		}
		//for principal para printar o nome dos atributos e os valores das tuplas
		
		//prints linha de cima do nome dos atributos
		for(int i = 0; i < arrayTamanhoCampo.length; i++){
			printBordas(arrayTamanhoCampo[i], (i == arrayTamanhoCampo.length-1 ? true : false));
		}
		
		//***prints atributos***
		for(int i = 0; i < listaAtributosValores.length; i++){
			printTuplas(arrayTamanhoCampo[i], 
					listaAtributosValores[i].getNomeAtributo(), 
					(i == listaAtributosValores.length-1 ? true : false));			
		}		
		
		//prints linha de baixo do nome dos atributos
		for(int i = 0; i < arrayTamanhoCampo.length; i++){
			printBordas(arrayTamanhoCampo[i], (i == arrayTamanhoCampo.length-1 ? true : false));
		}
		
		//***prints valores***		
		int totalTuplas = listaAtributosValores[0].getValores().size(); //total de tuplas na tabela
		//variavel i para contar numeros de registros
		//System.out.println("chegou antes size= " + totalTuplas);
		for(int i = 0; i < totalTuplas; i++){
			//System.out.println("chegou tuplas");
			for(int j = 0; j < listaAtributosValores.length; j++){
				//System.out.println("chegou lista");
				printTuplas(arrayTamanhoCampo[j], 
						listaAtributosValores[j].getValores().get(i), 
						(j == listaAtributosValores.length-1 ? true : false));
			}						
		}
		
		//prints linha de baixo "rodape"
		for(int i = 0; i < arrayTamanhoCampo.length; i++){
			printBordas(arrayTamanhoCampo[i], (i == arrayTamanhoCampo.length-1 ? true : false));		
		}
		//após printar a tabela verifica qual o tamanho de valores máximos de qualquer lista de valores do array do AtributosValores e printa esta quantidade
		String saidaQuantRows = listaAtributosValores[0].getValores().size() == 1 ? "1 row in set" : listaAtributosValores[0].getValores().size()+" rows in set";
		String tempo = CentralDeComandos.getFimTempoTransacao();
		console.printOnConsole(saidaQuantRows + tempo + "\n\n");	//após todos processmentos e saida, printa mais uma quebra de linha
		
	}
	/**
	 * Method para verificar qual o maior valor de cada campo da tabela para calcular o desenho
	 * @param av
	 * @return int
	 */
	private static int verificarMaiorStringValor(AtributosValores av){
		//int tamanho = 0;
		int tamanho = av.getNomeAtributo().length();	//o tamanho começa com nome do campo (nome do atributo), caso os valores sejam maiores é aumentado
		for(String valor : av.getValores()){
			if(tamanho < valor.length()){
				tamanho = valor.length();
			}
		}
		return tamanho + 4; //acrescenta mais 4 espaços
	}
	/**
	 * Method para printar
	 * @param nomeAtributo
	 */
	private static void printBordas(int tamanhoCampo, boolean isUltimoAtributo){
		System.out.print("+");
		for(int j = 1; j < tamanhoCampo; j++){
			console.printOnConsole("-");
			//System.out.print("-");
		}
		if(isUltimoAtributo){
			console.printOnConsole("-+\n");
			//System.out.println("-+");
		}
	}
	/**
	 * Method para printar as Tuplas, cada linha de registros
	 * @param tamanho,
	 */
	private static void printTuplas(int tamanhoCampo, String valor, boolean isUltimoCampo){
		System.out.print("| " + valor); //dois caracteres + o nome do atributo
		//for com i iniciando de 4 caracters "|  |" + a string do nome atributo
		for(int i = valor.length() + 2;
				i < tamanhoCampo; i++){
			console.printOnConsole(" ");
			//System.out.print(" ");
		}			
		if(isUltimoCampo){
			console.printOnConsole(" |\n");
			//System.out.println(" |");
		}
	}
}
