<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="jurado"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<script type="text/javascript">
	function ajaxAtivarDesativar(ref, id, mailing) {
		$.post("<c:url value="/mailing/ajaxAtivarDesativar"/>", {
			'mailing.id' : id
		}, function(dados, status) {
			if (status == "success") {
				alert("Sucesso!");
				$(ref).val(dados);
			} else {
				alert("Erro!");
			}
		});
	}
</script>

<h1>Mailings</h1>
<h2>Campanha: ${campanha.nome } - ${campanha.descricao }</h2>
<table>
	<thead>
		<td>idMailing</td>
		<td>Nome</td>
		<td>Descri��o</td>
		<td>Data in�cio</td>
		<td>Data t�rmino</td>
		<td>Manipula��o</td>
		<td></td>
	</thead>
	<c:forEach var="mailing" items="${mailingList }" varStatus="loopStatus">
		<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
			<td>${mailing.id }</td>
			<td>${mailing.nome }</td>
			<td>${mailing.descricao }</td>
			<td><joda:format value="${mailing.dataInicial }"
					pattern="dd/MM/yyyy" /></td>
			<td><joda:format value="${mailing.dataFinal }"
					pattern="dd/MM/yyyy" /></td>
			<td><jurado:botaoSubmit
					value="${mailing.ativo?'Desativar':'Ativar'}"
					clickEvent="ajaxAtivarDesativar($(this),${mailing.id})" /></td>
			<td><form action="<c:url value="/mailing/editar"/>">
					<input type="hidden" name="mailing.id" value="${mailing.id }" />
					<jurado:botaoSubmit value="Editar" />
				</form></td>
			<td><form action="<c:url value="/mailing/formularioPurge"/>">
					<input type="hidden" name="mailing.id" value="${mailing.id }" />
					<jurado:botaoSubmit value="Expurgar" />
				</form></td>
			<td><form action="<c:url value="/mailing/delete"/>"
					method="post" onsubmit="return confirm('Tem certeza???');">
					<input type="hidden" name="mailing.id" value="${mailing.id }" />
					<jurado:botaoSubmit value="Apagar" />
				</form></td>
		</tr>
	</c:forEach>
</table>
<form method="post" action="<c:url value="/mailing"/>">
	<jurado:botaoSubmit value="Novo..." method="PUT" />
</form>
