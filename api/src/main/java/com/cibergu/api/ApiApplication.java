package com.cibergu.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Esta es la clase principal (la "puerta de entrada") de toda nuestra aplicación.
 * Cuando ejecutamos el proyecto, el servidor empieza a leer instrucciones desde aquí.
 * 
 * La anotación @SpringBootApplication es muy importante. Funciona como un hechizo mágico que:
 * 1. Configura el servidor automáticamente (auto-configuración).
 * 2. Escanea todas nuestras carpetas (controllers, security, exceptions, etc.) buscando otros componentes.
 * 3. Permite poder inyectar dependencias y usar nuestras clases en cualquier parte.
 */
@SpringBootApplication
public class ApiApplication {

	/**
	 * El método 'main' es el punto de inicio de cualquier programa en Java.
	 * Cuando le damos al botón de "Play" para arrancar la API, Java busca exactamente esta función.
	 * 
	 * @param args Son argumentos o configuraciones extra que le podríamos pasar por consola
	 *             al arrancar la aplicación (por ejemplo: --server.port=8081).
	 */
	public static void main(String[] args) {
		
		// Esta línea es la que realmente "enciende el motor" de Spring Boot.
		// Toma nuestra clase principal (ApiApplication.class), arranca un servidor web integrado (normalmente Tomcat)
		// y deja nuestra API escuchando peticiones en internet de forma continua.
		SpringApplication.run(ApiApplication.class, args);
		
	}

}
