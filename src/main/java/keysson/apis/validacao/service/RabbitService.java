package keysson.apis.validacao.service;

import keysson.apis.validacao.dto.FuncionarioCadastradoEvent;
import keysson.apis.validacao.dto.MensagensPendentes;
import keysson.apis.validacao.repository.RabbitRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RabbitService {


    private final RabbitRepository rabbitRepository;

    public RabbitService(RabbitRepository rabbitRepository) {
        this.rabbitRepository = rabbitRepository;
    }

    public void saveMessagesInBank(FuncionarioCadastradoEvent event, int status) throws SQLException {
        MensagensPendentes mensagenPendente = new MensagensPendentes();
        mensagenPendente.setIdEmpresa(event.getIdEmpresa());
        mensagenPendente.setName(event.getName());
        mensagenPendente.setEmail(event.getEmail());
        mensagenPendente.setCpf(event.getCpf());
        mensagenPendente.setUsername(event.getUsername());
        mensagenPendente.setStatus(status);

        rabbitRepository.saveMenssage(mensagenPendente);
    }
}



