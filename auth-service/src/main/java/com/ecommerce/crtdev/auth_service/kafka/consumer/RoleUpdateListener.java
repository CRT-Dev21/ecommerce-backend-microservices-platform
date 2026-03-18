package com.ecommerce.crtdev.auth_service.kafka.consumer;

import com.ecommerce.crtdev.auth_service.entity.ProcessedEvent;
import com.ecommerce.crtdev.auth_service.entity.Role;
import com.ecommerce.crtdev.auth_service.entity.User;
import com.ecommerce.crtdev.auth_service.kafka.CloudEvent;
import com.ecommerce.crtdev.auth_service.kafka.EventMetadata;
import com.ecommerce.crtdev.auth_service.kafka.consumer.events.SellerCreated;
import com.ecommerce.crtdev.auth_service.repository.ProcessedEventRepository;
import com.ecommerce.crtdev.auth_service.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleUpdateListener {
    private final UserRepository repository;
    private final ProcessedEventRepository eventRepository;
    private final ObjectMapper mapper;

    public RoleUpdateListener(UserRepository repository, ProcessedEventRepository eventRepository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
        this.eventRepository = eventRepository;
    }

    @KafkaListener(topics = "sellers.events", groupId = "auth-group")
    @Transactional
    public void handleRoleUpdate(String json){
        try {
            CloudEvent<SellerCreated> event = mapper.readValue(json, new TypeReference<CloudEvent<SellerCreated>>() {});

            EventMetadata metadata = event.eventMetadata();
            SellerCreated payload = event.payload();

            if(eventRepository.existsById(metadata.eventId().toString())){
                return;
            }

            User user = repository.findById(Long.valueOf(payload.userId())).orElseThrow(()-> new IllegalStateException("User not found: "+payload.userId()));

            user.getRoles().add(Role.SELLER);

            eventRepository.save(new ProcessedEvent(metadata.eventId().toString()));
        } catch (Exception ex){
            System.err.println("Error processing event: " + ex.getMessage());
        }
    }
}
