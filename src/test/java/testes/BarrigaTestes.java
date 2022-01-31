package testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.manipulation.Filterable;
import org.junit.runners.MethodSorters;

import core.BaseTeste;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import utils.Data;
import utils.Movimentacao;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // ira executar os testes em ordem alfabetica
public class BarrigaTestes extends BaseTeste {
	
	private static String CONTA_NAME = "Conta" + System.nanoTime();
	private static Integer CONTA_ID; 
	private static Integer MOV_ID; 
	
	@BeforeClass
	public static void login() {
		// extracao do token
		Map<String, String> login = new HashMap<>();
		login.put("email", "rock@lee.com.br");
		login.put("senha", "1234");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}


	
	@Test
	public void ct02_inserirConta() {		
		CONTA_ID = given()
			.body("{\"nome\": \""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void ct03_alterarConta() {
		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
		;
	}
	
	@Test
	public void ct04_contaComNomeRepetido() {
		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void ct05_inserirMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao(Data.getDataDiferencaDias(-1));
		mov.setData_pagamento(Data.getDataDiferencaDias(3));
		mov.setValor(100f);
		mov.setStatus(true);
		
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void ct06_validarCampoObrigatorioMovimentacao() {
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void ct07_inserirMovimentacaoComDataFutura() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao(Data.getDataDiferencaDias(2));
		mov.setData_pagamento(Data.getDataDiferencaDias(2));
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
		;
	}
	
	@Test
	public void ct08_naoDeveDeletarContaContaComMovimentacao() {
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", Matchers.is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void ct09_calcularSaldoContas() {
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("100.00"))
		;
	}
	
	@Test
	public void ct10_deletarMovimentacao() {
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/movimentacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void ct011_naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		RestAssured.given()
		.when()
			.get("/contas")
		.then()
		 .statusCode(401)
		;
	}
	
}


