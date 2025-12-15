package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.exceptions.EntityDeactivatedException;
import edu.com.bookingsystem.exceptions.InvalidFieldValueException;
import edu.com.bookingsystem.exceptions.UnauthorizedException;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.EventRepo;
import edu.com.bookingsystem.repos.OrganizationRepo;
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
    private final OrganizationRepo orgRepo;

    private UserAccount getAuthorizedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged User not found"));
    }

    public Event getExistingEventById(UUID eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event by id not found"));
    }
    public List<EventResponseDTO> getList() {
        List<Event> list = eventRepo.findAll();
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public Integer getAvailableSeats(UUID eventId) {
        Event existingEvent = getExistingEventById(eventId);
        int maxParticipants = existingEvent.getMaxParticipants();
        int currentParticipants = existingEvent.getCurrentParticipants();

        return Math.max(0, maxParticipants - currentParticipants);
    }

    public EventResponseDTO createEvent(EventRequestDTO dto, String auth) {
        UserAccount createdBy = getAuthorizedUser(auth);//admin super
        //organization of event gets by managers organization id
        Organization org = orgRepo.findById(createdBy.getOrganization().getId()).orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        EventType type;
        try{
            type = EventType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFieldValueException("Invalid event type value: '" + dto.getType().toUpperCase() + "'");
        }

        Event event = eventMapper.toEntity(dto, type, createdBy, org);
        eventRepo.save(event);
        return eventMapper.toDto(event);
    }

    public EventResponseDTO updateEvent(UUID eventId, EventRequestDTO dto, String auth) {
        UserAccount updatedBy = getAuthorizedUser(auth);//admin super
        Event existingEvent = getExistingEventById(eventId);

        if(!updatedBy.getOrganization().getId().equals(existingEvent.getOrganization().getId())) {
            throw new UnauthorizedException("Event updates are restricted to managers whose organization matches the event's organization.");
        }
        if(!existingEvent.isActive() || existingEvent.getWhen().isBefore(LocalDateTime.now())) {
            throw new EntityDeactivatedException("Event is deactivated or has expired. No need of updating");
        }

        EventType newType;
        try{
            newType = EventType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFieldValueException("Invalid event type value: '" + dto.getType().toUpperCase() + "'");
        }

        existingEvent.setTitle(dto.getTitle());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setLocation(dto.getLocation());
        existingEvent.setWhen(dto.getWhen());
        existingEvent.setType(newType);
        existingEvent.setCreatedBy(updatedBy);
        existingEvent.setMaxParticipants(dto.getMaxParticipants());
        //TODO need to calculate the number of current participants without ruining already existing bookings for the current event
        eventRepo.save(existingEvent);
        return eventMapper.toDto(existingEvent);
    }

    public boolean deleteEvent(UUID eventId, String email) {
        UserAccount authUser = getAuthorizedUser(email);//admin super

        Event existingEvent = getExistingEventById(eventId);
        if(existingEvent.isActive() && !existingEvent.isDeprecated()
        && existingEvent.getWhen().isBefore(LocalDateTime.now())) {
            existingEvent.setCreatedBy(authUser);
            existingEvent.setActive(false);
            existingEvent.setDeprecated(true);
            eventRepo.save(existingEvent);
            return !existingEvent.isActive() && existingEvent.isDeprecated();
        }
        return false;
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
