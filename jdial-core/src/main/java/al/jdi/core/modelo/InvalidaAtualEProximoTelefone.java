package al.jdi.core.modelo;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.jdi.core.modelo.ModeloModule.ProvidenciaInvalidaAtualEProximoTelefone;
import al.jdi.core.modelo.ModeloModule.ProvidenciaProximoTelefone;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.Telefone;

@ProvidenciaInvalidaAtualEProximoTelefone
class InvalidaAtualEProximoTelefone implements Providencia {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final Instance<ProximoTelefone> proximoTelefone;

  @Inject
  InvalidaAtualEProximoTelefone(
      @ProvidenciaProximoTelefone Instance<ProximoTelefone> proximoTelefone) {
    this.proximoTelefone = proximoTelefone;
  }

  @Override
  public Telefone getTelefone(DaoFactory daoFactory, Cliente cliente) {
    logger.debug("Invalida atual e proximo para cliente {}...", cliente);
    Telefone result = cliente.getTelefone();
    if (result == null) {
      logger.debug("Cliente {} nao possuia telefone atual", cliente);
      return proximoTelefone.get().getTelefone(daoFactory, cliente);
    }
    logger.debug("Vai efetivamente invalidar para cliente {} Id {} DDD {} TEL {}", new Object[] {
        cliente, result, result.getDdd(), result.getTelefone()});
    result.setUtil(false);
    daoFactory.getTelefoneDao().atualiza(result);
    daoFactory.getClienteDao().atualiza(cliente);
    return proximoTelefone.get().getTelefone(daoFactory, cliente);
  }

  @Override
  public Codigo getCodigo() {
    return Providencia.Codigo.INVALIDA_ATUAL_E_PROXIMO_TELEFONE;
  }

}
