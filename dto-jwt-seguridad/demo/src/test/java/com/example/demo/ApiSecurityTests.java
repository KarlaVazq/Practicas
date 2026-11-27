package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSecurityTests {

    @Autowired MockMvc mvc;
    @Autowired JwtTokenService jwt;
    @Autowired ObjectMapper mapper;

    private static final String PRODUCTO = """
            {"nombre":"Proteína de fresa","descripcion":"2.2 kg","lote":"123456",
             "precioUnitario":1500.5,"fechaCaducidad":"2026-05-04"}""";

    private String token(String role) {
        return "Bearer " + jwt.createToken(new UsernamePasswordAuthenticationToken(
                "tester", "n/a", AuthorityUtils.createAuthorityList("ROLE_" + role)));
    }

    @Test
    void getsSonPublicos() throws Exception {
        for (String ruta : new String[] {"/productos", "/categorias", "/comentarios", "/imagenes"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk());
        }
        mvc.perform(get("/producto/no-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/imagenes/producto/no-existe")).andExpect(status().isNotFound());
    }

    @Test
    void escrituraSinTokenEs401() throws Exception {
        mvc.perform(post("/producto").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userNoPuedeEscribirProductos403() throws Exception {
        mvc.perform(post("/producto").header("Authorization", token("USER"))
                .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreaProductoConLocationYPatchParcial() throws Exception {
        String respuesta = mvc.perform(post("/producto").header("Authorization", token("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        String id = mapper.readTree(respuesta).get("id").asText();

        mvc.perform(patch("/producto/" + id).header("Authorization", token("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Nuevo nombre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo nombre"))
                .andExpect(jsonPath("$.lote").value("123456"));
    }

    @Test
    void dtoInvalidoEs400() throws Exception {
        mvc.perform(post("/producto").header("Authorization", token("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroSiempreCreaUserYLoginEmiteJwt() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"estudiante\",\"password\":\"clave-local-segura-123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"estudiante\",\"password\":\"clave-local-segura-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"estudiante\",\"password\":\"incorrecta-123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void openApiPublicaEsquemaBearer() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"));
    }

    @Test
    void userPuedeEscribirComentariosSiElCuerpoEsValido() throws Exception {
        mvc.perform(post("/comentario").header("Authorization", token("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ana\",\"comentario_corto\":\"Muy bueno\",\"estrellas\":5}"))
                .andExpect(status().isCreated());
    }
}
