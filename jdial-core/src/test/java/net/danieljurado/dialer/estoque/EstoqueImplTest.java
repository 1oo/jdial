package net.danieljurado.dialer.estoque;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Provider;

import net.danieljurado.dialer.configuracoes.Configuracoes;
import net.danieljurado.dialer.devolveregistro.DevolveRegistro;
import net.danieljurado.dialer.filter.TelefoneFilter;
import net.danieljurado.dialer.modelo.Discavel;
import net.danieljurado.dialer.modelo.Discavel.Factory;
import net.danieljurado.dialer.modelo.Ligacao;
import net.danieljurado.dialer.modelo.Providencia;
import net.danieljurado.dialer.modelo.Providencia.Codigo;
import net.danieljurado.dialer.tratadorespecificocliente.TratadorEspecificoCliente;

import org.jdial.common.Engine;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import al.jdi.dao.beans.Dao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Cliente;

public class EstoqueImplTest {

  private static final String CAMPANHA = "CAMPANHA";
  private static final DateTime DATA_BANCO = new DateTime();
  private static final Period INTERVALO_MONITORACAO = Period.seconds(5);
  @Mock
  private Configuracoes configuracoes;
  @Mock
  private Provider<DaoFactory> daoFactoryProvider;
  @Mock
  private DevolveRegistro devolveRegistro;
  @Mock
  private TratadorEspecificoCliente tratadorEspecificoCliente;
  @Mock
  private Factory discavelFactory;
  @Mock
  private Engine.Factory engineFactory;

  @Mock
  private ExtraidorClientes extraidorClientes;
  @Mock
  private Estoque estoqueAgendados;
  @Mock
  private Map<Codigo, Providencia> providencias;
  @Mock
  private DaoFactory daoFactory;
  @Mock
  private Dao<Campanha> campanhaDao;
  @Mock
  private Campanha campanha;
  @Mock
  private Registro registro;
  @Mock
  private Cliente cliente;
  @Mock
  private Discavel discavel;
  @Mock
  private TelefoneFilter telefoneFilter;

  private Collection<Registro> estoque;

  private EstoqueImpl estoqueImpl;

  @Before
  public void setUp() throws Exception {
    initMocks(this);

    when(configuracoes.getNomeCampanha()).thenReturn(CAMPANHA);

    when(daoFactoryProvider.get()).thenReturn(daoFactory);
    when(daoFactory.getDataBanco()).thenReturn(DATA_BANCO);
    when(daoFactory.getCampanhaDao()).thenReturn(campanhaDao);
    when(campanhaDao.procura(CAMPANHA)).thenReturn(campanha);

    when(discavelFactory.create(cliente)).thenReturn(discavel);

    when(registro.getCliente()).thenReturn(cliente);
    when(discavel.getCliente()).thenReturn(cliente);

    estoque = new LinkedList<Registro>(Arrays.asList(registro));

    estoqueImpl =
        new EstoqueImpl(configuracoes, daoFactoryProvider, devolveRegistro,
            tratadorEspecificoCliente, discavelFactory, engineFactory, estoque, extraidorClientes,
            estoqueAgendados, INTERVALO_MONITORACAO, providencias, telefoneFilter);
  }

  @Test
  public void limpaMemoriaPorSolicitacaoNaoDeveriaLimparSeCampanhaNaoPedir() throws Exception {
    estoqueImpl.limpaMemoriaPorSolicitacao(daoFactory);
    assertThat(estoque.contains(registro), is(true));
  }

  @Test
  public void limpaMemoriaPorSolicitacaoDeveriaLimparSeCampanhaPedir() throws Exception {
    when(campanha.isLimpaMemoria()).thenReturn(true);
    assertThat(estoque.isEmpty(), is(false));
    estoqueImpl.limpaMemoriaPorSolicitacao(daoFactory);
    assertThat(estoque.isEmpty(), is(true));
  }

  @Test
  public void limpaMemoriaPorSolicitacaoDeveriaDevolverRegistroSeLimparMemoria() throws Exception {
    when(campanha.isLimpaMemoria()).thenReturn(true);
    estoqueImpl.limpaMemoriaPorSolicitacao(daoFactory);
    ArgumentCaptor<Ligacao> captor = ArgumentCaptor.forClass(Ligacao.class);
    verify(devolveRegistro).devolveLigacao(captor.capture());
    Ligacao ligacao = captor.getValue();
    assertThat(ligacao.getDiscavel(), is(sameInstance(discavel)));
  }
}