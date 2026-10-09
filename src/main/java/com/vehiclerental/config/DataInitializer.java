package com.vehiclerental.config;

import com.vehiclerental.entity.*;
import com.vehiclerental.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing default data for Vehicle Rental Management System...");

        // 1. Create Default Users if not exist
        User admin = userRepository.findByEmail("admin@vehiclerental.com").orElseGet(() -> {
            User newAdmin = User.builder()
                    .name("System Administrator")
                    .email("admin@vehiclerental.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .phone("+91 9876543210")
                    .address("Tech Park, Bangalore, India")
                    .role(Role.ADMIN)
                    .build();
            return userRepository.save(newAdmin);
        });

        User customer = userRepository.findByEmail("customer@vehiclerental.com").orElseGet(() -> {
            User newCustomer = User.builder()
                    .name("Rahul Sharma")
                    .email("customer@vehiclerental.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .phone("+91 9123456780")
                    .address("Indiranagar, Bangalore, India")
                    .role(Role.CUSTOMER)
                    .build();
            return userRepository.save(newCustomer);
        });

        // 2. Create Sample Vehicles if empty
        if (vehicleRepository.count() == 0) {
            List<Vehicle> initialVehicles = Arrays.asList(
                    Vehicle.builder()
                            .vehicleName("Honda City 5th Gen")
                            .vehicleNumber("KA-01-AB-1234")
                            .vehicleType(VehicleType.SEDAN)
                            .brand("Honda")
                            .model("ZX CVT")
                            .fuelType("Petrol")
                            .transmission("Automatic")
                            .seatingCapacity(5)
                            .pricePerDay(2200.0)
                            .location("Bangalore")
                            .description("Premium sedan featuring plush leather upholstery, sunroof, Apple CarPlay, and superb highway mileage.")
                            .imageUrl("https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Toyota Innova Crysta")
                            .vehicleNumber("DL-03-XY-5678")
                            .vehicleType(VehicleType.VAN)
                            .brand("Toyota")
                            .model("2.4 VX")
                            .fuelType("Diesel")
                            .transmission("Manual")
                            .seatingCapacity(7)
                            .pricePerDay(3800.0)
                            .location("Delhi")
                            .description("Spacious 7-seater MPV built for long family road trips with unmatched comfort and powerful diesel performance.")
                            .imageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Hyundai Creta SX(O)")
                            .vehicleNumber("MH-02-CD-9012")
                            .vehicleType(VehicleType.SUV)
                            .brand("Hyundai")
                            .model("SX(O) Turbo")
                            .fuelType("Petrol")
                            .transmission("Automatic")
                            .seatingCapacity(5)
                            .pricePerDay(2800.0)
                            .location("Mumbai")
                            .description("Feature-packed compact SUV with panoramic sunroof, ventilated seats, Bose audio, and advanced driving dynamics.")
                            .imageUrl("https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Tata Nexon EV Empowered")
                            .vehicleNumber("MH-12-EV-3456")
                            .vehicleType(VehicleType.SUV)
                            .brand("Tata")
                            .model("Empowered Plus Long Range")
                            .fuelType("Electric")
                            .transmission("Automatic")
                            .seatingCapacity(5)
                            .pricePerDay(2500.0)
                            .location("Pune")
                            .description("Zero-emission high-tech electric SUV with 450km range, 5-star Bharat NCAP safety rating, and fast charging support.")
                            .imageUrl("https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Royal Enfield Classic 350")
                            .vehicleNumber("GA-07-RE-7890")
                            .vehicleType(VehicleType.BIKE)
                            .brand("Royal Enfield")
                            .model("Reborn Chrome Red")
                            .fuelType("Petrol")
                            .transmission("Manual")
                            .seatingCapacity(2)
                            .pricePerDay(1200.0)
                            .location("Goa")
                            .description("Iconic cruiser bike crafted for scenic coastal rides, boasting classic thumping exhaust and relaxed ergonomics.")
                            .imageUrl("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Yamaha YZF-R15 V4")
                            .vehicleNumber("TS-09-YR-1122")
                            .vehicleType(VehicleType.BIKE)
                            .brand("Yamaha")
                            .model("Racing Blue V4")
                            .fuelType("Petrol")
                            .transmission("Manual")
                            .seatingCapacity(2)
                            .pricePerDay(1400.0)
                            .location("Hyderabad")
                            .description("Track-bred performance sportbike equipped with quickshifter, traction control, upside-down forks, and aggressive styling.")
                            .imageUrl("https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("Mahindra Thar 4x4")
                            .vehicleNumber("HP-01-TH-4444")
                            .vehicleType(VehicleType.SUV)
                            .brand("Mahindra")
                            .model("LX Hard Top Diesel")
                            .fuelType("Diesel")
                            .transmission("Manual")
                            .seatingCapacity(4)
                            .pricePerDay(3600.0)
                            .location("Manali")
                            .description("Legendary off-roader designed to conquer rugged mountain trails, snow, and rough terrain with dedicated 4x4 low range.")
                            .imageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build(),

                    Vehicle.builder()
                            .vehicleName("BMW 3 Series Gran Limousine")
                            .vehicleNumber("KA-05-BM-9999")
                            .vehicleType(VehicleType.LUXURY)
                            .brand("BMW")
                            .model("330Li M Sport")
                            .fuelType("Petrol")
                            .transmission("Automatic")
                            .seatingCapacity(5)
                            .pricePerDay(7900.0)
                            .location("Bangalore")
                            .description("Ultimate luxury sedan featuring extended wheelbase, curved glass digital cockpit, Harman Kardon surround audio, and athletic power.")
                            .imageUrl("https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=800&q=80")
                            .availabilityStatus(AvailabilityStatus.AVAILABLE)
                            .build()
            );

            List<Vehicle> savedVehicles = vehicleRepository.saveAll(initialVehicles);
            log.info("Initialized {} sample vehicles successfully", savedVehicles.size());

            // 3. Seed an initial completed rental and review for demo purposes
            Vehicle firstVehicle = savedVehicles.get(0); // Honda City
            Booking pastBooking = Booking.builder()
                    .user(customer)
                    .vehicle(firstVehicle)
                    .pickupDate(LocalDate.now().minusDays(10))
                    .returnDate(LocalDate.now().minusDays(7))
                    .numberOfDays(3)
                    .totalAmount(6600.0)
                    .bookingStatus(BookingStatus.COMPLETED)
                    .build();
            Booking savedBooking = bookingRepository.save(pastBooking);

            Payment payment = Payment.builder()
                    .booking(savedBooking)
                    .amount(6600.0)
                    .paymentMethod(PaymentMethod.CARD)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId("TXN-SEED7788")
                    .build();
            paymentRepository.save(payment);

            Review review = Review.builder()
                    .user(customer)
                    .vehicle(firstVehicle)
                    .rating(5)
                    .comment("Fantastic experience! The Honda City was clean, well-maintained, and drove like a charm on the highway.")
                    .build();
            reviewRepository.save(review);

            log.info("Initialized sample completed booking and review for demo");
        }

        log.info("Data initialization complete. Admin: admin@vehiclerental.com | Customer: customer@vehiclerental.com");
    }
}
