package ServidorPersistente;

import java.util.List;

import Dominio.IExecutionPeriod;
import Dominio.IExecutionYear;
import ServidorPersistente.exceptions.ExistingPersistentException;

/**
 * Created on 11/Fev/2003
 * @author Jo�o Mota
 * Package ServidorPersistente
 * 
 */
public interface IPersistentExecutionPeriod extends IPersistentObject {

	/**
	 * 
	 * @return ArrayList
	 * @throws ExcepcaoPersistencia
	 */
	public List readAllExecutionPeriod() throws ExcepcaoPersistencia;

	/**
	 * 
	 * @param executionPeriod
	 * @return boolean
	 */
	public void writeExecutionPeriod(IExecutionPeriod executionPeriod) throws ExcepcaoPersistencia, ExistingPersistentException;
	/**
	 * 
	 * @param executionPeriod
	 * @return boolean
	 */
	public boolean delete(IExecutionPeriod executionPeriod);
	/**
	 * 
	 * @return boolean
	 */
	public boolean deleteAll();
	/**
	 * Returns the actual executionPeriod using the actual date.
	 * @return IExecutionPeriod
	 * @throws ExcepcaoPersistencia
	 */
	public IExecutionPeriod readActualExecutionPeriod() throws ExcepcaoPersistencia;	
	
	/**
	 * Method readByNameAndExecutionYear.
	 * @param string
	 * @param iExecutionYear
	 * @return IExecutionPeriod
	 */
	public IExecutionPeriod readByNameAndExecutionYear(
		String executionPeriodName,
		IExecutionYear executionYear) throws ExcepcaoPersistencia;

}
