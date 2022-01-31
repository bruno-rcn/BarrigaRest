package testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import core.BaseTeste;
import io.restassured.RestAssured;

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
	
}


