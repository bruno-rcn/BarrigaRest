package testes;

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

}
