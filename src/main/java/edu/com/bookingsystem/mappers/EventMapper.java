package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", expression = "java(type)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "deprecated", constant = "false")
    Event toEntity(EventRequestDTO dto, EventType type, UserAccount createdBy);

    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "currentParticipants", expression = "java(event.getBookings() == null ? 0 : event.getBookings().size())")
    EventResponseDTO toDto(Event event);
}
