package br.com.AutoStock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.AutoStock.dto.UpdateProfileRequest;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public void updateProfile(User user, UpdateProfileRequest dto) {

        user.setNomeFantasia(dto.getNomeFantasia());
        user.setTelefone(dto.getTelefone());
        user.setCep(dto.getCep());
        user.setLogradouro(dto.getLogradouro());
        user.setNumero(dto.getNumero());
        user.setComplemento(dto.getComplemento());
        user.setBairro(dto.getBairro());
        user.setCidade(dto.getCidade());
        user.setUf(dto.getUf());

        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(User user) {
        userRepository.delete(user);
    }
}
