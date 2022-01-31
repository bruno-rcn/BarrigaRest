package core;

import io.restassured.http.ContentType;

public interface Constantes {
	
	String APP_BASE_URL = "https://barrigarest.wcaquino.me";
	Integer APP_PORT = 443;
	String APP_BASE_PATH = ""; // serve para caso a api tenha v1, v2, etc ...
	
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
//	Long MAX_TIMEOUT = 2000L;

}
