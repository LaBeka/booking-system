package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.BookingResponseDTO;
import edu.com.bookingsystem.exceptions.UnauthorizedException;
import edu.com.bookingsystem.mappers.BookingMapper;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.BookingRepo;
import edu.com.bookingsystem.repos.EventRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final BookingMapper bookingMapper;
    private final EventRepo eventRepo;
    private final UserAccountRepo userRepo;


    private UserAccount getAuthorizedUser(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged User not found"));
    }

    private Event getExistingEventById(UUID eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event by id not found"));
    }

    private Optional<Booking> getExistingBookingByUserAndEvent(UUID userId, UUID eventId) {
        return bookingRepo.findByUserAndEvent(userId, eventId);
    }
    //all roles
    public List<BookingResponseDTO> getList(Principal email) {
        UserAccount user = getAuthorizedUser(email.getName());
        List<Booking> list = bookingRepo.findAllByUserId(user.getId());
        return list.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    //only admins TODO check if i can do this? event.getBookings()
    public List<BookingResponseDTO> getListByEventId(UUID eventId) {
        Event event = getExistingEventById(eventId);
        List<Booking> bookings = event.getBookings();
//        List<Booking> list = bookingRepo.getAllByEvent(event.getId());
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    //only users
    public BookingResponseDTO bookEvent(UUID eventId, Principal user) {
        UserAccount userAccount = getAuthorizedUser(user.getName());
        Event event = getExistingEventById(eventId);
        Optional<Booking> booking = getExistingBookingByUserAndEvent(userAccount.getId(), event.getId());
        if (booking.isPresent()) {
            throw new EntityExistsException("Booking exists. User already has an event with id " + eventId);
        }
        Booking save = bookingMapper.toEntity(event, userAccount);
        bookingRepo.save(save);
        return bookingMapper.toDto(save);
    }

    //only users
    public BookingResponseDTO unBookEvent(UUID bookingId, Principal user) {
        UserAccount userAccount = getAuthorizedUser(user.getName());
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (booking.isEmpty()) {
            throw new EntityNotFoundException("Booking does not exist");
        }
        if(userAccount.getId() != booking.get().getBookedBy().getId()) {
            throw new UnauthorizedException("Users can only unbook their own bookings");
        }
        booking.get().setActive(false);
        bookingRepo.save(booking.get());
        return bookingMapper.toDto(booking.get());
    }

    //only admins
    public Boolean deleteBooking(UUID bookingId) {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (booking.isEmpty()) {
            throw new EntityNotFoundException("Booking does not exist");
        }

        bookingRepo.delete(booking.get());
        return true;
    }
}
