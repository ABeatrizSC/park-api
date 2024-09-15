package com.example.park_api;

import com.example.park_api.resources.dto.UserCreateDTO;
import com.example.park_api.resources.dto.UserPasswordDTO;
import com.example.park_api.resources.dto.UserResponseDTO;
import com.example.park_api.resources.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {
    @Autowired
    WebTestClient testClient;

    @Test
    public void createUser_ComUsernameEPasswordValidos_RetornarUsuarioCriadoComStatus201() {
        UserResponseDTO responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("tody@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("tody@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUser_ComUsernameInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("toddy@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("toddy@gmail.", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_ComPasswordInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("tody@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("tody@gmail.com", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("tody@gmail.com", "12345678"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_ComUsernameERepetido_RetornarUsuarioCriadoComStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findUser_ComIdExistente_RetornarUsuarioCriadoComStatus200() {
        UserResponseDTO responseBody = testClient
                .get()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("ana@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = testClient
                .get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("maria@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = testClient
                .get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("maria@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void findUser_ComIdInexistente_RetornarUsuarioCriadoComStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarUsuario_ComUsuarioClienteBuscandoOutroCliente_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editPassword_ComDadosValidos_RetornarUsuarioCriadoComStatus204() {
        testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void editPassword_ComUsuariosDiferentes_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403  );
    }

    @Test
    public void editPassword_ComCamposInvalidos_RetornarErrorMessageComStatus422() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("12345678", "12345678", "12345678"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void editPassword_ComSenhaInvalidas_RetornarErrorMessageComStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123456", "000000"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("000000", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void listarUsuarios_SemQualquerParametro_RetornarListaDeUsuariosComStatus200() {
        List<UserResponseDTO> responseBody = testClient
                .get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.size()).isEqualTo(3);
    }
}
