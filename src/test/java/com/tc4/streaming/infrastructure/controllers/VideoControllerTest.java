package com.tc4.streaming.infrastructure.controllers;

import com.tc4.streaming.entities.VideoEntity;
import com.tc4.streaming.infrastructure.persistence.VideoEntityAux;
import com.tc4.streaming.usercases.VideoCrudUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class VideoControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private VideoCrudUseCase videoCrudUseCase;
    @MockBean
    private VideoDTOMapper videoDTOMapper;

    @InjectMocks
    private VideoController videoController;

    @Test
    @DisplayName("Cria um video com sucesso")
    void createComSucesso() {

        LocalDate dataDaPublicacao = LocalDate.parse("2024-01-16");
        CreateVideoRequest request = new CreateVideoRequest( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");
        VideoEntity videoEntity = new VideoEntity( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");
        CreateVideoResponse response = new CreateVideoResponse( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");

        Mockito.when(videoDTOMapper.toVideoEntity(any(CreateVideoRequest.class))).thenReturn(videoEntity);
        Mockito.when(videoCrudUseCase.criarVideo(any(VideoEntity.class))).thenReturn(Mono.just(videoEntity));
        Mockito.when(videoDTOMapper.toResponse(any(VideoEntity.class))).thenReturn(response);

        webTestClient.post().uri("/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(videoCrudUseCase).criarVideo(any(VideoEntity.class));
    }

    @Test
    @DisplayName("Não cria um video com sucesso")
    void createSemSucesso() {

        LocalDate dataDaPublicacao = LocalDate.parse("2024-01-16");
        CreateVideoRequest request = new CreateVideoRequest( "","", "", "", dataDaPublicacao, "");

        webTestClient.post().uri("/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Obter todos os videos com sucesso")
    void obterTodosVideosComSucesso() {

        LocalDate dataDaPublicacao = LocalDate.parse("2024-01-16");
        VideoEntityAux videoEntityAux = new VideoEntityAux( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");

        Mockito.when(videoCrudUseCase.obterTodosVideos()).thenReturn(Flux.just(videoEntityAux));

        webTestClient.get().uri("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0]id").isEqualTo("1234");
    }

    @Test
    @DisplayName("Obter um video por codigo com sucesso")
    void obterVideoPorCodigoComSucesso() {

        LocalDate dataDaPublicacao = LocalDate.parse("2024-01-16");
        VideoEntityAux videoEntityAux = new VideoEntityAux( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");

        Mockito.when(videoCrudUseCase.obterVideoPorCodigo(anyString())).thenReturn(Mono.just(videoEntityAux));

        webTestClient.get().uri("/videos/1234")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                        .expectBody()
                                .jsonPath("$.id").isEqualTo("1234");

        Mockito.verify(videoCrudUseCase).obterVideoPorCodigo(anyString());

    }

    @Test
    @DisplayName("Obter um video por codigo sem sucesso")
    void obterVideoPorCodigoSemSucesso() {

        webTestClient.get().uri("/videos/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    @DisplayName("Edita um video por codigo com sucesso")
    void editarVideoComSecesso() {

        LocalDate dataDaPublicacao = LocalDate.parse("2024-01-16");
        VideoEntityAux videoEntityAux = new VideoEntityAux( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");
        VideoEntity videoEntity = new VideoEntity( "1234","Tema", "Filme", "https://filme.com", dataDaPublicacao, "filme");

        Mockito.when(videoCrudUseCase.editarVideo("1234", videoEntityAux)).thenReturn(Mono.just(videoEntity));

        webTestClient.put().uri("/videos/editar/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(videoEntityAux))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Apaga um video por codigo com sucesso")
    void apagarVideoComSucesso() {


        Mockito.when(videoCrudUseCase.apagarVideo("1234")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/videos/apagar/1234")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(videoCrudUseCase).apagarVideo(anyString());

    }
    @Test
    @DisplayName("Apaga um video por codigo sem sucesso")
    void apagarVideoSemSucesso() {

        webTestClient.delete().uri("/videos/ ")
                .exchange()
                .expectStatus().is4xxClientError();

    }

    @Test
    void obterVideoPorCategoria() {
    }

    @Test
    void obterVideoPorTitulo() {
    }

    @Test
    void obterVideoPorData() {
    }

    @Test
    void obterVideoPorTituloEData() {
    }
}