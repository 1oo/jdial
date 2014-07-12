package al.jdi.core.devolveregistro;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import al.jdi.core.modelo.Ligacao;
import al.jdi.dao.beans.Dao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.EstadoCliente;
import al.jdi.dao.model.ResultadoLigacao;

public class ProcessaAsseguraExistenciaReservaTest {

  @Mock
  private Ligacao ligacao;
  @Mock
  private Cliente cliente;
  @Mock
  private ResultadoLigacao resultadoLigacao;
  @Mock
  private DaoFactory daoFactory;
  @Mock
  private EstadoCliente estadoClienteReservado;
  @Mock
  private EstadoCliente estadoCliente;
  @Mock
  private Dao<EstadoCliente> estadoClienteDao;
  @Mock
  private Logger logger;

  private ProcessaAsseguraExistenciaReserva processaAsseguraExistenciaReserva;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(daoFactory.getEstadoClienteDao()).thenReturn(estadoClienteDao);
    when(estadoClienteDao.procura("Reservado pelo Discador")).thenReturn(estadoClienteReservado);
    when(cliente.getEstadoCliente()).thenReturn(estadoCliente);
    processaAsseguraExistenciaReserva = new ProcessaAsseguraExistenciaReserva(logger);
  }

  @Test
  public void getOrdemDeveriaRetornar() throws Exception {
    assertThat(processaAsseguraExistenciaReserva.getOrdem(), is(4));
  }

  @Test
  public void acceptDeveriaRetornarTrue() throws Exception {
    assertThat(
        processaAsseguraExistenciaReserva.accept(ligacao, cliente, resultadoLigacao, daoFactory),
        is(true));
  }

  @Test
  public void acceptDeveriaRetornarFalse() throws Exception {
    when(cliente.getEstadoCliente()).thenReturn(estadoClienteReservado);
    assertThat(
        processaAsseguraExistenciaReserva.accept(ligacao, cliente, resultadoLigacao, daoFactory),
        is(false));
  }

  @Test
  public void executaDeveriaRetornarTrue() throws Exception {
    assertThat(
        processaAsseguraExistenciaReserva.executa(ligacao, cliente, resultadoLigacao, daoFactory),
        is(true));
  }

}