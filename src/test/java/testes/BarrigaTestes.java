package testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import core.BaseTeste;
import io.restassured.RestAssured;

public class BarrigaTestes extends BaseTeste {
	
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
		
		// extracao do token
		Map<String, String> login = new HashMap<>();
		login.put("email", "rock@lee.com.br");
		login.put("senha", "1234");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		System.out.println(token);
		
		// incluir a conta
		given()
			.header("Authorization", "JWT " + token)
			.body("{\"nome\": \"conta RockLee\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
}


