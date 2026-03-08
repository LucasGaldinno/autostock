package br.com.AutoStock.service;

import br.com.AutoStock.dto.CepResponse;
import br.com.AutoStock.exception.CepInvalidoException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
@RequiredArgsConstructor
public class CepApiService {

    private final RestTemplate restTemplate;
    private static final String CEP_URL = "https://brasilapi.com.br/api/cep/v1/{cep}";

    public CepResponse buscarCep(String cep) {
        try {
            JsonNode node = restTemplate.getForObject(CEP_URL, JsonNode.class, cep);

            CepResponse r = new CepResponse();
            r.setCep(node.path("cep").asText());
            r.setLogradouro(node.path("street").asText(node.path("logradouro").asText()));
            r.setBairro(node.path("neighborhood").asText(node.path("bairro").asText()));
            r.setCidade(node.path("city").asText(node.path("localidade").asText()));
            r.setUf(node.path("state").asText(node.path("uf").asText()));
            return r;

        } catch (HttpClientErrorException.NotFound ex) {
            throw new CepInvalidoException("CEP não encontrado: " + cep);

        } catch (HttpClientErrorException.BadRequest ex) {
            throw new CepInvalidoException("CEP inválido: " + cep);

        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden ex) {
            throw new RuntimeException("Acesso negado à API de CEPs");

        } catch (HttpServerErrorException ex) {
            throw new RuntimeException("Serviço de consulta de CEPs indisponível, tente novamente mais tarde");

        } catch (ResourceAccessException ex) {
            throw new RuntimeException("Falha de conexão com a API de CEPs", ex);

        } catch (RestClientException ex) {
            throw new RuntimeException("Erro ao processar a resposta da API de CEPs", ex);
        }
    }
}
