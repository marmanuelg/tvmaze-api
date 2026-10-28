package com.evaluacion.tvmaze.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.evaluacion.tvmaze.client.dto.TvMazeSearchResult;
import com.evaluacion.tvmaze.exception.ShowNotFoundException;
import com.evaluacion.tvmaze.exception.TvMazeApiException;

class TvMazeClientTest {

    private static final String BASE_URL = "https://api.tvmaze.com";

    private MockRestServiceServer server;
    private TvMazeClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TvMazeClient(builder.baseUrl(BASE_URL).build());
    }

    @Test
    void searchShowsMapsTvMazeResponse() {
        String json = """
                [{"score": 0.9, "show": {
                    "id": 139, "name": "Girls", "summary": "<p>Resumen</p>", "genres": ["Drama"],
                    "network": {"id": 8, "name": "HBO"}, "webChannel": null, "language": "English"}}]
                """;
        server.expect(requestTo(BASE_URL + "/search/shows?q=girls"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<TvMazeSearchResult> results = client.searchShows("girls");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).show().id()).isEqualTo(139L);
        assertThat(results.get(0).show().channelName()).isEqualTo("HBO");
        server.verify();
    }

    @Test
    void getShowByIdReturnsFullObject() {
        server.expect(requestTo(BASE_URL + "/shows/139"))
                .andRespond(withSuccess("{\"id\": 139, \"name\": \"Girls\", \"language\": \"English\"}",
                        MediaType.APPLICATION_JSON));

        Map<String, Object> show = client.getShowById(139);

        assertThat(show).containsEntry("name", "Girls").containsEntry("language", "English");
    }

    @Test
    void getShowByIdThrowsNotFoundWhenTvMazeReturns404() {
        server.expect(requestTo(BASE_URL + "/shows/999999999"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getShowById(999999999))
                .isInstanceOf(ShowNotFoundException.class);
    }

    @Test
    void serverErrorIsReportedAsTvMazeApiException() {
        server.expect(requestTo(BASE_URL + "/shows/1")).andRespond(withServerError());

        assertThatThrownBy(() -> client.getShowById(1))
                .isInstanceOf(TvMazeApiException.class);
    }
}
