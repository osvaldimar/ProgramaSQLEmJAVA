
package br.com.beansql.validators;

/**
 * Enum com valores de comandos utilizados na aplicação
 * @author osvaldimar.costa
 *
 */
public enum Command {

	SHOW_DATABASES, SHOW_TABLES, CREATE_DATABASE, USE, CREATE_TABLE, DESCRIBE, INSERT_INTO_VALUES, UPDATE_SET_WHERE, DROP_TABLE, DROP_DATABASE, ALTER_TABLE, DELETE_FROM_WHERE, 
	
	SELECT_FROM, SELECT_FROM_JOIN_WHERE, 
	
	INVALID_COMMAND, ERROR_IN, KEY_WORD_RESERVED,
	
	EXIT,
	

}
