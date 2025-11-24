package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.dtos.BookingResponseDTO;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.user.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, EventMapper.class })
public interface BookingMapper {

    // Create booking from request: we ignore associations (set in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "bookedBy", source = "bookedBy")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "bookedOn", expression = "java(java.time.LocalDateTime.now())")
    Booking toEntity(Event event, UserAccount bookedBy);

    // Return full nested DTOs
    @Mapping(target = "bookedBy", source = "bookedBy")
    @Mapping(target = "event", source = "event")
    BookingResponseDTO toDto(Booking booking);
}
