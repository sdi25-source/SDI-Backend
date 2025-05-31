package com.sdi.service;

import com.sdi.domain.Client;
import com.sdi.domain.ProductDeployement;
import com.sdi.repository.RequestOfChangeRepository;
import com.sdi.repository.ClientRepository;
import com.sdi.repository.ProductDeployementRepository;
import com.sdi.service.dto.ClientOverview;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientOverviewService {

    private final ClientRepository clientRepository;
    private final ProductDeployementRepository productDeployementRepository;
    private final RequestOfChangeRepository changeRequestRepository;

    public ClientOverviewService(
        ClientRepository clientRepository,
        ProductDeployementRepository productDeployementRepository,
        RequestOfChangeRepository changeRequestRepository
    ) {
        this.clientRepository = clientRepository;
        this.productDeployementRepository = productDeployementRepository;
        this.changeRequestRepository = changeRequestRepository;
    }

    public List<ClientOverview> getClientsOverview() {
        // Use the new method to fetch clients with clientType
        List<Client> clients = clientRepository.findAllWithClientType();

        return clients.stream().map(client -> {
            Long productCount = productDeployementRepository.findByClientId(client.getId())
                .stream()
                .map(ProductDeployement::getProduct)
                .map(product -> product.getId())
                .distinct()
                .count();

            Long requestCount = changeRequestRepository.countByClientId(client.getId());

            Long deploymentCount = productDeployementRepository.countByClientId(client.getId());

            // Safely access clientType
            String clientType = client.getClientType() != null ? client.getClientType().getType() : "Unknown";
            String badgeClass = determineBadgeClass(clientType);
            String icon = determineIcon(client.getName(), clientType);

            return new ClientOverview(
                client.getName(),
                clientType,
                badgeClass,
                icon,
                productCount.intValue(),
                requestCount.intValue(),
                deploymentCount.intValue()
            );
        }).collect(Collectors.toList());
    }

    private String determineBadgeClass(String clientType) {
        if (clientType == null || clientType.equals("Unknown")) {
            return "default";
        }
        switch (clientType.toLowerCase()) {
            case "Banque":
                return "finance";
            case "Assurance":
                return "insurance";
            case "Commerce":
                return "communication";
            case "IT & Technology":
                return "health";
            case "industry":
                return "logistics";
            default:
                return "default";
        }
    }

    private String determineIcon(String clientName, String clientType) {
        String name = clientName != null ? clientName.toLowerCase() : "";
        String type = clientType != null ? clientType.toLowerCase() : "";

        if (name.contains("bank") || type.contains("finance")) {
            return "bi bi-piggy-bank";
        } else if (name.contains("insur") || type.contains("insurance")) {
            return "bi bi-shield";
        } else if (name.contains("security") || type.contains("security")) {
            return "bi bi-lock";
        } else if (name.contains("analytic") || type.contains("analytics")) {
            return "bi bi-bar-chart";
        } else if (name.contains("comm") || type.contains("communication")) {
            return "bi bi-telephone";
        } else if (name.contains("health") || type.contains("health")) {
            return "bi bi-heart-pulse";
        } else if (name.contains("logistic") || type.contains("logistics")) {
            return "bi bi-truck";
        } else {
            return "bi bi-building";
        }
    }
}
