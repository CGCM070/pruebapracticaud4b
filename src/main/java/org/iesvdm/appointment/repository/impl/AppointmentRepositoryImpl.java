package org.iesvdm.appointment.repository.impl;

import org.iesvdm.appointment.entity.Appointment;
import org.iesvdm.appointment.entity.AppointmentStatus;
import org.iesvdm.appointment.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppointmentRepositoryImpl implements AppointmentRepository {

    private Set<Appointment> appointments = new HashSet<>();

    public AppointmentRepositoryImpl(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public Appointment getOne(int appointmentId) {
        return appointments.stream().filter(appointment -> appointment.getId() == appointmentId)
                .findAny()
                .orElse(null);
    }

    @Override
    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public List<Appointment> findCanceledByUser(int userId) {
        return appointments.stream().filter(appointment -> appointment.getStatus().equals(AppointmentStatus.CANCELED)
                        && appointment.getCanceler() != null
                        && appointment.getCanceler().getId() == userId)
                .toList();
    }


    @Override
    public List<Appointment> findByCustomerIdWithStartInPeroid(int customerId, LocalDateTime startPeroid, LocalDateTime endPeroid) {
        return appointments.stream().filter(appointment -> appointment.getCustomer() != null
                        && appointment.getCustomer().getId() == customerId
                        && appointment.getStart().isAfter(startPeroid)
                        && appointment.getEnd().isBefore(endPeroid))
                .toList();
    }

    @Override
    public List<Appointment> findScheduledWithEndBeforeDate(LocalDateTime now) {
        return appointments.stream().filter(appointment -> appointment.getStatus().equals(AppointmentStatus.SCHEDULED)
                        && appointment.getEnd().isBefore(now))
                .toList();
    }

    @Override
    public List<Appointment> getEligibleAppointmentsForExchange(LocalDateTime start, Integer customerId) {
        return appointments.stream().filter(appointment -> appointment.getStatus().equals(AppointmentStatus.SCHEDULED)
                        && appointment.getCustomer().getId() != customerId
                        && appointment.getStart().isAfter(start))
                .toList();
    }

    @Override
    public List<Appointment> findExchangeRequestedWithStartBefore(LocalDateTime date) {
        return appointments.stream().filter(appointment -> appointment.getStatus().equals(AppointmentStatus.EXCHANGE_REQUESTED)
                        && appointment.getStart().isBefore(date))
                .toList();
    }
}
