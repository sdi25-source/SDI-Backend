package com.sdi.service.mapper;


import com.sdi.domain.Client;
import com.sdi.service.dto.ClientDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientMapper {

    public ClientDTO toDto(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientDTO(
            client.getName(),
            client.getMainContactName(),
            client.getMainContactEmail(),
            client.getMainContactPhoneNumber(),
            client.getAddress(),
            client.getCountry() != null ? client.getCountry().getCountryname() : null,
            client.getClientType() != null ? client.getClientType().getType() : null,
            client.getSize() != null ? client.getSize().getSizeName() : null,
            client.getCurrentCustomersNumber(),
            client.getCurrentCardHolderNumber(),
            client.getCurrentBruncheNumber(),
            client.getCreateDate()
        );
    }

    public List<ClientDTO> toDtoList(List<Client> clients) {
        List<ClientDTO> clientDTOs = new ArrayList<>();
        if (clients != null) {
            for (Client client : clients) {
                ClientDTO clientDTO = toDto(client);
                if (clientDTO != null) {
                    clientDTOs.add(clientDTO);
                }
            }
        }
        return clientDTOs;
    }
}
