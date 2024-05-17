package org.iesvdm.appointment.repository;

import org.iesvdm.appointment.entity.Appointment;
import org.iesvdm.appointment.entity.AppointmentStatus;
import org.iesvdm.appointment.entity.Customer;
import org.iesvdm.appointment.entity.User;
import org.iesvdm.appointment.repository.impl.AppointmentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRepositoryImplTest {

    private Set<Appointment> appointments;

    private AppointmentRepository appointmentRepository;

    @BeforeEach
    public void setup() {
        appointments = new HashSet<>();
        appointmentRepository = new AppointmentRepositoryImpl(appointments);
    }

    /**
     * Crea 2 citas (Appointment) una con id 1 y otra con id 2,
     * resto de valores inventados.
     * Agrégalas a las citas (appointments) con la que
     * construyes el objeto appointmentRepository bajo test.
     * Comprueba que cuando invocas appointmentRepository.getOne con uno
     * de los id's anteriores recuperas obtienes el objeto.
     * Pero si lo invocas con otro id diferente recuperas null
     */
    @Test
    void getOneTest() {
        Appointment appointment1 = new Appointment();
        appointment1.setId(1);
        appointments.add(appointment1);
        Appointment appointment2 = new Appointment();
        appointment2.setId(2);
        appointments.add(appointment2);

        assertEquals(appointment1, appointmentRepository.getOne(1));
        assertEquals(null, appointmentRepository.getOne(3));
    }

    /**
     * Crea 2 citas (Appointment) y guárdalas mediante
     * appointmentRepository.save.
     * Comprueba que la colección appointments
     * contiene sólo esas 2 citas.
     */
    @Test
    void saveTest() {
        Appointment appointment1 = new Appointment();
        appointment1.setId(1);
        appointments.add(appointment1);

        Appointment appointment2 = new Appointment();
        appointment2.setId(2);
        appointments.add(appointment2);

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);


        assertEquals(2, appointments.size());
        assertTrue(appointments.contains(appointment1) && appointments.contains(appointment2));
    }

    /**
     * Crea 2 citas (Appointment) una cancelada por un usuario y otra no,
     * (atención al estado de la cita, lee el código) y agrégalas mediante
     * appointmentRepository.save a la colección de appointments
     * Comprueba que mediante appointmentRepository.findCanceledByUser
     * obtienes la cita cancelada.
     */
    @Test
    void findCanceledByUserTest() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELED);
        User user = new User();
        user.setId(1);
        appointment.setCanceler(user);
        Appointment appointment2 = new Appointment();
        appointment2.setStatus(AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment);
        appointmentRepository.save(appointment2);

        List<Appointment> result = appointmentRepository.findCanceledByUser(1);

        assertTrue(result.contains(appointment));




    }

    /**
     * Crea 3 citas (Appointment), 2 para un mismo cliente (Customer)
     * con sólo una cita de ellas presentando fecha de inicio (start)
     * y fin (end) dentro del periodo de búsqueda (startPeriod,endPeriod).
     * Guárdalas mediante appointmentRepository.save.
     * Comprueba que appointmentRepository.findByCustomerIdWithStartInPeroid
     * encuentra la cita en cuestión.
     * Nota: utiliza LocalDateTime.of(...) para crear los LocalDateTime
     */
    @Test
    void findByCustomerIdWithStartInPeroidTest() {
        Appointment appointment = new Appointment();
        Appointment appointment2 = new Appointment();
        Appointment appointment3 = new Appointment();

        Customer customer = new Customer();
        customer.setId(1);
        appointment.setCustomer(customer);
        appointment.setStart(LocalDateTime.of(2021, 1, 1, 10, 0));
        appointment.setEnd(LocalDateTime.of(2021, 1, 1, 11, 0));
        appointmentRepository.save(appointment);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);

        List<Appointment> result = appointmentRepository.findByCustomerIdWithStartInPeroid(1, LocalDateTime.of(2021, 1, 1, 9, 0), LocalDateTime.of(2021, 1, 1, 12, 0));
        assertTrue(result.contains(appointment));

    }


    /**
     * Crea 2 citas (Appointment) una planificada (SCHEDULED) con tiempo fin
     * anterior a la tiempo buscado por appointmentRepository.findScheduledWithEndBeforeDate
     * guardándolas mediante appointmentRepository.save para la prueba de findScheduledWithEndBeforeDate
     */
    @Test
    void findScheduledWithEndBeforeDateTest() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setEnd(LocalDateTime.of(2021, 1, 1, 10, 0));
        Appointment appointment2 = new Appointment();
        appointment2.setStatus(AppointmentStatus.SCHEDULED);
        appointment2.setEnd(LocalDateTime.of(2021, 1, 1, 11, 0));

        appointmentRepository.save(appointment);
        appointmentRepository.save(appointment2);

        List<Appointment> result = appointmentRepository.findScheduledWithEndBeforeDate(LocalDateTime.of(2021, 1, 1, 11, 0));
        assertTrue(result.contains(appointment));


    }


    /**
     * Crea 3 citas (Appointment) planificadas (SCHEDULED)
     * , 2 para un mismo cliente, con una elegible para cambio (con fecha de inicio, start, adecuada)
     * y otra no.
     * La tercera ha de ser de otro cliente.
     * Guárdalas mediante appointmentRepository.save
     * Comprueba que getEligibleAppointmentsForExchange encuentra la correcta.
     */
    @Test
    void getEligibleAppointmentsForExchangeTest() {
         /* El test esta fallando ,
         Se esperaba encontrar  la primera cita */

        Customer customer = new Customer();
        customer.setId(1);
        Customer customer2 = new Customer();
        customer2.setId(2);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCustomer(customer);
        appointment.setStart(LocalDateTime.of(2021, 1, 1, 10, 0));

        Appointment appointment2 = new Appointment();
        appointment2.setStatus(AppointmentStatus.SCHEDULED);
        appointment2.setCustomer(customer);

        Appointment appointment3 = new Appointment();
        appointment3.setStatus(AppointmentStatus.SCHEDULED);
        appointment3.setCustomer(customer2);

        appointmentRepository.save(appointment);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);

        List<Appointment> result = appointmentRepository.getEligibleAppointmentsForExchange(LocalDateTime.of(2021, 1, 1, 10, 0), 1);

        assertTrue(result.contains(appointment));

    }


    /**
     * Igual que antes, pero ahora las 3 citas tienen que tener
     * clientes diferentes y 2 de ellas con fecha de inicio (start)
     * antes de la especificada en el método de búsqueda para
     * findExchangeRequestedWithStartBefore
     */
    @Test
    void findExchangeRequestedWithStartBeforeTest() {
        LocalDateTime now = LocalDateTime.now();
        Customer customer1 = new Customer();
        customer1.setId(1);

        Customer customer2 = new Customer();
        customer2.setId(2);

        Customer customer3 = new Customer();
        customer3.setId(3);

        Appointment appointment1 = new Appointment();
        appointment1.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);
        appointment1.setStart(LocalDateTime.of(2021, 1, 1, 6, 0));
        appointment1.setCustomer(customer1);

        Appointment appointment2 = new Appointment();
        appointment2.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);
        appointment2.setStart(LocalDateTime.of(2021, 1, 1, 9, 0));
        appointment2.setCustomer(customer2);

        Appointment appointment3 = new Appointment();
        appointment3.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);
        appointment3.setCustomer(customer3);

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);


        List<Appointment> result = appointmentRepository.findExchangeRequestedWithStartBefore(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertTrue(result.contains(appointment1));
        assertTrue(result.contains(appointment2));

    }


}
