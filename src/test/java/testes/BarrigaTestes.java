package testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import core.BaseTeste;
import io.restassured.RestAssured;
import utils.Movimentacao;

public class BarrigaTestes extends BaseTeste {
	
	private String TOKEN;
	
	@Before
	public void login() {
		// extracao do token
		Map<String, String> login = new HashMap<>();
		login.put("email", "rock@lee.com.br");
		login.put("senha", "1234");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
	}
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		RestAssured.given()
		.when()
			.get("/contas")
		.then()
		 .statusCode(401)
		;
	}

	
	@Test
	public void inserirConta() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta RockLee\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void alterarConta() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta RockLee alterada\"}")
		.when()
			.put("/contas/1052073")
		.then()
			.statusCode(200)
		;
	}
	
	@Test
	public void contaComNomeRepetido() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta RockLee alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void inserirMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(1052073);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2000");
		mov.setData_pagamento("10/05/2010");
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
}


