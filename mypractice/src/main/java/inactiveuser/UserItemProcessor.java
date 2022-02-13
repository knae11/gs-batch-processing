package inactiveuser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<FileUserDto, User> {

    private static final Logger log = LoggerFactory.getLogger(UserItemProcessor.class);

    @Override
    public User process(FileUserDto item) throws Exception {

        log.info("status enum: " + item.getStatus());
        return new User(item.getId(), item.getName(), item.getStatus(), item.getUpdatedTime());
    }
}
