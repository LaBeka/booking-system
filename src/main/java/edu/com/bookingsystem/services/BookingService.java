package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.BookingResponseDTO;
import edu.com.bookingsystem.exceptions.EntityDeactivatedException;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final BookingMapper bookingMapper;
    private final EventRepo eventRepo;
    private final UserAccountRepo userAccountRepo;


    private UserAccount getAuthorizedUser(String email) {
        return userAccountRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged User not found"));
    }

    private Event getExistingEventById(UUID eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event by id not found"));
    }
    private Optional<Booking> getExistingBookingByUser(UUID userId) {
        return bookingRepo.findByUser(userId);
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
    @Transactional
    public BookingResponseDTO bookEvent(UUID eventId, Principal user) {
        UserAccount userAccount = getAuthorizedUser(user.getName());

        Event event = eventRepo.findByIdForUpdate(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if(event.getMaxParticipants() <= event.getCurrentParticipants()){
            throw new EntityDeactivatedException("No spots are available for booking the event");
        }

        if (event.getWhen().isBefore(LocalDateTime.now()) || !event.isActive()) {
            throw new EntityDeactivatedException("Event is not available for booking");
        }

        Optional<Booking> booking = getExistingBookingByUserAndEvent(userAccount.getId(), event.getId());
        if (booking.isPresent()) {
            throw new EntityExistsException("Booking exists. User already has an event with id " + eventId);
        }
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepo.save(event);// persist updated participant count

        Booking save = bookingMapper.toEntity(event, userAccount);
        bookingRepo.save(save);
        return bookingMapper.toDto(save);
    }

    //only users
    public BookingResponseDTO unBookEvent(UUID bookingId, Principal user) {
        UserAccount userAccount = getAuthorizedUser(user.getName());
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (!booking.isActive()) {
            throw new EntityNotFoundException("Booking is already unbooked");
        }
        if(userAccount.getId() != booking.getBookedBy().getId()) {
            throw new UnauthorizedException("Users can only unbook their own bookings");
        }
        booking.setActive(false);
        bookingRepo.save(booking);
        return bookingMapper.toDto(booking);
    }

    //only admins
    public Boolean deleteBooking(UUID bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking does not exist"));

        bookingRepo.delete(booking);
        return true;
    }

    public List<BookingResponseDTO> getUpcomingBookings(Principal auth) {
        UserAccount user = getAuthorizedUser(auth.getName());
        List<Booking> bookings = bookingRepo.findAllUpcomingByUserId(user.getId(), true, LocalDateTime.now());
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    public List<BookingResponseDTO> getPastBookings(Principal auth) {
        UserAccount user = getAuthorizedUser(auth.getName());
        List<Booking> bookings = bookingRepo.findAllPastByUserId(user.getId(), LocalDateTime.now());
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    public BookingResponseDTO getBookingById(UUID bookingId, Principal auth) {
        UserAccount user = getAuthorizedUser(auth.getName());
       Booking byId = bookingRepo.findById(bookingId)
               .orElseThrow(() -> new EntityNotFoundException("Booking by given id not found"));
        Booking existingBookingByUser = getExistingBookingByUser(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User does not have any booked bookings"));

        if(!existingBookingByUser.getId().equals(byId.getId())) {
            throw new EntityDeactivatedException("Booking by given id does not belong to you.");
        }
        return bookingMapper.toDto(existingBookingByUser);
    }
}
