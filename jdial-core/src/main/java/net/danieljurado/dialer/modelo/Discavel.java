package net.danieljurado.dialer.modelo;

import al.jdi.dao.model.Cliente;

public interface Discavel {

  public interface Factory {
    Discavel create(Cliente cliente);
  }

  String getChave();

  Cliente getCliente();

  String getDigitoSaida();

  String getDdd();

  String getTelefone();

  void setCliente(Cliente procura);

  String getDestino();
}
