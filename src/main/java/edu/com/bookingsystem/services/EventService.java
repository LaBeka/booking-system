package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.exceptions.InvalidFieldValueException;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.EventRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserAccountRepo userRepository;
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    private UserAccount getAuthorizedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged User not found"));
    }

    private Event getExistingEventById(UUID eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event by id not found"));
    }
    public List<EventResponseDTO> getList() {
        List<Event> list = eventRepo.findAll();
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public EventResponseDTO createEvent(EventRequestDTO dto, String auth) {
        UserAccount createdBy = getAuthorizedUser(auth);//admin super

        EventType type;
        try{
            type = EventType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFieldValueException("Invalid grade value: '" + dto.getType().toUpperCase() + "'");
        }

        Event event = eventMapper.toEntity(dto, type, createdBy);
        eventRepo.save(event);
        return eventMapper.toDto(event);
    }

    public EventResponseDTO updateEvent(UUID eventId, EventRequestDTO dto, String email) {
        UserAccount updatedBy = getAuthorizedUser(email);//admin super
        Event existingEvent = getExistingEventById(eventId);

        existingEvent.setTitle(dto.getTitle());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setLocation(dto.getLocation());
        existingEvent.setWhen(LocalDateTime.now());
        existingEvent.setCreatedBy(updatedBy);
        existingEvent.setMaxParticipants(dto.getMaxParticipants());
        //TODO need to calculate the numv=ber of current participants without ruining already existing bookings for the current event
        eventRepo.save(existingEvent);
        return eventMapper.toDto(existingEvent);
    }

    public boolean deleteEvent(UUID eventId, String email) {
        UserAccount authUser = getAuthorizedUser(email);//admin super

        Event existingEvent = getExistingEventById(eventId);
        existingEvent.setCreatedBy(authUser);
        existingEvent.setActive(false);
        existingEvent.setDeprecated(true);
        return !existingEvent.isActive() && existingEvent.isDeprecated();
    }

    public List<EventResponseDTO> getUpcomingByLocationList(String city) {
        List<Event> list = eventRepo.findUpcomingListEventsByLocation(city, LocalDateTime.now());
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventResponseDTO> getPastByLocationList(String city) {
        List<Event> list = eventRepo.findPastListEventsByLocation(city, LocalDateTime.now());
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
