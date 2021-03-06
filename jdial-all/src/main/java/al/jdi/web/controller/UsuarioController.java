package al.jdi.web.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import al.jdi.dao.model.Usuario;
import al.jdi.dao.model.Usuario.TipoPerfil;
import al.jdi.web.component.DaoFactoryRequest;
import al.jdi.web.interceptor.DBLogInterceptor.LogAcesso;
import al.jdi.web.interceptor.Permissao;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Permissao(TipoPerfil.ADMINISTRADOR)
@Controller
public class UsuarioController {
  private final DaoFactoryRequest daoFactoryRequest;
  private final Result result;

  @Deprecated
  public UsuarioController() {
    this(null, null);
  }

  @Inject
  public UsuarioController(DaoFactoryRequest daoFactoryRequest, Result result) {
    this.daoFactoryRequest = daoFactoryRequest;
    this.result = result;
  }

  @LogAcesso
  public void add(Usuario usuario) {
    if (StringUtils.isBlank(usuario.getSenha())) {
      result.include("errors", "Senha nao pode ser vazia!");
      result.use(Results.logic()).redirectTo(UsuarioController.class)
          .formularioUsuario("add", usuario);
      return;
    }
    usuario.setSenha(usuario.criptografaSenha(usuario.getSenha()));
    daoFactoryRequest.get().getUsuarioDao().adiciona(usuario);
    result.use(Results.logic()).redirectTo(UsuarioController.class).list();
  }

  @Put
  @Path("/usuario")
  public void adicionar() {
    result.use(Results.logic()).redirectTo(UsuarioController.class).formularioUsuario("add", null);
  }

  @LogAcesso
  @Delete
  @Path("/usuario/{usuario.id}")
  public void delete(Usuario usuario) {
    usuario = daoFactoryRequest.get().getUsuarioDao().procura(usuario.getId());
    daoFactoryRequest.get().getUsuarioDao().remove(usuario);
    result.use(Results.logic()).redirectTo(UsuarioController.class).list();
  }

  @LogAcesso
  public void edit(Usuario usuario) {
    Usuario u = daoFactoryRequest.get().getUsuarioDao().procura(usuario.getId());
    u.setLogin(usuario.getLogin());
    u.setNome(usuario.getNome());
    u.setTipoPerfil(usuario.getTipoPerfil());
    if (!isBlank(usuario.getSenha())) {
      usuario.setSenha(usuario.criptografaSenha(usuario.getSenha()));
    }
    daoFactoryRequest.get().getUsuarioDao().atualiza(u);
    result.use(Results.logic()).redirectTo(UsuarioController.class).list();
  }

  @Get
  @Path("/usuario/{usuario.id}")
  public void editar(Usuario usuario) {
    usuario = daoFactoryRequest.get().getUsuarioDao().procura(usuario.getId());
    result.use(Results.logic()).forwardTo(UsuarioController.class)
        .formularioUsuario("edit", usuario);
  }

  public void formularioUsuario(String action, Usuario usuario) {
    result.include("formAction", action);
    result.include("usuario", usuario);
    result.include("tiposPerfil", TipoPerfil.values());
  }

  @Get
  @Path("/usuarios")
  public Collection<Usuario> list() {
    return daoFactoryRequest.get().getUsuarioDao().listaTudo();
  }

}
