package net.danieljurado.dialer.devolveregistro;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import net.danieljurado.dialer.configuracoes.Configuracoes;
import net.danieljurado.dialer.modelo.Ligacao;
import net.danieljurado.dialer.tratadorespecificocliente.TratadorEspecificoCliente;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import al.jdi.dao.beans.ClienteDao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

public class ProcessaLimpaReservaTest {

  private static final Integer OPERADOR = 3;
  private static final String NOME_BASE_DADOS = "BASE";
  @Mock
  private TratadorEspecificoCliente tratadorEspecificoCliente;
  @Mock
  private Configuracoes configuracoes;
  @Mock
  private Ligacao ligacao;
  @Mock
  private Cliente cliente;
  @Mock
  private ResultadoLigacao resultadoLigacao;
  @Mock
  private DaoFactory daoFactory;
  @Mock
  private ClienteDao clienteDao;

  private ProcessaLimpaReserva processaLimpaReserva;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(tratadorEspecificoCliente.obtemClienteDao(daoFactory)).thenReturn(clienteDao);
    when(configuracoes.getOperador()).thenReturn(OPERADOR);
    when(configuracoes.getNomeBaseDados()).thenReturn(NOME_BASE_DADOS);

    processaLimpaReserva = new ProcessaLimpaReserva(tratadorEspecificoCliente, configuracoes);
  }

  @Test
  public void getOrdemDeveriaRetornar() throws Exception {
    assertThat(processaLimpaReserva.getOrdem(), is(12));
  }

  @Test
  public void acceptDeveriaRetornarTrue() throws Exception {
    assertThat(processaLimpaReserva.accept(ligacao, cliente, resultadoLigacao, daoFactory),
        is(true));

  }

  @Test
  public void executaDeveriaLimpar() throws Exception {
    processaLimpaReserva.executa(ligacao, cliente, resultadoLigacao, daoFactory);
    verify(clienteDao).limpaReserva(cliente, OPERADOR, NOME_BASE_DADOS);
  }

  @Test
  public void executaDeveriaRetornarTrue() throws Exception {
    assertThat(processaLimpaReserva.executa(ligacao, cliente, resultadoLigacao, daoFactory),
        is(true));
  }

}