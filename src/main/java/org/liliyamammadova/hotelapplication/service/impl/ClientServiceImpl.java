package org.liliyamammadova.hotelapplication.service.impl;

import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.service.ClientService;
import org.liliyamammadova.jpastarter.repository.BaseRepository;
import org.liliyamammadova.jpastarter.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl extends BaseServiceImpl<Client> implements ClientService {
    public ClientServiceImpl(BaseRepository<Client> baseRepository) {
        super(baseRepository);
    }
}
