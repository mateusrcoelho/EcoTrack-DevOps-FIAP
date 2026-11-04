package com.ecotrack.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

public class ApiSteps {
    private static final String BASE = System.getProperty("ecotrack.baseUrl", "http://localhost:8080");
    private Response last;
    private String token, email;
    private final String password = "Teste123!";
    private long residuoId, coletaId;
    private final String residuoJson = "{\"tipoResiduo\":\"Plastico\",\"pesoKg\":10.0,\"statusColeta\":\"PENDENTE\"}";

    @Before
    public void preparar() {
        email = "fiap." + UUID.randomUUID().toString().replace("-", "") + "@example.com";
        token = null;
        residuoId = 0;
        coletaId = 0;
        last = null;
    }

    private Response send(String method, String path, String json, boolean authenticated) {
        var request = RestAssured.given().baseUri(BASE).accept(ContentType.JSON);
        if (authenticated) {
            assertNotNull(token, "Token JWT ausente");
            request.auth().oauth2(token);
        }
        if (json != null) request.contentType(ContentType.JSON).body(json);
        return switch (method) {
            case "GET" -> request.get(path);
            case "POST" -> request.post(path);
            case "PUT" -> request.put(path);
            case "DELETE" -> request.delete(path);
            default -> throw new IllegalArgumentException(method);
        };
    }

    private String registro() {
        return String.format("{\"nome\":\"Estudante FIAP\",\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"USER\"}", email, password);
    }

    @Dado("que a API EcoTrack está disponível")
    public void apiDisponivel() {
        Response health = send("GET", "/actuator/health", null, false);
        assertEquals(200, health.statusCode(), "Inicie a aplicação em " + BASE);
        health.then().body(matchesJsonSchemaInClasspath("schemas/health.schema.json"));
    }

    @Dado("que possuo uma conta autenticada")
    public void contaAutenticada() {
        last = send("POST", "/auth/register", registro(), false);
        assertEquals(201, last.statusCode(), last.asString());
        last.then().body(matchesJsonSchemaInClasspath("schemas/auth.schema.json"));
        token = last.jsonPath().getString("token");
        assertNotNull(token);
    }

    @Quando("cadastro uma nova conta válida")
    public void cadastrarConta() { last = send("POST", "/auth/register", registro(), false); }

    @Quando("realizo login com minhas credenciais")
    public void login() {
        last = send("POST", "/auth/login", String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, password), false);
    }

    @Quando("tento login com senha incorreta")
    public void loginInvalido() {
        last = send("POST", "/auth/login", "{\"email\":\"" + email + "\",\"senha\":\"errada\"}", false);
    }

    @Quando("tento cadastrar a mesma conta novamente")
    public void duplicado() { last = send("POST", "/auth/register", registro(), false); }

    @Quando("cadastro um resíduo válido")
    public void cadastrarResiduo() {
        last = send("POST", "/residuos", residuoJson, true);
        if (last.statusCode() == 201) residuoId = last.jsonPath().getLong("id");
    }

    @Dado("que cadastrei um resíduo válido")
    public void residuoExistente() {
        cadastrarResiduo();
        assertEquals(201, last.statusCode(), last.asString());
    }

    @Quando("consulto a lista de resíduos")
    public void listarResiduos() { last = send("GET", "/residuos", null, true); }

    @Quando("consulto o resíduo por identificador")
    public void buscarResiduo() { last = send("GET", "/residuos/" + residuoId, null, true); }

    @Quando("atualizo o resíduo")
    public void atualizarResiduo() {
        last = send("PUT", "/residuos/" + residuoId, "{\"tipoResiduo\":\"Papel\",\"pesoKg\":5.0,\"statusColeta\":\"COLETADA\"}", true);
    }

    @Quando("excluo o resíduo")
    public void excluirResiduo() { last = send("DELETE", "/residuos/" + residuoId, null, true); }

    @Quando("tento cadastrar um resíduo com peso zero")
    public void pesoInvalido() {
        last = send("POST", "/residuos", "{\"tipoResiduo\":\"Plastico\",\"pesoKg\":0}", true);
    }

    @Quando("busco um resíduo inexistente")
    public void residuoInexistente() { last = send("GET", "/residuos/999999999", null, true); }

    @Quando("acesso resíduos sem autenticação")
    public void semJwt() { last = send("GET", "/residuos", null, false); }

    private String coletaPayload(String empresa) {
        return "{\"dataColeta\":\"2026-11-03\",\"empresaResponsavel\":\"" + empresa + "\",\"status\":\"AGENDADA\",\"residuoId\":" + residuoId + "}";
    }

    @Quando("cadastro uma coleta para esse resíduo")
    public void cadastrarColeta() {
        last = send("POST", "/coletas", coletaPayload("Eco Coleta"), true);
        if (last.statusCode() == 201) coletaId = last.jsonPath().getLong("id");
    }

    @Dado("que cadastrei uma coleta para esse resíduo")
    public void coletaExistente() {
        cadastrarColeta();
        assertEquals(201, last.statusCode(), last.asString());
    }

    @Quando("consulto a lista de coletas")
    public void listarColetas() { last = send("GET", "/coletas", null, true); }

    @Quando("consulto a coleta por identificador")
    public void buscarColeta() { last = send("GET", "/coletas/" + coletaId, null, true); }

    @Quando("atualizo a coleta")
    public void atualizarColeta() { last = send("PUT", "/coletas/" + coletaId, coletaPayload("Nova Empresa"), true); }

    @Quando("excluo a coleta")
    public void excluirColeta() { last = send("DELETE", "/coletas/" + coletaId, null, true); }

    @Quando("tento cadastrar uma coleta sem empresa responsável")
    public void coletaInvalida() { last = send("POST", "/coletas", coletaPayload(""), true); }

    @Quando("consulto os indicadores ESG")
    public void indicadores() { last = send("GET", "/indicadores", null, true); }

    @Quando("gero o relatório ambiental")
    public void relatorio() { last = send("POST", "/relatorios/ambiental", null, true); }

    @Então("o status HTTP deve ser {int}")
    public void status(int expected) { assertEquals(expected, last.statusCode(), last.asString()); }

    @Então("a resposta segue o contrato {string}")
    public void contrato(String name) {
        last.then().contentType(ContentType.JSON).body(matchesJsonSchemaInClasspath("schemas/" + name + ".schema.json"));
    }

    @Então("o token JWT está presente")
    public void jwtPresente() {
        String jwt = last.jsonPath().getString("token");
        assertNotNull(jwt);
        assertFalse(jwt.isBlank());
    }

    @Então("^o cálculo de CO2 evitado corresponde a ([0-9]+\\.[0-9]+) kg$")
    public void co2(String expected) { assertEquals(Double.parseDouble(expected), last.jsonPath().getDouble("impactoCo2Kg"), 0.01); }

    @Então("a lista de resíduos segue o contrato JSON Schema")
    public void listaResiduos() {
        last.then().contentType(ContentType.JSON).body(matchesJsonSchemaInClasspath("schemas/residuos.schema.json"));
    }

    @Então("a lista de coletas segue o contrato JSON Schema")
    public void listaColetas() {
        last.then().contentType(ContentType.JSON).body(matchesJsonSchemaInClasspath("schemas/coletas.schema.json"));
    }

    @Então("o texto confirma a exclusão do resíduo")
    public void textoExclusao() { assertTrue(last.asString().contains("excluído com sucesso")); }

    @Então("a resposta não possui corpo")
    public void semCorpo() { assertTrue(last.asString().isEmpty()); }
}
