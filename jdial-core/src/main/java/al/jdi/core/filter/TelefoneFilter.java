package al.jdi.core.filter;

import java.util.List;

import al.jdi.dao.model.Telefone;

public interface TelefoneFilter {
  List<Telefone> filter(List<Telefone> telefones);
}