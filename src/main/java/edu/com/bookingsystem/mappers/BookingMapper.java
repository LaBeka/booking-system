package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.dtos.BookingRequestDTO;
import edu.com.bookingsystem.dtos.BookingResponseDTO;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.user.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, EventMapper.class })
public interface BookingMapper {

    // Create booking from request: we ignore associations (set in service)
    @Mapping(target = "bookedBy", source = "bookedBy")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "bookedOn", expression = "java(java.time.LocalDateTime.now())")
    Booking toEntity(BookingRequestDTO dto, UserAccount bookedBy);

    // Return full nested DTOs
    @Mapping(target = "bookedBy", source = "bookedBy")
    @Mapping(target = "event", source = "event")
    BookingResponseDTO toDto(Booking booking);
}
