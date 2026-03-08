package br.com.AutoStock.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.AutoStock.dto.CnpjResponse;
import br.com.AutoStock.exception.CnpjInvalidoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CnpjApiService {

    private final RestTemplate restTemplate;
    private static final String CNPJ_URL = "https://brasilapi.com.br/api/cnpj/v1/{cnpj}";

    public CnpjResponse consultarCnpj(String cnpj) {
        try {
            return restTemplate.getForObject(CNPJ_URL, CnpjResponse.class, cnpj);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new CnpjInvalidoException("CNPJ não encontrado: " + cnpj);

        } catch (HttpClientErrorException.BadRequest ex) {
            throw new CnpjInvalidoException("CNPJ inválido ou inativo: " + cnpj);

        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden ex) {
            throw new RuntimeException("Acesso negado à API de CNPJs");

        } catch (HttpServerErrorException ex) {
            throw new RuntimeException("Serviço de consulta de CNPJs indisponível, tente novamente mais tarde");

        } catch (ResourceAccessException ex) {
            throw new RuntimeException("Falha de conexão com a API de CNPJs", ex);

        } catch (RestClientException ex) {
            throw new RuntimeException("Erro ao processar a resposta da API de CNPJs", ex);
        }
    }
}


