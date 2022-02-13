//package inactiveuser;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Component
//@Profile("!test")
//public class DataLoader implements CommandLineRunner {
//    private UserRepository userRepository;
//
//    public DataLoader(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<User> users = new ArrayList<>();
//        for (int id = 1; id < 31; id++) {
//            users.add(new User((long) id, "옛날사람" + id, UserStatus.ACTIVE,
//                    LocalDateTime.now().minusYears(1).minusMonths(1)));
//        }
//        for (int id = 31; id < 101; id++) {
//            users.add(new User((long) id, "최근사람" + id, UserStatus.ACTIVE, LocalDateTime.now().minusMonths(1)));
//        }
//        System.out.println("0000000000");
//        System.out.println(users);
//        userRepository.saveAll(users);
//    }
//}
