package br.com.AutoStock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.AutoStock.dto.FipeResponse;
import br.com.AutoStock.exception.FipeCodigoInvalidoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FipeApiService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://brasilapi.com.br/api/fipe/preco/v1/{codigoFipe}";

    public List<FipeResponse> consultarPreco(String codigoFipe) {
        try {
            FipeResponse[] response = restTemplate.getForObject(API_URL, FipeResponse[].class, codigoFipe);
            return response != null ? List.of(response) : List.of();

        } catch (HttpClientErrorException.NotFound ex) {
            // 404 → código FIPE não existe
            throw new FipeCodigoInvalidoException("Código FIPE não encontrado: " + codigoFipe);

        } catch (HttpClientErrorException.BadRequest ex) {
            // 400 → código mal formatado
            throw new FipeCodigoInvalidoException("Código FIPE inválido: " + codigoFipe);

        } catch (HttpServerErrorException ex) {
            // 500/502/503
            throw new RuntimeException("Serviço FIPE indisponível, tente novamente mais tarde");

        } catch (ResourceAccessException ex) {
            // falha de conexão, timeout, DNS
            throw new RuntimeException("Falha de conexão com a API FIPE", ex);

        } catch (RestClientException ex) {
            // parsing de JSON ou outros erros inesperados
            throw new RuntimeException("Erro ao processar resposta da API FIPE", ex);
        }
    }
}
